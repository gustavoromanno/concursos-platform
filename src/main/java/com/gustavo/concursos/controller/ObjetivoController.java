package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.DefinirObjetivoDTO;
import com.gustavo.concursos.dto.ObjetivoDTO;
import com.gustavo.concursos.entity.Concurso;
import com.gustavo.concursos.entity.ConcursoCargo;
import com.gustavo.concursos.entity.ConcursoEtapa;
import com.gustavo.concursos.entity.ObjetivoUsuario;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.ConcursoCargoRepository;
import com.gustavo.concursos.repository.ConcursoRepository;
import com.gustavo.concursos.repository.ObjetivoUsuarioRepository;
import com.gustavo.concursos.repository.ProgressoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/objetivo")
public class ObjetivoController {

    private final ObjetivoUsuarioRepository objetivoRepository;
    private final ConcursoRepository concursoRepository;
    private final ConcursoCargoRepository cargoRepository;
    private final ProgressoRepository progressoRepository;
    private final UsuarioRepository usuarioRepository;

    public ObjetivoController(
            ObjetivoUsuarioRepository objetivoRepository,
            ConcursoRepository concursoRepository,
            ConcursoCargoRepository cargoRepository,
            ProgressoRepository progressoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.objetivoRepository = objetivoRepository;
        this.concursoRepository = concursoRepository;
        this.cargoRepository = cargoRepository;
        this.progressoRepository = progressoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // GET /objetivo — 204 quando o usuário ainda não escolheu um alvo.
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<ObjetivoDTO> meuObjetivo(Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        return objetivoRepository.findByUsuarioId(usuario.getId())
                .map(o -> ResponseEntity.ok(montar(o, usuario.getId())))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @Transactional
    @PutMapping
    public ObjetivoDTO definir(
            @Valid @RequestBody DefinirObjetivoDTO request,
            Authentication authentication
    ) {
        Usuario usuario = usuarioLogado(authentication);

        Concurso concurso = concursoRepository.findById(request.concursoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Concurso nao encontrado"));

        ConcursoCargo cargo = null;
        if (request.cargoId() != null) {
            cargo = cargoRepository.findById(request.cargoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cargo nao encontrado"));
            if (!cargo.getConcurso().getId().equals(concurso.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esse cargo nao pertence a esse concurso");
            }
        }

        ObjetivoUsuario objetivo = objetivoRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    ObjetivoUsuario novo = new ObjetivoUsuario();
                    novo.setUsuario(usuario);
                    return novo;
                });
        objetivo.setConcurso(concurso);
        objetivo.setCargo(cargo);
        objetivoRepository.save(objetivo);

        return montar(objetivo, usuario.getId());
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<Void> remover(Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        objetivoRepository.findByUsuarioId(usuario.getId()).ifPresent(objetivoRepository::delete);
        return ResponseEntity.noContent().build();
    }

    // --- montagem ---

    private ObjetivoDTO montar(ObjetivoUsuario objetivo, Long usuarioId) {
        Concurso c = objetivo.getConcurso();
        ConcursoCargo cargo = objetivo.getCargo();

        ObjetivoDTO.ProgressoDTO progresso = new ObjetivoDTO.ProgressoDTO(0, 0, 0.0, 0, 0, 0, 0);
        List<ObjetivoDTO.DisciplinaProgressoDTO> porDisciplina = List.of();

        if (cargo != null) {
            var linhas = progressoRepository.progressoDoCargo(cargo.getId(), usuarioId);
            progresso = classificar(linhas);
            porDisciplina = agruparPorDisciplina(linhas);
        }

        return new ObjetivoDTO(
                c.getId(), c.getNome(), c.getOrgao(),
                c.getBanca() != null ? c.getBanca().getNome() : null,
                c.getAno(), c.getSituacao(),
                cargo != null ? cargo.getId() : null,
                cargo != null ? cargo.getNome() : null,
                cargo != null ? cargo.getNivel() : null,
                cargo != null ? cargo.getVagas() : c.getVagas(),
                c.getInscricoesAte(),
                progresso,
                porDisciplina,
                proximaEtapa(c)
        );
    }

    /**
     * Classifica cada assunto do conteúdo programático pelo desempenho do
     * usuário. Sem resposta nenhuma, o assunto entra em "vai cair" — é o que
     * ainda nem foi tocado.
     */
    private ObjetivoDTO.ProgressoDTO classificar(List<ProgressoRepository.ProgressoAssunto> linhas) {
        int dominado = 0, atencao = 0, revisar = 0, vaiCair = 0;

        for (var l : linhas) {
            if (l.getRespondidas() == 0) {
                vaiCair++;
                continue;
            }
            double taxa = (double) l.getAcertos() / l.getRespondidas();
            if (taxa >= 0.8) dominado++;
            else if (taxa >= 0.5) atencao++;
            else revisar++;
        }

        int total = linhas.size();
        int iniciados = total - vaiCair;
        double percentual = total == 0 ? 0.0 : Math.round(iniciados * 1000.0 / total) / 10.0;

        return new ObjetivoDTO.ProgressoDTO(total, iniciados, percentual, dominado, atencao, revisar, vaiCair);
    }

    private List<ObjetivoDTO.DisciplinaProgressoDTO> agruparPorDisciplina(
            List<ProgressoRepository.ProgressoAssunto> linhas
    ) {
        // [0] = total de tópicos, [1] = tópicos já iniciados
        Map<String, int[]> acumulado = new LinkedHashMap<>();
        for (var l : linhas) {
            int[] c = acumulado.computeIfAbsent(l.getDisciplina(), k -> new int[2]);
            c[0]++;
            if (l.getRespondidas() > 0) c[1]++;
        }

        List<ObjetivoDTO.DisciplinaProgressoDTO> resultado = new ArrayList<>();
        acumulado.forEach((disciplina, c) -> resultado.add(new ObjetivoDTO.DisciplinaProgressoDTO(
                disciplina, c[0], c[1],
                c[0] == 0 ? 0.0 : Math.round(c[1] * 1000.0 / c[0]) / 10.0
        )));
        return resultado;
    }

    // Primeira etapa ainda não concluída, com a contagem regressiva.
    private ObjetivoDTO.EtapaDTO proximaEtapa(Concurso c) {
        return c.getEtapas().stream()
                .filter(e -> !"CONCLUIDO".equals(e.getStatus()))
                .findFirst()
                .map(this::paraEtapaDTO)
                .orElse(null);
    }

    private ObjetivoDTO.EtapaDTO paraEtapaDTO(ConcursoEtapa e) {
        Integer dias = e.getDataPrevista() == null
                ? null
                : (int) ChronoUnit.DAYS.between(LocalDate.now(), e.getDataPrevista());
        return new ObjetivoDTO.EtapaDTO(e.getNome(), e.getDataPrevista(), e.getStatus(), dias);
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

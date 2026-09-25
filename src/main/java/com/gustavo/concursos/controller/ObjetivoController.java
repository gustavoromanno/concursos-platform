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
import java.util.List;
import java.util.NoSuchElementException;

import static com.gustavo.concursos.service.ObjetivoCalculadora.agruparPorDisciplina;
import static com.gustavo.concursos.service.ObjetivoCalculadora.classificar;

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

package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.CriarSimuladoRequestDTO;
import com.gustavo.concursos.dto.DesempenhoDisciplinaDTO;
import com.gustavo.concursos.dto.QuestaoResponseDTO;
import com.gustavo.concursos.dto.ResponderQuestaoRequestDTO;
import com.gustavo.concursos.dto.ResultadoSimuladoDTO;
import com.gustavo.concursos.dto.SimuladoResponseDTO;
import com.gustavo.concursos.entity.Alternativa;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Resposta;
import com.gustavo.concursos.entity.Simulado;
import com.gustavo.concursos.entity.SimuladoQuestao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.RespostaRepository;
import com.gustavo.concursos.repository.SimuladoQuestaoRepository;
import com.gustavo.concursos.repository.SimuladoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/simulados")
public class SimuladoController {

    @org.springframework.beans.factory.annotation.Value("${pro.simulados-gratis-por-mes:3}")
    private int simuladosGratisPorMes;

    private final SimuladoRepository simuladoRepository;
    private final SimuladoQuestaoRepository simuladoQuestaoRepository;
    private final QuestaoRepository questaoRepository;
    private final RespostaRepository respostaRepository;
    private final UsuarioRepository usuarioRepository;

    public SimuladoController(
            SimuladoRepository simuladoRepository,
            SimuladoQuestaoRepository simuladoQuestaoRepository,
            QuestaoRepository questaoRepository,
            RespostaRepository respostaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.simuladoRepository = simuladoRepository;
        this.simuladoQuestaoRepository = simuladoQuestaoRepository;
        this.questaoRepository = questaoRepository;
        this.respostaRepository = respostaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // POST /simulados
    // Sorteia as questoes e inicia o cronometro na hora da criacao.
    @Transactional
    @PostMapping
    public ResponseEntity<SimuladoResponseDTO> criar(
            @Valid @RequestBody CriarSimuladoRequestDTO request,
            Authentication authentication
    ) {
        Usuario usuario = usuarioLogado(authentication);

        // Plano gratuito: limite de simulados por mes. Pro: ilimitado e com ineditas.
        boolean pro = usuario.ehPro();
        if (!pro) {
            java.time.LocalDateTime inicioDoMes = java.time.LocalDate.now().withDayOfMonth(1).atStartOfDay();
            long feitos = simuladoRepository.countByUsuarioIdAndCriadoEmGreaterThanEqual(usuario.getId(), inicioDoMes);
            if (feitos >= simuladosGratisPorMes) {
                throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED,
                        "No plano gratuito são " + simuladosGratisPorMes
                                + " simulados por mês. Simulados ilimitados são um recurso do plano Pro.");
            }
        }

        List<Questao> sorteadas = questaoRepository.sortear(
                request.disciplinaId(), request.bancaId(), pro, request.quantidade()
        );

        if (sorteadas.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Nenhuma questao encontrada com esses filtros"
            );
        }

        Simulado simulado = new Simulado();
        simulado.setUsuario(usuario);
        simulado.setDuracaoMinutos(request.duracaoMinutos());
        simulado.setCriadoEm(LocalDateTime.now());

        int ordem = 1;
        for (Questao questao : sorteadas) {
            SimuladoQuestao sq = new SimuladoQuestao();
            sq.setSimulado(simulado);
            sq.setQuestao(questao);
            sq.setOrdem(ordem++);
            simulado.getQuestoes().add(sq);
        }

        simuladoRepository.save(simulado);
        return ResponseEntity.status(HttpStatus.CREATED).body(montarResposta(simulado));
    }

    // GET /simulados/{id} — estado atual e tempo restante.
    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<SimuladoResponseDTO> buscar(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(montarResposta(carregar(id, authentication)));
    }

    // POST /simulados/{id}/questoes/{questaoId}/responder
    // Durante o simulado a correcao NAO e devolvida: o usuario so ve o resultado no fim.
    @Transactional
    @PostMapping("/{id}/questoes/{questaoId}/responder")
    public ResponseEntity<SimuladoResponseDTO> responder(
            @PathVariable Long id,
            @PathVariable Long questaoId,
            @Valid @RequestBody ResponderQuestaoRequestDTO request,
            Authentication authentication
    ) {
        Simulado simulado = carregar(id, authentication);

        if (!simulado.aberto()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Simulado encerrado");
        }

        SimuladoQuestao sq = simuladoQuestaoRepository
                .findBySimuladoIdAndQuestaoId(simulado.getId(), questaoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Essa questao nao faz parte deste simulado"
                ));

        if (sq.getResposta() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Questao ja respondida neste simulado");
        }

        Questao questao = sq.getQuestao();
        Alternativa escolhida = questao.getAlternativas().stream()
                .filter(a -> a.getId().equals(request.alternativaId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Essa alternativa nao pertence a essa questao"
                ));

        Resposta resposta = new Resposta();
        resposta.setUsuario(simulado.getUsuario());
        resposta.setQuestao(questao);
        resposta.setAlternativaEscolhida(escolhida);
        resposta.setCorreta(escolhida.isCorreta());
        respostaRepository.save(resposta);

        sq.setResposta(resposta);
        simuladoQuestaoRepository.save(sq);

        return ResponseEntity.ok(montarResposta(simulado));
    }

    // POST /simulados/{id}/finalizar — encerra e devolve o resultado.
    @Transactional
    @PostMapping("/{id}/finalizar")
    public ResponseEntity<ResultadoSimuladoDTO> finalizar(@PathVariable Long id, Authentication authentication) {
        Simulado simulado = carregar(id, authentication);

        if (simulado.getFinalizadoEm() == null) {
            simulado.setFinalizadoEm(LocalDateTime.now());
            simuladoRepository.save(simulado);
        }

        return ResponseEntity.ok(montarResultado(simulado));
    }

    // GET /simulados — historico do usuario.
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<SimuladoResponseDTO>> listar(Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        List<SimuladoResponseDTO> lista = simuladoRepository
                .findByUsuarioIdOrderByCriadoEmDesc(usuario.getId())
                .stream()
                .map(this::montarResposta)
                .toList();
        return ResponseEntity.ok(lista);
    }

    // --- apoio ---

    private Simulado carregar(Long id, Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        // Busca pelo par (id, dono) para nao expor simulado de outro usuario.
        return simuladoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Simulado nao encontrado"));
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }

    private SimuladoResponseDTO montarResposta(Simulado simulado) {
        long restantes = Math.max(0, Duration.between(LocalDateTime.now(), simulado.prazo()).getSeconds());
        if (simulado.getFinalizadoEm() != null) {
            restantes = 0;
        }

        int respondidas = (int) simulado.getQuestoes().stream()
                .filter(sq -> sq.getResposta() != null)
                .count();

        List<QuestaoResponseDTO> questoes = simulado.getQuestoes().stream()
                .map(sq -> QuestaoResponseDTO.fromEntity(sq.getQuestao()))
                .toList();

        return new SimuladoResponseDTO(
                simulado.getId(),
                simulado.getCriadoEm(),
                simulado.prazo(),
                restantes,
                simulado.aberto(),
                simulado.getQuestoes().size(),
                respondidas,
                questoes
        );
    }

    private ResultadoSimuladoDTO montarResultado(Simulado simulado) {
        int total = simulado.getQuestoes().size();
        int acertos = 0;
        int respondidas = 0;

        // Acumula total e acertos por disciplina para a quebra do resultado.
        Map<String, long[]> porDisciplina = new LinkedHashMap<>();

        for (SimuladoQuestao sq : simulado.getQuestoes()) {
            String disciplina = sq.getQuestao().getDisciplina().getNome();
            long[] contador = porDisciplina.computeIfAbsent(disciplina, d -> new long[2]);
            contador[0]++;

            if (sq.getResposta() != null) {
                respondidas++;
                if (sq.getResposta().isCorreta()) {
                    acertos++;
                    contador[1]++;
                }
            }
        }

        List<DesempenhoDisciplinaDTO> quebra = new ArrayList<>();
        porDisciplina.forEach((disciplina, c) ->
                quebra.add(DesempenhoDisciplinaDTO.de(disciplina, c[0], c[1]))
        );

        LocalDateTime fim = simulado.getFinalizadoEm() != null ? simulado.getFinalizadoEm() : LocalDateTime.now();
        long minutos = Duration.between(simulado.getCriadoEm(), fim).toMinutes();

        return new ResultadoSimuladoDTO(
                simulado.getId(),
                total,
                respondidas,
                total - respondidas,
                acertos,
                respondidas - acertos,
                DesempenhoDisciplinaDTO.percentual(acertos, total),
                minutos,
                quebra
        );
    }
}

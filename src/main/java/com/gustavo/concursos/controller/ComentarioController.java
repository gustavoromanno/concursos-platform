package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.ComentarioDTO;
import com.gustavo.concursos.dto.EstatisticaQuestaoDTO;
import com.gustavo.concursos.dto.NovoComentarioDTO;
import com.gustavo.concursos.entity.Alternativa;
import com.gustavo.concursos.entity.Comentario;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.ComentarioRepository;
import com.gustavo.concursos.repository.EstatisticaQuestaoRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class ComentarioController {

    private final ComentarioRepository comentarioRepository;
    private final EstatisticaQuestaoRepository estatisticaRepository;
    private final QuestaoRepository questaoRepository;
    private final UsuarioRepository usuarioRepository;

    public ComentarioController(
            ComentarioRepository comentarioRepository,
            EstatisticaQuestaoRepository estatisticaRepository,
            QuestaoRepository questaoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.comentarioRepository = comentarioRepository;
        this.estatisticaRepository = estatisticaRepository;
        this.questaoRepository = questaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // GET /questoes/{id}/comentarios
    @Transactional(readOnly = true)
    @GetMapping("/questoes/{questaoId}/comentarios")
    public List<ComentarioDTO> listar(@PathVariable Long questaoId, Authentication authentication) {
        Long usuarioId = usuarioLogado(authentication).getId();
        return comentarioRepository.findByQuestaoIdOrderByCriadoEmDesc(questaoId).stream()
                .map(c -> ComentarioDTO.fromEntity(c, usuarioId))
                .toList();
    }

    @Transactional
    @PostMapping("/questoes/{questaoId}/comentarios")
    public ResponseEntity<ComentarioDTO> comentar(
            @PathVariable Long questaoId,
            @Valid @RequestBody NovoComentarioDTO request,
            Authentication authentication
    ) {
        Usuario usuario = usuarioLogado(authentication);
        Questao questao = questaoRepository.findById(questaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questao nao encontrada"));

        Comentario comentario = new Comentario();
        comentario.setUsuario(usuario);
        comentario.setQuestao(questao);
        comentario.setTexto(request.texto().trim());
        comentarioRepository.save(comentario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ComentarioDTO.fromEntity(comentario, usuario.getId()));
    }

    // Apagar so o proprio comentario.
    @Transactional
    @DeleteMapping("/comentarios/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario nao encontrado"));

        if (!comentario.getUsuario().getId().equals(usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Voce so pode apagar os seus comentarios");
        }

        comentarioRepository.delete(comentario);
        return ResponseEntity.noContent().build();
    }

    // GET /questoes/{id}/estatisticas — desempenho de TODOS os usuarios nesta questao.
    @Transactional(readOnly = true)
    @GetMapping("/questoes/{questaoId}/estatisticas")
    public EstatisticaQuestaoDTO estatisticas(@PathVariable Long questaoId) {
        Questao questao = questaoRepository.findById(questaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questao nao encontrada"));

        long total = estatisticaRepository.totalRespostas(questaoId);
        long acertos = estatisticaRepository.totalAcertos(questaoId);

        Map<Long, Long> contagem = new HashMap<>();
        estatisticaRepository.distribuicao(questaoId)
                .forEach(d -> contagem.put(d.getAlternativaId(), d.getTotal()));

        // Percorre as alternativas na ordem da questao para gerar as letras.
        List<EstatisticaQuestaoDTO.FatiaAlternativa> fatias = new ArrayList<>();
        List<Alternativa> alternativas = questao.getAlternativas();
        for (int i = 0; i < alternativas.size(); i++) {
            Alternativa a = alternativas.get(i);
            long marcacoes = contagem.getOrDefault(a.getId(), 0L);
            fatias.add(new EstatisticaQuestaoDTO.FatiaAlternativa(
                    a.getId(),
                    String.valueOf((char) ('A' + i)),
                    marcacoes,
                    total == 0 ? 0.0 : Math.round(marcacoes * 1000.0 / total) / 10.0,
                    a.isCorreta()
            ));
        }

        double percentual = total == 0 ? 0.0 : Math.round(acertos * 1000.0 / total) / 10.0;

        return new EstatisticaQuestaoDTO(total, acertos, percentual, dificuldade(total, percentual), fatias);
    }

    // Classificacao pela taxa de acerto da comunidade. Abaixo de 5 respostas
    // a amostra e pequena demais para rotular.
    private String dificuldade(long total, double percentualAcerto) {
        if (total < 5) return "Sem dados suficientes";
        if (percentualAcerto >= 80) return "Fácil";
        if (percentualAcerto >= 60) return "Média";
        if (percentualAcerto >= 40) return "Difícil";
        return "Muito difícil";
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

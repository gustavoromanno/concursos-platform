package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.AnotacaoDTO;
import com.gustavo.concursos.entity.Anotacao;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.AnotacaoRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

// Anotacoes pessoais. Tudo e sempre do usuario logado: nao ha como
// ler ou alterar a anotacao de outra pessoa por nenhum destes endpoints.
@RestController
public class AnotacaoController {

    private final AnotacaoRepository anotacaoRepository;
    private final QuestaoRepository questaoRepository;
    private final UsuarioRepository usuarioRepository;

    public AnotacaoController(
            AnotacaoRepository anotacaoRepository,
            QuestaoRepository questaoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.anotacaoRepository = anotacaoRepository;
        this.questaoRepository = questaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // GET /anotacoes/ids — questoes em que o usuario escreveu algo.
    @Transactional(readOnly = true)
    @GetMapping("/anotacoes/ids")
    public List<Long> ids(Authentication authentication) {
        return anotacaoRepository.questaoIdsDoUsuario(usuarioLogado(authentication).getId());
    }

    // GET /questoes/{id}/anotacao — 204 quando ainda nao ha anotacao.
    @Transactional(readOnly = true)
    @GetMapping("/questoes/{questaoId}/anotacao")
    public ResponseEntity<AnotacaoDTO.Resposta> ler(@PathVariable Long questaoId, Authentication authentication) {
        Long usuarioId = usuarioLogado(authentication).getId();
        return anotacaoRepository.findByUsuarioIdAndQuestaoId(usuarioId, questaoId)
                .map(a -> ResponseEntity.ok(new AnotacaoDTO.Resposta(a.getTexto(), a.getAtualizadoEm())))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    // PUT /questoes/{id}/anotacao — cria ou atualiza. Texto em branco apaga.
    @Transactional
    @PutMapping("/questoes/{questaoId}/anotacao")
    public ResponseEntity<AnotacaoDTO.Resposta> salvar(
            @PathVariable Long questaoId,
            @Valid @RequestBody AnotacaoDTO.Salvar request,
            Authentication authentication
    ) {
        Usuario usuario = usuarioLogado(authentication);
        var existente = anotacaoRepository.findByUsuarioIdAndQuestaoId(usuario.getId(), questaoId);

        if (request.texto().isBlank()) {
            existente.ifPresent(anotacaoRepository::delete);
            return ResponseEntity.noContent().build();
        }

        Anotacao anotacao = existente.orElseGet(() -> {
            Questao questao = questaoRepository.findById(questaoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questao nao encontrada"));
            Anotacao nova = new Anotacao();
            nova.setUsuario(usuario);
            nova.setQuestao(questao);
            return nova;
        });
        anotacao.setTexto(request.texto().strip());
        anotacao.setAtualizadoEm(LocalDateTime.now());
        anotacaoRepository.save(anotacao);

        return ResponseEntity.ok(new AnotacaoDTO.Resposta(anotacao.getTexto(), anotacao.getAtualizadoEm()));
    }

    @Transactional
    @DeleteMapping("/questoes/{questaoId}/anotacao")
    public ResponseEntity<Void> apagar(@PathVariable Long questaoId, Authentication authentication) {
        Long usuarioId = usuarioLogado(authentication).getId();
        anotacaoRepository.findByUsuarioIdAndQuestaoId(usuarioId, questaoId).ifPresent(anotacaoRepository::delete);
        return ResponseEntity.noContent().build();
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

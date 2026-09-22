package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.ResponderQuestaoRequestDTO;
import com.gustavo.concursos.dto.RespostaResultDTO;
import com.gustavo.concursos.entity.Alternativa;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Resposta;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.RespostaRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/questoes")
public class RespostaController {

    private final QuestaoRepository questaoRepository;
    private final RespostaRepository respostaRepository;
    private final UsuarioRepository usuarioRepository;

    public RespostaController(
            QuestaoRepository questaoRepository,
            RespostaRepository respostaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.questaoRepository = questaoRepository;
        this.respostaRepository = respostaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // @Transactional mantem a sessao do banco aberta durante todo o metodo.
    // Sem isso, questao.getAlternativas() (que e LAZY) estoura
    // LazyInitializationException, porque a conexao ja teria sido fechada.
    @Transactional
    @PostMapping("/{id}/responder")
    public ResponseEntity<RespostaResultDTO> responder(
            @PathVariable Long id,
            @Valid @RequestBody ResponderQuestaoRequestDTO request,
            Authentication authentication
    ) {
        Questao questao = questaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questao nao encontrada"));

        Alternativa alternativaEscolhida = questao.getAlternativas().stream()
                .filter(a -> a.getId().equals(request.alternativaId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Essa alternativa nao pertence a essa questao"
                ));

        // O email do usuario logado vem do token JWT (ver JwtAuthenticationFilter).
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));

        boolean correta = alternativaEscolhida.isCorreta();

        Resposta resposta = new Resposta();
        resposta.setUsuario(usuario);
        resposta.setQuestao(questao);
        resposta.setAlternativaEscolhida(alternativaEscolhida);
        resposta.setCorreta(correta);
        respostaRepository.save(resposta);

        Long alternativaCorretaId = questao.getAlternativas().stream()
                .filter(Alternativa::isCorreta)
                .map(Alternativa::getId)
                .findFirst()
                .orElse(null);

        return ResponseEntity.ok(new RespostaResultDTO(correta, alternativaCorretaId, questao.getExplicacao()));
    }
}

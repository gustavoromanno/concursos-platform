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
import com.gustavo.concursos.service.RevisaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/questoes")
public class RespostaController {

    private final QuestaoRepository questaoRepository;
    private final RespostaRepository respostaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RevisaoService revisaoService;

    private final com.gustavo.concursos.pro.AcessoPro acessoPro;

    public RespostaController(
            QuestaoRepository questaoRepository,
            RespostaRepository respostaRepository,
            UsuarioRepository usuarioRepository,
            RevisaoService revisaoService,
            com.gustavo.concursos.pro.AcessoPro acessoPro
    ) {
        this.questaoRepository = questaoRepository;
        this.respostaRepository = respostaRepository;
        this.usuarioRepository = usuarioRepository;
        this.revisaoService = revisaoService;
        this.acessoPro = acessoPro;
    }

    @Transactional
    @PostMapping("/{id}/responder")
    public ResponseEntity<RespostaResultDTO> responder(
            @PathVariable Long id,
            @Valid @RequestBody ResponderQuestaoRequestDTO request,
            Authentication authentication
    ) {
        Questao questao = questaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questao nao encontrada"));
        if (Questao.ORIGEM_IA.equals(questao.getOrigem()) && !acessoPro.ehPro(authentication)) {
            throw com.gustavo.concursos.pro.AcessoPro.bloqueio("As questões inéditas das provas");
        }

        Alternativa alternativaEscolhida = questao.getAlternativas().stream()
                .filter(a -> a.getId().equals(request.alternativaId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Essa alternativa nao pertence a essa questao"
                ));

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));

        boolean correta = alternativaEscolhida.isCorreta();

        Resposta resposta = new Resposta();
        resposta.setUsuario(usuario);
        resposta.setQuestao(questao);
        resposta.setAlternativaEscolhida(alternativaEscolhida);
        resposta.setCorreta(correta);
        respostaRepository.save(resposta);

        // Reagenda a questao conforme o resultado (repeticao espacada).
        revisaoService.registrar(usuario, questao, correta);

        Long alternativaCorretaId = questao.getAlternativas().stream()
                .filter(Alternativa::isCorreta)
                .map(Alternativa::getId)
                .findFirst()
                .orElse(null);

        return ResponseEntity.ok(new RespostaResultDTO(correta, alternativaCorretaId, questao.getExplicacao()));
    }
}

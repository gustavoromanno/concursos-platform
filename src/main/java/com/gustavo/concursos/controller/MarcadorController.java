package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.AdicionarQuestaoDTO;
import com.gustavo.concursos.dto.QuestaoResponseDTO;
import com.gustavo.concursos.entity.Marcador;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.MarcadorRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/marcadores")
public class MarcadorController {

    private final MarcadorRepository marcadorRepository;
    private final QuestaoRepository questaoRepository;
    private final UsuarioRepository usuarioRepository;

    public MarcadorController(
            MarcadorRepository marcadorRepository,
            QuestaoRepository questaoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.marcadorRepository = marcadorRepository;
        this.questaoRepository = questaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // GET /marcadores — questoes marcadas, da mais recente para a mais antiga.
    @Transactional(readOnly = true)
    @GetMapping
    public List<QuestaoResponseDTO> listar(Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        return marcadorRepository.findByUsuarioIdOrderByCriadoEmDesc(usuario.getId())
                .stream()
                .map(m -> QuestaoResponseDTO.fromEntity(m.getQuestao()))
                .toList();
    }

    // GET /marcadores/ids — so os ids, para o frontend pintar o icone
    // das questoes ja marcadas sem baixar tudo de novo.
    @Transactional(readOnly = true)
    @GetMapping("/ids")
    public List<Long> ids(Authentication authentication) {
        return marcadorRepository.idsMarcados(usuarioLogado(authentication).getId());
    }

    // POST /marcadores — alterna: marca se nao estiver marcada, desmarca se estiver.
    // Um endpoint so evita o frontend ter que saber o estado antes de agir.
    @Transactional
    @PostMapping
    public ResponseEntity<MarcadorStatus> alternar(
            @Valid @RequestBody AdicionarQuestaoDTO request,
            Authentication authentication
    ) {
        Usuario usuario = usuarioLogado(authentication);

        var existente = marcadorRepository.findByUsuarioIdAndQuestaoId(usuario.getId(), request.questaoId());
        if (existente.isPresent()) {
            marcadorRepository.delete(existente.get());
            return ResponseEntity.ok(new MarcadorStatus(request.questaoId(), false));
        }

        Questao questao = questaoRepository.findById(request.questaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questao nao encontrada"));

        Marcador marcador = new Marcador();
        marcador.setUsuario(usuario);
        marcador.setQuestao(questao);
        marcadorRepository.save(marcador);

        return ResponseEntity.ok(new MarcadorStatus(request.questaoId(), true));
    }

    public record MarcadorStatus(Long questaoId, boolean marcada) {}

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

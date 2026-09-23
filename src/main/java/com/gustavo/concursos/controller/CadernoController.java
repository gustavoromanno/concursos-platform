package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.AdicionarQuestaoDTO;
import com.gustavo.concursos.dto.CadernoDTO;
import com.gustavo.concursos.dto.NovoCadernoDTO;
import com.gustavo.concursos.dto.QuestaoResponseDTO;
import com.gustavo.concursos.entity.Caderno;
import com.gustavo.concursos.entity.CadernoQuestao;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.CadernoQuestaoRepository;
import com.gustavo.concursos.repository.CadernoRepository;
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
@RequestMapping("/cadernos")
public class CadernoController {

    private final CadernoRepository cadernoRepository;
    private final CadernoQuestaoRepository cadernoQuestaoRepository;
    private final QuestaoRepository questaoRepository;
    private final UsuarioRepository usuarioRepository;

    public CadernoController(
            CadernoRepository cadernoRepository,
            CadernoQuestaoRepository cadernoQuestaoRepository,
            QuestaoRepository questaoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.cadernoRepository = cadernoRepository;
        this.cadernoQuestaoRepository = cadernoQuestaoRepository;
        this.questaoRepository = questaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // GET /cadernos — lista resumida (nome + quantidade), sem carregar as questoes.
    @Transactional(readOnly = true)
    @GetMapping
    public List<CadernoDTO> listar(Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        return cadernoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuario.getId())
                .stream()
                .map(c -> CadernoDTO.resumo(
                        c.getId(), c.getNome(), c.getCriadoEm(),
                        cadernoQuestaoRepository.countByCadernoId(c.getId())))
                .toList();
    }

    // GET /cadernos/{id} — caderno aberto, com as questoes dentro.
    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public CadernoDTO abrir(@PathVariable Long id, Authentication authentication) {
        Caderno caderno = carregar(id, authentication);

        List<QuestaoResponseDTO> questoes = caderno.getItens().stream()
                .map(item -> QuestaoResponseDTO.fromEntity(item.getQuestao()))
                .toList();

        return new CadernoDTO(
                caderno.getId(), caderno.getNome(), caderno.getCriadoEm(),
                questoes.size(), questoes
        );
    }

    @Transactional
    @PostMapping
    public ResponseEntity<CadernoDTO> criar(
            @Valid @RequestBody NovoCadernoDTO request,
            Authentication authentication
    ) {
        Usuario usuario = usuarioLogado(authentication);

        if (cadernoRepository.existsByUsuarioIdAndNomeIgnoreCase(usuario.getId(), request.nome())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Voce ja tem um caderno com esse nome");
        }

        Caderno caderno = new Caderno();
        caderno.setUsuario(usuario);
        caderno.setNome(request.nome().trim());
        cadernoRepository.save(caderno);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                CadernoDTO.resumo(caderno.getId(), caderno.getNome(), caderno.getCriadoEm(), 0)
        );
    }

    @Transactional
    @PatchMapping("/{id}")
    public CadernoDTO renomear(
            @PathVariable Long id,
            @Valid @RequestBody NovoCadernoDTO request,
            Authentication authentication
    ) {
        Caderno caderno = carregar(id, authentication);
        caderno.setNome(request.nome().trim());
        cadernoRepository.save(caderno);
        return CadernoDTO.resumo(
                caderno.getId(), caderno.getNome(), caderno.getCriadoEm(),
                cadernoQuestaoRepository.countByCadernoId(caderno.getId())
        );
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id, Authentication authentication) {
        cadernoRepository.delete(carregar(id, authentication));
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/{id}/questoes")
    public ResponseEntity<Void> adicionarQuestao(
            @PathVariable Long id,
            @Valid @RequestBody AdicionarQuestaoDTO request,
            Authentication authentication
    ) {
        Caderno caderno = carregar(id, authentication);

        if (cadernoQuestaoRepository.existsByCadernoIdAndQuestaoId(caderno.getId(), request.questaoId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Questao ja esta nesse caderno");
        }

        Questao questao = questaoRepository.findById(request.questaoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questao nao encontrada"));

        CadernoQuestao item = new CadernoQuestao();
        item.setCaderno(caderno);
        item.setQuestao(questao);
        cadernoQuestaoRepository.save(item);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    @DeleteMapping("/{id}/questoes/{questaoId}")
    public ResponseEntity<Void> removerQuestao(
            @PathVariable Long id,
            @PathVariable Long questaoId,
            Authentication authentication
    ) {
        Caderno caderno = carregar(id, authentication);
        cadernoQuestaoRepository.findByCadernoIdAndQuestaoId(caderno.getId(), questaoId)
                .ifPresent(cadernoQuestaoRepository::delete);
        return ResponseEntity.noContent().build();
    }

    private Caderno carregar(Long id, Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        return cadernoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caderno nao encontrado"));
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

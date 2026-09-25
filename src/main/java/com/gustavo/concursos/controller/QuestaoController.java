package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.QuestaoResponseDTO;
import com.gustavo.concursos.entity.Orgao;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.OrgaoRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import com.gustavo.concursos.specification.QuestaoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class QuestaoController {

    private final QuestaoRepository questaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrgaoRepository orgaoRepository;

    public QuestaoController(
            QuestaoRepository questaoRepository,
            UsuarioRepository usuarioRepository,
            OrgaoRepository orgaoRepository
    ) {
        this.questaoRepository = questaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.orgaoRepository = orgaoRepository;
    }

    // Filtros opcionais e combináveis: disciplina, assunto, banca, órgão,
    // ano e tipo (MULTIPLA_ESCOLHA ou CERTO_ERRADO).
    @Transactional(readOnly = true)
    @GetMapping("/questoes")
    public Page<QuestaoResponseDTO> listar(
            @RequestParam(required = false) Long disciplinaId,
            @RequestParam(required = false) Long bancaId,
            @RequestParam(required = false) Long orgaoId,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String assunto,
            @RequestParam(required = false) Long assuntoId,
            @RequestParam(required = false) String tipo,
            Pageable pageable
    ) {
        Specification<Questao> filtro = Specification
                .where(QuestaoSpecification.disciplinaId(disciplinaId))
                .and(QuestaoSpecification.bancaId(bancaId))
                .and(QuestaoSpecification.orgaoId(orgaoId))
                .and(QuestaoSpecification.ano(ano))
                .and(QuestaoSpecification.assunto(assunto))
                .and(QuestaoSpecification.assuntoId(assuntoId))
                .and(QuestaoSpecification.tipo(tipo));

        return questaoRepository.findAll(filtro, pageable).map(QuestaoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    @GetMapping("/questoes/erradas")
    public List<QuestaoResponseDTO> erradas(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));

        return questaoRepository.questoesErradas(usuario.getId()).stream()
                .map(QuestaoResponseDTO::fromEntity)
                .toList();
    }

    // Alimenta o filtro de órgão na interface.
    @GetMapping("/orgaos")
    public List<Orgao> orgaos() {
        return orgaoRepository.findAllByOrderByNomeAsc();
    }
}

package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.QuestaoResponseDTO;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.specification.QuestaoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questoes")
public class QuestaoController {

    private final QuestaoRepository questaoRepository;

    public QuestaoController(QuestaoRepository questaoRepository) {
        this.questaoRepository = questaoRepository;
    }

    // Exemplos:
    //   GET /questoes?disciplinaId=3
    //   GET /questoes?disciplinaId=3&bancaId=1&ano=2013
    //   GET /questoes?assunto=morfologia&page=0&size=20
    // Todos os filtros sao opcionais e combinaveis entre si.
    @GetMapping
    public Page<QuestaoResponseDTO> listar(
            @RequestParam(required = false) Long disciplinaId,
            @RequestParam(required = false) Long bancaId,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String assunto,
            Pageable pageable
    ) {
        Specification<Questao> filtro = Specification
                .where(QuestaoSpecification.disciplinaId(disciplinaId))
                .and(QuestaoSpecification.bancaId(bancaId))
                .and(QuestaoSpecification.ano(ano))
                .and(QuestaoSpecification.assunto(assunto));

        return questaoRepository.findAll(filtro, pageable)
                .map(QuestaoResponseDTO::fromEntity);
    }
}

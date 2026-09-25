package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.NovaQuestaoDTO;
import com.gustavo.concursos.dto.QuestaoResponseDTO;
import com.gustavo.concursos.entity.*;
import com.gustavo.concursos.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

/**
 * Cadastro de questões (restrito a ADMIN pela configuração de segurança).
 *
 * Fica separado do QuestaoController, que só lê, para manter cada classe
 * com uma responsabilidade.
 */
@RestController
@RequestMapping("/questoes")
public class QuestaoAdminController {

    private final QuestaoRepository questaoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final BancaRepository bancaRepository;
    private final OrgaoRepository orgaoRepository;
    private final AssuntoRepository assuntoRepository;

    public QuestaoAdminController(
            QuestaoRepository questaoRepository,
            DisciplinaRepository disciplinaRepository,
            BancaRepository bancaRepository,
            OrgaoRepository orgaoRepository,
            AssuntoRepository assuntoRepository
    ) {
        this.questaoRepository = questaoRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.bancaRepository = bancaRepository;
        this.orgaoRepository = orgaoRepository;
        this.assuntoRepository = assuntoRepository;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<QuestaoResponseDTO> criar(@Valid @RequestBody NovaQuestaoDTO request) {
        String tipo = (request.tipo() == null || request.tipo().isBlank())
                ? Questao.MULTIPLA_ESCOLHA
                : request.tipo().trim().toUpperCase();

        if (!Questao.MULTIPLA_ESCOLHA.equals(tipo) && !Questao.CERTO_ERRADO.equals(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo deve ser MULTIPLA_ESCOLHA ou CERTO_ERRADO");
        }

        Questao questao = new Questao();
        questao.setEnunciado(request.enunciado().trim());
        questao.setAno(request.ano());
        questao.setTipo(tipo);
        questao.setExplicacao(request.explicacao());

        questao.setDisciplina(disciplinaRepository.findById(request.disciplinaId())
                .orElseThrow(() -> erro("Disciplina nao encontrada")));
        questao.setBanca(bancaRepository.findById(request.bancaId())
                .orElseThrow(() -> erro("Banca nao encontrada")));

        if (request.orgaoId() != null) {
            questao.setOrgao(orgaoRepository.findById(request.orgaoId())
                    .orElseThrow(() -> erro("Orgao nao encontrado")));
        }
        if (request.assuntoId() != null) {
            Assunto assunto = assuntoRepository.findById(request.assuntoId())
                    .orElseThrow(() -> erro("Assunto nao encontrado"));
            questao.setAssuntoRef(assunto);
            questao.setAssunto(assunto.getNome());
        }

        if (Questao.CERTO_ERRADO.equals(tipo)) {
            montarCertoErrado(questao, request.gabaritoCerto());
        } else {
            montarMultiplaEscolha(questao, request.alternativas());
        }

        questaoRepository.save(questao);
        return ResponseEntity.status(HttpStatus.CREATED).body(QuestaoResponseDTO.fromEntity(questao));
    }

    /**
     * Em Certo/Errado o sistema cria as duas alternativas. Guardá-las como
     * alternativas normais faz com que respostas, estatísticas, simulados e
     * revisão espaçada funcionem sem nenhum tratamento especial.
     */
    private void montarCertoErrado(Questao questao, Boolean gabaritoCerto) {
        if (gabaritoCerto == null) {
            throw erro("Em CERTO_ERRADO informe gabaritoCerto (true ou false)");
        }
        questao.getAlternativas().add(alternativa(questao, "Certo", gabaritoCerto, 1));
        questao.getAlternativas().add(alternativa(questao, "Errado", !gabaritoCerto, 2));
    }

    private void montarMultiplaEscolha(Questao questao, List<NovaQuestaoDTO.AlternativaEntradaDTO> entradas) {
        if (entradas == null || entradas.size() < 2) {
            throw erro("Informe ao menos duas alternativas");
        }

        long corretas = entradas.stream().filter(a -> Boolean.TRUE.equals(a.correta())).count();
        if (corretas != 1) {
            throw erro("A questao precisa ter exatamente uma alternativa correta");
        }

        // Embaralha para a correta não cair sempre na mesma posição.
        List<NovaQuestaoDTO.AlternativaEntradaDTO> baralho = new java.util.ArrayList<>(entradas);
        Collections.shuffle(baralho);

        int ordem = 1;
        for (var entrada : baralho) {
            if (entrada.texto() == null || entrada.texto().isBlank()) {
                throw erro("Alternativa sem texto");
            }
            questao.getAlternativas().add(alternativa(
                    questao, entrada.texto().trim(), Boolean.TRUE.equals(entrada.correta()), ordem++));
        }
    }

    private Alternativa alternativa(Questao questao, String texto, boolean correta, int ordem) {
        Alternativa a = new Alternativa();
        a.setQuestao(questao);
        a.setTexto(texto);
        a.setCorreta(correta);
        a.setOrdem(ordem);
        return a;
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        questaoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}

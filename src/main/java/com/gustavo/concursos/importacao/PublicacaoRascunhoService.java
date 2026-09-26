package com.gustavo.concursos.importacao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavo.concursos.entity.Alternativa;
import com.gustavo.concursos.entity.Assunto;
import com.gustavo.concursos.entity.CargoDisciplina;
import com.gustavo.concursos.entity.ConcursoCargo;
import com.gustavo.concursos.entity.Disciplina;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.repository.AssuntoRepository;
import com.gustavo.concursos.repository.DisciplinaRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforma rascunhos aprovados em questoes publicas, ligadas ao concurso e
 * ao cargo da prova de origem. Disciplina e assunto sao reaproveitados pelo
 * nome, ou criados; e o conteudo programatico do cargo passa a incluir a
 * disciplina (assim o "objetivo de estudo" enxerga as questoes novas).
 */
@Service
public class PublicacaoRascunhoService {

    private final QuestaoRascunhoRepository rascunhoRepository;
    private final QuestaoRepository questaoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final AssuntoRepository assuntoRepository;
    private final EntityManager em;
    private final ObjectMapper json;

    public PublicacaoRascunhoService(
            QuestaoRascunhoRepository rascunhoRepository,
            QuestaoRepository questaoRepository,
            DisciplinaRepository disciplinaRepository,
            AssuntoRepository assuntoRepository,
            EntityManager em,
            ObjectMapper json
    ) {
        this.rascunhoRepository = rascunhoRepository;
        this.questaoRepository = questaoRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.assuntoRepository = assuntoRepository;
        this.em = em;
        this.json = json;
    }

    @Transactional
    public Questao aprovar(Long rascunhoId) {
        QuestaoRascunho r = rascunhoRepository.findById(rascunhoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rascunho nao encontrado"));
        if (!QuestaoRascunho.PENDENTE.equals(r.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este rascunho ja foi " + r.getStatus().toLowerCase());
        }
        return publicar(r);
    }

    @Transactional
    public int aprovarTodos(Long importacaoId) {
        List<QuestaoRascunho> pendentes =
                rascunhoRepository.findByImportacaoIdAndStatusOrderByIdAsc(importacaoId, QuestaoRascunho.PENDENTE);
        pendentes.forEach(this::publicar);
        return pendentes.size();
    }

    @Transactional
    public void descartar(Long rascunhoId) {
        QuestaoRascunho r = rascunhoRepository.findById(rascunhoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rascunho nao encontrado"));
        if (!QuestaoRascunho.PENDENTE.equals(r.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este rascunho ja foi " + r.getStatus().toLowerCase());
        }
        r.setStatus(QuestaoRascunho.DESCARTADA);
    }

    private Questao publicar(QuestaoRascunho r) {
        ImportacaoProva imp = r.getImportacao();
        if (imp.getConcurso() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A importacao ainda nao tem concurso vinculado");
        }

        Disciplina disciplina = disciplinaRepository.findFirstByNomeIgnoreCase(r.getDisciplina())
                .orElseGet(() -> {
                    Disciplina d = new Disciplina();
                    d.setNome(r.getDisciplina());
                    return disciplinaRepository.save(d);
                });

        Assunto assunto = null;
        if (r.getAssunto() != null && !r.getAssunto().isBlank()) {
            assunto = assuntoRepository.findFirstByDisciplinaIdAndNomeIgnoreCase(disciplina.getId(), r.getAssunto())
                    .orElseGet(() -> {
                        Assunto a = new Assunto();
                        a.setDisciplina(disciplina);
                        a.setNome(r.getAssunto());
                        return assuntoRepository.save(a);
                    });
        }

        Questao q = new Questao();
        q.setEnunciado(r.getEnunciado());
        q.setExplicacao(r.getExplicacao());
        q.setTipo(r.getTipo());
        q.setDisciplina(disciplina);
        q.setBanca(imp.getConcurso().getBanca() != null ? imp.getConcurso().getBanca() : bancaPadrao());
        q.setAno(imp.getConcurso().getAno());
        q.setConcurso(imp.getConcurso());
        q.setCargo(imp.getCargo());
        q.setOrigem(Questao.ORIGEM_IA);
        if (assunto != null) {
            q.setAssuntoRef(assunto);
            q.setAssunto(assunto.getNome());
        }
        montarAlternativas(q, lerAlternativas(r.getAlternativas()), r.getId());
        questaoRepository.save(q);

        if (imp.getCargo() != null) garantirNoConteudo(imp.getCargo(), disciplina);

        r.setStatus(QuestaoRascunho.APROVADA);
        r.setQuestao(q);
        return q;
    }

    // Certo/Errado sempre "Certo" (1) e "Errado" (2). Na multipla escolha a
    // correta muda de posicao conforme o id, para o gabarito nao viciar numa letra.
    static void montarAlternativas(Questao q, List<ValidadorQuestaoGerada.Alternativa> alternativas, long semente) {
        List<ValidadorQuestaoGerada.Alternativa> ordenadas = new ArrayList<>();
        if (Questao.CERTO_ERRADO.equals(q.getTipo())) {
            ordenadas.addAll(alternativas);
        } else {
            ValidadorQuestaoGerada.Alternativa correta = alternativas.stream()
                    .filter(ValidadorQuestaoGerada.Alternativa::correta).findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Rascunho sem alternativa correta"));
            List<ValidadorQuestaoGerada.Alternativa> erradas = alternativas.stream().filter(a -> !a.correta()).toList();
            int posicao = (int) (semente % alternativas.size());
            ordenadas.addAll(erradas.subList(0, posicao));
            ordenadas.add(correta);
            ordenadas.addAll(erradas.subList(posicao, erradas.size()));
        }
        int ordem = 1;
        for (ValidadorQuestaoGerada.Alternativa a : ordenadas) {
            Alternativa alt = new Alternativa();
            alt.setQuestao(q);
            alt.setTexto(a.texto());
            alt.setCorreta(a.correta());
            alt.setOrdem(ordem++);
            q.getAlternativas().add(alt);
        }
    }

    private void garantirNoConteudo(ConcursoCargo cargoDetached, Disciplina disciplina) {
        ConcursoCargo cargo = em.find(ConcursoCargo.class, cargoDetached.getId());
        boolean jaTem = cargo.getConteudo().stream()
                .anyMatch(cd -> cd.getDisciplina().getId().equals(disciplina.getId()));
        if (!jaTem) {
            CargoDisciplina cd = new CargoDisciplina();
            cd.setCargo(cargo);
            cd.setDisciplina(disciplina);
            cd.setOrdem(cargo.getConteudo().size() + 1);
            cargo.getConteudo().add(cd);
        }
        long topicos = assuntoRepository.findByDisciplinaIdOrderByNomeAsc(disciplina.getId()).size();
        cargo.getConteudo().stream()
                .filter(cd -> cd.getDisciplina().getId().equals(disciplina.getId()))
                .forEach(cd -> cd.setTotalTopicos((int) topicos));
    }

    private com.gustavo.concursos.entity.Banca bancaPadrao() {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "O concurso de origem nao tem banca cadastrada");
    }

    List<ValidadorQuestaoGerada.Alternativa> lerAlternativas(String jsonAlternativas) {
        try {
            return json.readValue(jsonAlternativas, new TypeReference<List<ValidadorQuestaoGerada.Alternativa>>() {});
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Alternativas do rascunho em formato invalido");
        }
    }
}

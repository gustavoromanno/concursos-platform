package com.gustavo.concursos.specification;

import com.gustavo.concursos.entity.Comentario;
import com.gustavo.concursos.entity.Questao;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

// Cada metodo devolve um filtro isolado; o controller combina os que vierem
// preenchidos. Parametro nulo significa "nao restringe".
public class QuestaoSpecification {

    private QuestaoSpecification() {
    }

    public static Specification<Questao> disciplinaId(Long disciplinaId) {
        return (root, query, cb) ->
                disciplinaId == null ? null : cb.equal(root.get("disciplina").get("id"), disciplinaId);
    }

    public static Specification<Questao> bancaId(Long bancaId) {
        return (root, query, cb) ->
                bancaId == null ? null : cb.equal(root.get("banca").get("id"), bancaId);
    }

    public static Specification<Questao> orgaoId(Long orgaoId) {
        return (root, query, cb) ->
                orgaoId == null ? null : cb.equal(root.get("orgao").get("id"), orgaoId);
    }

    public static Specification<Questao> ano(Integer ano) {
        return (root, query, cb) ->
                ano == null ? null : cb.equal(root.get("ano"), ano);
    }

    public static Specification<Questao> assunto(String assunto) {
        return (root, query, cb) ->
                (assunto == null || assunto.isBlank())
                        ? null
                        : cb.like(cb.lower(root.get("assunto")), "%" + assunto.toLowerCase() + "%");
    }

    public static Specification<Questao> assuntoId(Long assuntoId) {
        return (root, query, cb) ->
                assuntoId == null ? null : cb.equal(root.get("assuntoRef").get("id"), assuntoId);
    }

    public static Specification<Questao> tipo(String tipo) {
        return (root, query, cb) ->
                (tipo == null || tipo.isBlank()) ? null : cb.equal(root.get("tipo"), tipo);
    }

    // Busca por trecho do enunciado, sem diferenciar maiusculas de minusculas.
    public static Specification<Questao> palavraChave(String termo) {
        return (root, query, cb) ->
                (termo == null || termo.isBlank())
                        ? null
                        : cb.like(cb.lower(root.get("enunciado")), "%" + termo.trim().toLowerCase() + "%");
    }

    // Apenas questoes que ja receberam pelo menos um comentario.
    public static Specification<Questao> comComentarios(Boolean somenteComComentarios) {
        return (root, query, cb) -> {
            if (!Boolean.TRUE.equals(somenteComComentarios)) return null;
            Subquery<Long> sub = query.subquery(Long.class);
            var c = sub.from(Comentario.class);
            sub.select(c.get("id")).where(cb.equal(c.get("questao"), root));
            return cb.exists(sub);
        };
    }

    // Restringe a um conjunto de ids (lista vazia = nenhum resultado).
    public static Specification<Questao> idEm(Collection<Long> ids) {
        return (root, query, cb) ->
                ids == null ? null : (ids.isEmpty() ? cb.disjunction() : root.get("id").in(ids));
    }

    // Exclui um conjunto de ids (lista vazia = nao restringe).
    public static Specification<Questao> idFora(Collection<Long> ids) {
        return (root, query, cb) ->
                (ids == null || ids.isEmpty()) ? null : cb.not(root.get("id").in(ids));
    }
}

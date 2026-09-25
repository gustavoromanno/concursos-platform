package com.gustavo.concursos.specification;

import com.gustavo.concursos.entity.Questao;
import org.springframework.data.jpa.domain.Specification;

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
}

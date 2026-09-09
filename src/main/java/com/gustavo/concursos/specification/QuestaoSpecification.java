package com.gustavo.concursos.specification;

import com.gustavo.concursos.entity.Questao;
import org.springframework.data.jpa.domain.Specification;

// Cada metodo retorna um filtro isolado. O controller combina apenas os que
// vierem preenchidos na requisicao, usando Specification.where(...).and(...).
// Isso evita ter um metodo de repositorio para cada combinacao possivel
// (disciplina, disciplina+banca, disciplina+banca+ano, etc).
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
}

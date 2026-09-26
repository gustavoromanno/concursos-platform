package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Questao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestaoRepository extends JpaRepository<Questao, Long>, JpaSpecificationExecutor<Questao> {

    // @EntityGraph faz o Hibernate trazer disciplina, banca e assunto no MESMO
    // SELECT da questao (via JOIN), em vez de uma query extra por questao.
    //
    // As alternativas ficam de fora do grafo de proposito: buscar uma colecao
    // junto com paginacao obriga o Hibernate a paginar em memoria. Elas sao
    // resolvidas pelo default_batch_fetch_size (uma query para ate 50 questoes).
    @Override
    @EntityGraph(attributePaths = {"disciplina", "banca", "assuntoRef"})
    Page<Questao> findAll(Specification<Questao> spec, Pageable pageable);

    @Query(value = """
        SELECT * FROM questao q
        WHERE (:disciplinaId IS NULL OR q.disciplina_id = :disciplinaId)
          AND (:bancaId IS NULL OR q.banca_id = :bancaId)
        ORDER BY RANDOM()
        LIMIT :quantidade
        """, nativeQuery = true)
    List<Questao> sortear(
            @Param("disciplinaId") Long disciplinaId,
            @Param("bancaId") Long bancaId,
            @Param("quantidade") int quantidade
    );

    // Questoes cuja resposta MAIS RECENTE do usuario foi errada.
    // Se ele errou e depois acertou, a questao sai da lista de revisao.
    @Query(value = """
        SELECT q.* FROM questao q
        JOIN (
            SELECT DISTINCT ON (r.questao_id) r.questao_id, r.correta
            FROM resposta r
            WHERE r.usuario_id = :usuarioId
            ORDER BY r.questao_id, r.respondida_em DESC
        ) ultima ON ultima.questao_id = q.id
        WHERE ultima.correta = FALSE
        ORDER BY q.id
        """, nativeQuery = true)
    List<Questao> questoesErradas(@Param("usuarioId") Long usuarioId);

    // Ids das questoes que o usuario ja respondeu pelo menos uma vez.
    @Query(value = "SELECT DISTINCT r.questao_id FROM resposta r WHERE r.usuario_id = :usuarioId",
            nativeQuery = true)
    List<Long> idsRespondidas(@Param("usuarioId") Long usuarioId);

    // Ids das questoes cuja resposta MAIS RECENTE do usuario foi certa (true) ou errada (false).
    // Mesmo criterio da lista "Revisar erradas".
    @Query(value = """
        SELECT ultima.questao_id FROM (
            -- respondida_em no SELECT deixa a consulta valida tambem no H2 dos testes.
            SELECT DISTINCT ON (r.questao_id) r.questao_id, r.correta, r.respondida_em
            FROM resposta r
            WHERE r.usuario_id = :usuarioId
            ORDER BY r.questao_id, r.respondida_em DESC
        ) ultima
        WHERE ultima.correta = :correta
        """, nativeQuery = true)
    List<Long> idsPorUltimaResposta(@Param("usuarioId") Long usuarioId, @Param("correta") boolean correta);
}

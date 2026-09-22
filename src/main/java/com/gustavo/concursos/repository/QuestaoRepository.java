package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Questao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// JpaSpecificationExecutor permite montar filtros dinamicos e combinaveis
// (disciplina + banca + ano + assunto) sem precisar de um metodo para cada
// combinacao possivel. Usado no endpoint de listagem.
public interface QuestaoRepository extends JpaRepository<Questao, Long>, JpaSpecificationExecutor<Questao> {

    // Sorteia questoes para o simulado. Query nativa porque ORDER BY RANDOM()
    // nao existe em JPQL. Os filtros sao opcionais: quando o parametro vem nulo,
    // a condicao passa a ser sempre verdadeira e nao restringe nada.
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
}

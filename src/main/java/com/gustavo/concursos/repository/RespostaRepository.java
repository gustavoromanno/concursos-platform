package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RespostaRepository extends JpaRepository<Resposta, Long> {

    List<Resposta> findByUsuarioId(Long usuarioId);

    // Projections: em vez de List<Object[]> (onde voce precisa lembrar que a
    // posicao 0 e o nome e a 1 e o total), o Spring Data monta objetos com
    // getters tipados a partir dos aliases da query.
    interface EstatisticaDisciplina {
        String getDisciplina();
        long getTotal();
        long getAcertos();
    }

    interface EstatisticaDiaria {
        LocalDate getDia();
        long getTotal();
        long getAcertos();
    }

    interface TotalGeral {
        long getTotal();
        long getAcertos();
    }

    @Query("""
        SELECT q.disciplina.nome AS disciplina,
               COUNT(r) AS total,
               SUM(CASE WHEN r.correta = true THEN 1 ELSE 0 END) AS acertos
        FROM Resposta r
        JOIN r.questao q
        WHERE r.usuario.id = :usuarioId
        GROUP BY q.disciplina.nome
        ORDER BY COUNT(r) DESC
        """)
    List<EstatisticaDisciplina> estatisticasPorDisciplina(@Param("usuarioId") Long usuarioId);

    @Query("""
        SELECT COUNT(r) AS total,
               SUM(CASE WHEN r.correta = true THEN 1 ELSE 0 END) AS acertos
        FROM Resposta r
        WHERE r.usuario.id = :usuarioId
        """)
    TotalGeral totalGeral(@Param("usuarioId") Long usuarioId);

    // Evolucao diaria. Query nativa porque agrupar por data (descartando a hora)
    // e mais direto em SQL do que em JPQL.
    @Query(value = """
        SELECT CAST(r.respondida_em AS DATE) AS dia,
               COUNT(*) AS total,
               SUM(CASE WHEN r.correta THEN 1 ELSE 0 END) AS acertos
        FROM resposta r
        WHERE r.usuario_id = :usuarioId
          AND r.respondida_em >= :desde
        GROUP BY CAST(r.respondida_em AS DATE)
        ORDER BY dia
        """, nativeQuery = true)
    List<EstatisticaDiaria> evolucaoDiaria(
            @Param("usuarioId") Long usuarioId,
            @Param("desde") LocalDate desde
    );

    // Base para "refazer questoes erradas" (Sprint 5).
    @Query("""
        SELECT DISTINCT r.questao.id
        FROM Resposta r
        WHERE r.usuario.id = :usuarioId AND r.correta = false
        """)
    List<Long> idsQuestoesErradas(@Param("usuarioId") Long usuarioId);
}

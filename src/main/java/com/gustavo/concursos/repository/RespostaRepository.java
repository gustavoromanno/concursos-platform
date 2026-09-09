package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RespostaRepository extends JpaRepository<Resposta, Long> {

    List<Resposta> findByUsuarioId(Long usuarioId);

    // Base para o dashboard do Sprint 4: % de acerto por disciplina.
    // Equivalente a query SQL combinada no roadmap.
    @Query("""
        SELECT q.disciplina.nome AS disciplina,
               COUNT(r) AS total,
               SUM(CASE WHEN r.correta = true THEN 1 ELSE 0 END) AS acertos
        FROM Resposta r
        JOIN r.questao q
        WHERE r.usuario.id = :usuarioId
        GROUP BY q.disciplina.nome
        """)
    List<Object[]> estatisticasPorDisciplina(@Param("usuarioId") Long usuarioId);

    // Base para "refazer questoes erradas" do Sprint 5.
    @Query("""
        SELECT DISTINCT r.questao.id
        FROM Resposta r
        WHERE r.usuario.id = :usuarioId AND r.correta = false
        """)
    List<Long> idsQuestoesErradas(@Param("usuarioId") Long usuarioId);
}

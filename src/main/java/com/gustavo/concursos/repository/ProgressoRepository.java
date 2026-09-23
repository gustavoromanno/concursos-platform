package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Assunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Cruza o conteudo programatico do cargo com o historico do usuario.
// O LEFT JOIN garante que assuntos nunca respondidos apareçam com zero —
// sao exatamente os que ainda "vao cair" e nao foram tocados.
public interface ProgressoRepository extends JpaRepository<Assunto, Long> {

    interface ProgressoAssunto {
        Long getAssuntoId();
        String getAssunto();
        String getDisciplina();
        long getRespondidas();
        long getAcertos();
    }

    @Query(value = """
        SELECT a.id AS "assuntoId",
               a.nome AS assunto,
               d.nome AS disciplina,
               COALESCE(h.respondidas, 0) AS respondidas,
               COALESCE(h.acertos, 0) AS acertos
        FROM cargo_disciplina cd
        JOIN disciplina d ON d.id = cd.disciplina_id
        JOIN assunto a ON a.disciplina_id = d.id
        LEFT JOIN (
            SELECT q.assunto_id,
                   COUNT(*) AS respondidas,
                   SUM(CASE WHEN r.correta THEN 1 ELSE 0 END) AS acertos
            FROM resposta r
            JOIN questao q ON q.id = r.questao_id
            WHERE r.usuario_id = :usuarioId AND q.assunto_id IS NOT NULL
            GROUP BY q.assunto_id
        ) h ON h.assunto_id = a.id
        WHERE cd.cargo_id = :cargoId
        ORDER BY d.nome, a.nome
        """, nativeQuery = true)
    List<ProgressoAssunto> progressoDoCargo(
            @Param("cargoId") Long cargoId,
            @Param("usuarioId") Long usuarioId
    );
}

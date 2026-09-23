package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Videoaula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoaulaRepository extends JpaRepository<Videoaula, Long> {

    @Query("""
        SELECT v FROM Videoaula v
        WHERE (:disciplinaId IS NULL OR v.disciplina.id = :disciplinaId)
          AND (:assuntoId IS NULL OR v.assunto.id = :assuntoId)
        ORDER BY v.titulo ASC
        """)
    List<Videoaula> buscar(
            @Param("disciplinaId") Long disciplinaId,
            @Param("assuntoId") Long assuntoId
    );

    // Videoaulas dos assuntos em que o usuario mais erra.
    // A subconsulta ranqueia os assuntos por quantidade de erros; o JOIN traz
    // os videos daqueles assuntos, dos piores para os melhores.
    @Query(value = """
        SELECT v.* FROM videoaula v
        JOIN (
            SELECT q.assunto_id, COUNT(*) AS erros
            FROM resposta r
            JOIN questao q ON q.id = r.questao_id
            WHERE r.usuario_id = :usuarioId
              AND r.correta = FALSE
              AND q.assunto_id IS NOT NULL
            GROUP BY q.assunto_id
        ) fracos ON fracos.assunto_id = v.assunto_id
        ORDER BY fracos.erros DESC, v.titulo ASC
        LIMIT :limite
        """, nativeQuery = true)
    List<Videoaula> sugestoesPorErros(
            @Param("usuarioId") Long usuarioId,
            @Param("limite") int limite
    );

    // Videos do assunto de uma questao especifica — usado para sugerir
    // conteudo logo depois que o usuario erra aquela questao.
    @Query("""
        SELECT v FROM Videoaula v
        WHERE v.assunto.id = (SELECT q.assuntoRef.id FROM Questao q WHERE q.id = :questaoId)
        ORDER BY v.titulo ASC
        """)
    List<Videoaula> porQuestao(@Param("questaoId") Long questaoId);
}

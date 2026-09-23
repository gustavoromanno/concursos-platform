package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Revisao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RevisaoRepository extends JpaRepository<Revisao, Long> {

    Optional<Revisao> findByUsuarioIdAndQuestaoId(Long usuarioId, Long questaoId);

    // Vencidas e de hoje.
    List<Revisao> findByUsuarioIdAndProximaRevisaoLessThanEqualOrderByProximaRevisaoAsc(
            Long usuarioId, LocalDate ate);

    long countByUsuarioIdAndProximaRevisaoLessThanEqual(Long usuarioId, LocalDate ate);

    // Quantas revisoes caem em cada dia daqui para a frente — alimenta a
    // previsao "o que vem pela semana".
    interface AgendaDia {
        LocalDate getDia();
        long getTotal();
    }

    @Query("""
        SELECT r.proximaRevisao AS dia, COUNT(r) AS total
        FROM Revisao r
        WHERE r.usuario.id = :usuarioId
          AND r.proximaRevisao > :hoje
          AND r.proximaRevisao <= :limite
        GROUP BY r.proximaRevisao
        ORDER BY r.proximaRevisao
        """)
    List<AgendaDia> agenda(
            @Param("usuarioId") Long usuarioId,
            @Param("hoje") LocalDate hoje,
            @Param("limite") LocalDate limite
    );
}

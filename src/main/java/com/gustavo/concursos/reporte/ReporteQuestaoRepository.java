package com.gustavo.concursos.reporte;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteQuestaoRepository extends JpaRepository<ReporteQuestao, Long> {

    List<ReporteQuestao> findByStatusOrderByCriadoEmAsc(String status);

    List<ReporteQuestao> findTop100ByOrderByCriadoEmDesc();

    long countByUsuarioIdAndCriadoEmGreaterThanEqual(Long usuarioId, LocalDateTime desde);
}

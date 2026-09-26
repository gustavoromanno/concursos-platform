package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.ConcursoCargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcursoCargoRepository extends JpaRepository<ConcursoCargo, Long> {
    List<ConcursoCargo> findByConcursoIdOrderByOrdemAsc(Long concursoId);

    java.util.Optional<ConcursoCargo> findFirstByConcursoIdAndNomeIgnoreCase(Long concursoId, String nome);
}

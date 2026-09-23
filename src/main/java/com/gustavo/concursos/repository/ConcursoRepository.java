package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Concurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcursoRepository extends JpaRepository<Concurso, Long> {
    List<Concurso> findAllByOrderByAnoDescNomeAsc();
    List<Concurso> findBySituacaoOrderByAnoDescNomeAsc(String situacao);
}

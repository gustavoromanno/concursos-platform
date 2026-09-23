package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Prova;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvaRepository extends JpaRepository<Prova, Long> {
    List<Prova> findByConcursoId(Long concursoId);
}

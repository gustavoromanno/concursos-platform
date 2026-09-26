package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    java.util.Optional<com.gustavo.concursos.entity.Disciplina> findFirstByNomeIgnoreCase(String nome);
}

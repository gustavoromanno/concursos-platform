package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Banca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancaRepository extends JpaRepository<Banca, Long> {
}

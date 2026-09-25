package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Orgao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrgaoRepository extends JpaRepository<Orgao, Long> {
    List<Orgao> findAllByOrderByNomeAsc();
    Optional<Orgao> findByNomeIgnoreCase(String nome);
}

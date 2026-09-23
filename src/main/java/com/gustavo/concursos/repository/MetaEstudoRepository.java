package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.MetaEstudo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MetaEstudoRepository extends JpaRepository<MetaEstudo, Long> {
    Optional<MetaEstudo> findByUsuarioId(Long usuarioId);
}

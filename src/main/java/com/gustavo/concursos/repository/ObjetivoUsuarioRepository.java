package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.ObjetivoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ObjetivoUsuarioRepository extends JpaRepository<ObjetivoUsuario, Long> {
    Optional<ObjetivoUsuario> findByUsuarioId(Long usuarioId);
}

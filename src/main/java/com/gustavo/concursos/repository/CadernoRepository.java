package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Caderno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CadernoRepository extends JpaRepository<Caderno, Long> {

    List<Caderno> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    // Sempre pelo par (id, dono): ninguem abre caderno alheio adivinhando o id.
    Optional<Caderno> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByUsuarioIdAndNomeIgnoreCase(Long usuarioId, String nome);
}

package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Simulado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SimuladoRepository extends JpaRepository<Simulado, Long> {

    List<Simulado> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    long countByUsuarioIdAndCriadoEmGreaterThanEqual(Long usuarioId, java.time.LocalDateTime desde);

    // Busca por id JUNTO com o dono: impede que um usuario acesse o simulado de outro
    // apenas adivinhando o id.
    Optional<Simulado> findByIdAndUsuarioId(Long id, Long usuarioId);
}

package com.gustavo.concursos.pro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByStripeSessaoId(String stripeSessaoId);

    Optional<Pagamento> findFirstByStripePagamentoId(String stripePagamentoId);

    List<Pagamento> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    @Modifying
    @Query(value = "INSERT INTO stripe_evento (id, tipo) VALUES (:id, :tipo)", nativeQuery = true)
    void registrarEvento(@Param("id") String id, @Param("tipo") String tipo);

    @Query(value = "SELECT COUNT(*) FROM stripe_evento WHERE id = :id", nativeQuery = true)
    long contarEvento(@Param("id") String id);
}

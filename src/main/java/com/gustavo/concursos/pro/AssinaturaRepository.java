package com.gustavo.concursos.pro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {

    Optional<Assinatura> findByStripeAssinaturaId(String stripeAssinaturaId);

    List<Assinatura> findByUsuarioIdOrderByCriadaEmDesc(Long usuarioId);
}

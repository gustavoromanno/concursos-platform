package com.gustavo.concursos.email;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenConfirmacaoRepository extends JpaRepository<TokenConfirmacao, Long> {

    Optional<TokenConfirmacao> findByTokenHash(String tokenHash);

    long countByUsuarioIdAndCriadoEmGreaterThanEqual(Long usuarioId, LocalDateTime desde);
}

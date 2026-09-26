package com.gustavo.concursos.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRedefinicaoRepository extends JpaRepository<TokenRedefinicao, Long> {

    Optional<TokenRedefinicao> findByTokenHash(String tokenHash);

    long countByUsuarioIdAndCriadoEmGreaterThanEqual(Long usuarioId, LocalDateTime desde);

    // Ao redefinir, os outros links pendentes da mesma pessoa deixam de valer.
    @Modifying
    @Query("UPDATE TokenRedefinicao t SET t.usadoEm = :agora WHERE t.usuario.id = :usuarioId AND t.usadoEm IS NULL")
    void invalidarPendentes(@Param("usuarioId") Long usuarioId, @Param("agora") LocalDateTime agora);
}

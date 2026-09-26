package com.gustavo.concursos.solicitacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SolicitacaoConteudoRepository extends JpaRepository<SolicitacaoConteudo, Long> {

    List<SolicitacaoConteudo> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    List<SolicitacaoConteudo> findAllByOrderByCriadoEmDesc();

    List<SolicitacaoConteudo> findByStatusOrderByCriadoEmAsc(String status);

    long countByUsuarioIdAndCriadoEmGreaterThanEqual(Long usuarioId, LocalDateTime desde);
}

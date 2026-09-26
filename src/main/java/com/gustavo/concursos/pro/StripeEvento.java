package com.gustavo.concursos.pro;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Evento do Stripe ja processado (idempotencia). Mapeado para o Hibernate
// validar/criar a tabela; a gravacao e feita pelo PagamentoRepository.
@Entity
@Table(name = "stripe_evento")
@Getter
@Setter
@NoArgsConstructor
public class StripeEvento {

    @Id
    @Column(length = 255)
    private String id;

    @Column(nullable = false, length = 100)
    private String tipo;

    @Column(name = "recebido_em", nullable = false)
    private LocalDateTime recebidoEm = LocalDateTime.now();
}

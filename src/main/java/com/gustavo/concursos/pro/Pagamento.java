package com.gustavo.concursos.pro;

import com.gustavo.concursos.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
public class Pagamento {

    public static final String PENDENTE = "PENDENTE";
    public static final String AGUARDANDO = "AGUARDANDO";
    public static final String PAGO = "PAGO";
    public static final String FALHOU = "FALHOU";
    public static final String EXPIRADO = "EXPIRADO";
    public static final String REEMBOLSADO = "REEMBOLSADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String plano;

    @Column(nullable = false)
    private int dias;

    @Column(name = "valor_centavos", nullable = false)
    private int valorCentavos;

    @Column(nullable = false, length = 20)
    private String status = PENDENTE;

    @Column(name = "stripe_sessao_id", length = 255)
    private String stripeSessaoId;

    @Column(name = "stripe_pagamento_id", length = 255)
    private String stripePagamentoId;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "pago_em")
    private LocalDateTime pagoEm;

    @Column(name = "reembolsado_em")
    private LocalDateTime reembolsadoEm;
}

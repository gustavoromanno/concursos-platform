package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "revisao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Revisao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id", nullable = false)
    private Questao questao;

    @Column(name = "proxima_revisao", nullable = false)
    private LocalDate proximaRevisao = LocalDate.now();

    @Column(name = "intervalo_dias", nullable = false)
    private Integer intervaloDias = 1;

    // Fator de facilidade do SM-2. Comeca em 2.5 e varia entre 1.3 e 2.8.
    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal facilidade = new BigDecimal("2.50");

    @Column(nullable = false)
    private Integer repeticoes = 0;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();
}

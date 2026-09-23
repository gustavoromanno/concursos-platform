package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "concurso_cargo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ConcursoCargo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concurso_id", nullable = false)
    private Concurso concurso;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 40)
    private String nivel;

    private Integer vagas;

    @Column(name = "cadastro_reserva")
    private Integer cadastroReserva;

    @Column(precision = 12, scale = 2)
    private BigDecimal salario;

    @Column(nullable = false)
    private Integer ordem = 1;

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<CargoDisciplina> conteudo = new ArrayList<>();
}

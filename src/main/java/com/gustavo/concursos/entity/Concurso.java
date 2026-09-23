package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "concurso")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Concurso {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 150)
    private String orgao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banca_id")
    private Banca banca;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false, length = 30)
    private String situacao = "PREVISTO";

    private Integer vagas;

    @Column(name = "inscricoes_de")
    private LocalDate inscricoesDe;

    @Column(name = "inscricoes_ate")
    private LocalDate inscricoesAte;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxa;

    @Column(name = "edital_url", length = 400)
    private String editalUrl;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prova> provas = new ArrayList<>();

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<ConcursoEtapa> etapas = new ArrayList<>();

    @OneToMany(mappedBy = "concurso", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<ConcursoCargo> cargos = new ArrayList<>();
}

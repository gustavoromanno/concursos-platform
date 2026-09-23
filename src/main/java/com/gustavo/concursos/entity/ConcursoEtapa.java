package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity @Table(name = "concurso_etapa")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ConcursoEtapa {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concurso_id", nullable = false)
    private Concurso concurso;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "data_prevista")
    private LocalDate dataPrevista;

    // CONCLUIDO, EM_ANDAMENTO, PREVISTO
    @Column(nullable = false, length = 20)
    private String status = "PREVISTO";

    @Column(nullable = false)
    private Integer ordem = 1;
}

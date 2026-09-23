package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.*;

// Uma disciplina do conteudo programatico de um cargo.
@Entity @Table(name = "cargo_disciplina")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CargoDisciplina {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", nullable = false)
    private ConcursoCargo cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @Column(name = "total_topicos")
    private Integer totalTopicos;

    @Column(nullable = false)
    private Integer ordem = 1;
}

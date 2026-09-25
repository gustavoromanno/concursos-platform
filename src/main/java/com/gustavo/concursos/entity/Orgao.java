package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "orgao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Orgao {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @Column(length = 30)
    private String sigla;

    // FEDERAL, ESTADUAL, MUNICIPAL
    @Column(length = 20)
    private String esfera;
}

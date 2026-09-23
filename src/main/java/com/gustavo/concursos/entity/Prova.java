package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prova")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prova {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concurso_id", nullable = false)
    private Concurso concurso;

    @Column(nullable = false, length = 150)
    private String cargo;

    @Column(length = 30)
    private String nivel;

    @Column(name = "aplicada_em")
    private LocalDate aplicadaEm;

    @OneToMany(mappedBy = "prova", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numero ASC")
    private List<ProvaQuestao> questoes = new ArrayList<>();
}

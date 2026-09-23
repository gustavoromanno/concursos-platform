package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Questao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banca_id", nullable = false)
    private Banca banca;

    @Column(nullable = false)
    private Integer ano;

    // Texto livre, mantido por compatibilidade com o que ja existia.
    @Column(length = 150)
    private String assunto;

    // Assunto estruturado. E por aqui que agrupamos desempenho por topico
    // e ligamos a questao as videoaulas.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assunto_id")
    private Assunto assuntoRef;

    @Column(columnDefinition = "TEXT")
    private String explicacao;

    // @OrderBy garante que as alternativas sempre saiam na ordem sorteada,
    // e nao na ordem em que o banco devolver.
    @OneToMany(mappedBy = "questao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<Alternativa> alternativas = new ArrayList<>();
}

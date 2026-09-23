package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assunto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @Column(nullable = false, length = 150)
    private String nome;

    // Auto-relacionamento: um assunto pode ser subassunto de outro.
    // Ex: "Morfologia" (pai) -> "Classes de palavras" (filho).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pai_id")
    private Assunto pai;

    @OneToMany(mappedBy = "pai")
    @OrderBy("nome ASC")
    private List<Assunto> subassuntos = new ArrayList<>();
}

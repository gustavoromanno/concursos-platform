package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "questao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Questao {

    public static final String MULTIPLA_ESCOLHA = "MULTIPLA_ESCOLHA";
    public static final String CERTO_ERRADO = "CERTO_ERRADO";
    public static final String ORIGEM_AUTORAL = "AUTORAL";
    public static final String ORIGEM_IA = "IA";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banca_id", nullable = false)
    private Banca banca;

    // Órgão que abriu o concurso de onde a questão veio.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgao_id")
    private Orgao orgao;

    @Column(nullable = false)
    private Integer ano;

    @Column(length = 150)
    private String assunto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assunto_id")
    private Assunto assuntoRef;

    // MULTIPLA_ESCOLHA ou CERTO_ERRADO. Em Certo/Errado as alternativas
    // continuam existindo (duas linhas), então nada mais no sistema muda.
    @Column(nullable = false, length = 20)
    private String tipo = MULTIPLA_ESCOLHA;

    @Column(columnDefinition = "TEXT")
    private String explicacao;

    // Concurso e cargo de origem (questoes geradas a partir de provas antigas).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concurso_id")
    private Concurso concurso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    private ConcursoCargo cargo;

    // AUTORAL ou IA.
    @Column(nullable = false, length = 20)
    private String origem = ORIGEM_AUTORAL;

    @OneToMany(mappedBy = "questao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<Alternativa> alternativas = new ArrayList<>();

    public boolean isCertoErrado() {
        return CERTO_ERRADO.equals(tipo);
    }
}

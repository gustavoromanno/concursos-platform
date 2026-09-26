package com.gustavo.concursos.importacao;

import com.gustavo.concursos.entity.Questao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Questao inedita gerada pela IA. So vira questao publica depois de aprovada.
@Entity
@Table(name = "questao_rascunho")
@Getter
@Setter
@NoArgsConstructor
public class QuestaoRascunho {

    public static final String PENDENTE = "PENDENTE";
    public static final String APROVADA = "APROVADA";
    public static final String DESCARTADA = "DESCARTADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "importacao_id", nullable = false)
    private ImportacaoProva importacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_base_id")
    private QuestaoBase base;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false, length = 100)
    private String disciplina;

    @Column(length = 150)
    private String assunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    // JSON: [{"texto": "...", "correta": true}, ...]
    @Column(nullable = false, columnDefinition = "TEXT")
    private String alternativas;

    @Column(columnDefinition = "TEXT")
    private String explicacao;

    @Column(nullable = false, length = 20)
    private String status = PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id")
    private Questao questao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}

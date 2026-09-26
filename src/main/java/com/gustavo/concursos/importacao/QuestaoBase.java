package com.gustavo.concursos.importacao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Questao ORIGINAL extraida da prova. Privada: so serve de base para a geracao
// e nunca e exposta pelos endpoints publicos (direito autoral das bancas).
@Entity
@Table(name = "questao_base")
@Getter
@Setter
@NoArgsConstructor
public class QuestaoBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "importacao_id", nullable = false)
    private ImportacaoProva importacao;

    @Column(nullable = false)
    private int numero;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(length = 100)
    private String disciplina;

    @Column(length = 150)
    private String assunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    // JSON com a lista de textos das alternativas.
    @Column(columnDefinition = "TEXT")
    private String alternativas;

    @Column(length = 10)
    private String gabarito;

    @Column(nullable = false)
    private boolean anulada;
}

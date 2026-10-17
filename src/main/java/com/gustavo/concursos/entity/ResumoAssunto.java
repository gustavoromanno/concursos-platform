package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Resumo de teoria de um assunto (tabela criada na V41). O TeoriaController le e
// grava por SQL; a entidade existe para o Hibernate validar o schema em producao
// e criar a tabela no banco em memoria dos testes.
@Entity
@Table(name = "resumo_assunto")
@Getter
@Setter
@NoArgsConstructor
public class ResumoAssunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "assunto_id", nullable = false, unique = true)
    private Long assuntoId;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(nullable = false)
    private boolean gratuito;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();
}

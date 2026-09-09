package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "resposta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id", nullable = false)
    private Questao questao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alternativa_escolhida_id", nullable = false)
    private Alternativa alternativaEscolhida;

    @Column(nullable = false)
    private boolean correta;

    @Column(name = "respondida_em", nullable = false)
    private LocalDateTime respondidaEm = LocalDateTime.now();
}

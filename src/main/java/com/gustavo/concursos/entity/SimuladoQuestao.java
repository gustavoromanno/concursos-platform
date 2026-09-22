package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "simulado_questao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimuladoQuestao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulado_id", nullable = false)
    private Simulado simulado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id", nullable = false)
    private Questao questao;

    @Column(nullable = false)
    private Integer ordem;

    // Nulo enquanto o usuario nao responder essa questao do simulado.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resposta_id")
    private Resposta resposta;
}

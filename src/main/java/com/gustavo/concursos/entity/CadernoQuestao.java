package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "caderno_questao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CadernoQuestao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caderno_id", nullable = false)
    private Caderno caderno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id", nullable = false)
    private Questao questao;

    @Column(name = "adicionado_em", nullable = false)
    private LocalDateTime adicionadoEm = LocalDateTime.now();
}

package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "videoaula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Videoaula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    // So o ID do video, nao a URL completa. Ex: "dQw4w9WgXcQ".
    @Column(name = "youtube_id", nullable = false, length = 20)
    private String youtubeId;

    @Column(length = 120)
    private String canal;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    // Opcional: video geral da disciplina fica sem assunto.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assunto_id")
    private Assunto assunto;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}

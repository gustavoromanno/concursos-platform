package com.gustavo.concursos.email;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "campanha_email")
@Getter
@Setter
@NoArgsConstructor
public class CampanhaEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String assunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(nullable = false)
    private int destinatarios;

    @Column(nullable = false)
    private int enviados;

    @Column(nullable = false, length = 20)
    private String status = "ENVIANDO";

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}

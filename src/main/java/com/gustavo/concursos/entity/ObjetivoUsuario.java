package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Concurso e cargo que o usuario escolheu como alvo. Um por usuario.
@Entity @Table(name = "objetivo_usuario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ObjetivoUsuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concurso_id", nullable = false)
    private Concurso concurso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    private ConcursoCargo cargo;

    @Column(name = "definido_em", nullable = false)
    private LocalDateTime definidoEm = LocalDateTime.now();
}

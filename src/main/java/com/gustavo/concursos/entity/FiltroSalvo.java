package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Mapeada para o Hibernate validar a tabela (e cria-la no banco dos testes).
// A leitura e a gravacao ficam no FiltroSalvoController, via SQL simples.
@Entity
@Table(name = "filtro_salvo")
@Getter
@Setter
@NoArgsConstructor
public class FiltroSalvo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 60)
    private String nome;

    @Column(nullable = false, length = 2000)
    private String parametros;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}

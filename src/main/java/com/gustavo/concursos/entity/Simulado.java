package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "simulado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Simulado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;

    @Column(name = "finalizado_em")
    private LocalDateTime finalizadoEm;

    @OneToMany(mappedBy = "simulado", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<SimuladoQuestao> questoes = new ArrayList<>();

    // Momento em que o prazo se encerra.
    public LocalDateTime prazo() {
        return criadoEm.plusMinutes(duracaoMinutos);
    }

    // Um simulado esta aberto enquanto nao foi finalizado e o prazo nao venceu.
    public boolean aberto() {
        return finalizadoEm == null && LocalDateTime.now().isBefore(prazo());
    }
}

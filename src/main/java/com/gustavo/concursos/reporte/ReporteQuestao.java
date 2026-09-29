package com.gustavo.concursos.reporte;

import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reporte_questao")
@Getter
@Setter
@NoArgsConstructor
public class ReporteQuestao {

    public static final java.util.Set<String> MOTIVOS = java.util.Set.of("GABARITO", "ENUNCIADO", "DESATUALIZADA", "OUTRO");
    public static final java.util.Set<String> STATUS = java.util.Set.of("NOVO", "RESOLVIDO", "DESCARTADO");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questao_id", nullable = false)
    private Questao questao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 20)
    private String motivo;

    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false, length = 20)
    private String status = "NOVO";

    @Column(length = 500)
    private String resposta;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();
}

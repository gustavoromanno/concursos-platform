package com.gustavo.concursos.solicitacao;

import com.gustavo.concursos.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_conteudo")
@Getter
@Setter
@NoArgsConstructor
public class SolicitacaoConteudo {

    public static final java.util.Set<String> STATUS = java.util.Set.of("NOVA", "EM_ANALISE", "ATENDIDA", "RECUSADA");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String concurso;

    @Column(length = 150)
    private String cargo;

    @Column(nullable = false, length = 150)
    private String materia;

    @Column(name = "link_edital", length = 500)
    private String linkEdital;

    @Column(length = 1000)
    private String detalhes;

    @Column(nullable = false, length = 20)
    private String status = "NOVA";

    @Column(length = 500)
    private String resposta;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();
}

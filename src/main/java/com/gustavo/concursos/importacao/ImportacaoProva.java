package com.gustavo.concursos.importacao;

import com.gustavo.concursos.entity.Concurso;
import com.gustavo.concursos.entity.ConcursoCargo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "importacao_prova")
@Getter
@Setter
@NoArgsConstructor
public class ImportacaoProva {

    public static final String AGUARDANDO = "AGUARDANDO";
    public static final String EXTRAINDO = "EXTRAINDO";
    public static final String GERANDO = "GERANDO";
    public static final String CONCLUIDA = "CONCLUIDA";
    public static final String ERRO = "ERRO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String status = AGUARDANDO;

    @Column(length = 200)
    private String etapa;

    @Column(nullable = false)
    private int progresso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concurso_id")
    private Concurso concurso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    private ConcursoCargo cargo;

    @Column(name = "cargo_informado", length = 150)
    private String cargoInformado;

    @Column(name = "ineditas_por_questao", nullable = false)
    private int ineditasPorQuestao = 1;

    @Column(name = "nome_arquivo_prova", length = 255)
    private String nomeArquivoProva;

    @Column(name = "nome_arquivo_gabarito", length = 255)
    private String nomeArquivoGabarito;

    @Column(name = "tokens_entrada", nullable = false)
    private long tokensEntrada;

    @Column(name = "tokens_saida", nullable = false)
    private long tokensSaida;

    @Column(name = "mensagem_erro", columnDefinition = "TEXT")
    private String mensagemErro;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    public boolean emAndamento() {
        return EXTRAINDO.equals(status) || GERANDO.equals(status) || AGUARDANDO.equals(status);
    }
}

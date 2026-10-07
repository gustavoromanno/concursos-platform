package com.gustavo.concursos.service;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Situacao do concurso e status das etapas derivados da data de hoje, em vez de
 * congelados no valor gravado pela migration (que envelhece: "inscricoes abertas"
 * continuaria aparecendo depois do prazo).
 *
 * O valor gravado continua valendo quando nao ha data para decidir, e ENCERRADO
 * so vem do valor gravado: o fim de um concurso (homologacao) nao se deduz de datas.
 */
public final class SituacaoCalculadora {

    public static final String PREVISTO = "PREVISTO";
    public static final String INSCRICOES_ABERTAS = "INSCRICOES_ABERTAS";
    public static final String EM_ANDAMENTO = "EM_ANDAMENTO";
    public static final String ENCERRADO = "ENCERRADO";
    public static final String CONCLUIDO = "CONCLUIDO";

    public static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private SituacaoCalculadora() {
    }

    public static LocalDate hoje() {
        return LocalDate.now(FUSO);
    }

    /**
     * Regras: ENCERRADO gravado prevalece; com periodo de inscricao, antes dele e
     * PREVISTO, durante e INSCRICOES_ABERTAS, depois e EM_ANDAMENTO; sem datas,
     * fica o valor gravado.
     */
    public static String situacao(String gravada, LocalDate inscricoesDe, LocalDate inscricoesAte, LocalDate hoje) {
        if (ENCERRADO.equals(gravada)) return ENCERRADO;
        if (inscricoesAte != null && hoje.isAfter(inscricoesAte)) return EM_ANDAMENTO;
        if (inscricoesDe != null && hoje.isBefore(inscricoesDe)) return PREVISTO;
        if (inscricoesDe != null && inscricoesAte != null) return INSCRICOES_ABERTAS;
        return gravada;
    }

    /**
     * Regras: sem data, fica o gravado; data passada e CONCLUIDO; data de hoje e
     * EM_ANDAMENTO; data futura mantem EM_ANDAMENTO/CONCLUIDO se assim foi gravado
     * (ex.: "Inscricoes (ate)" em curso) e, fora isso, e PREVISTO.
     */
    public static String statusEtapa(String gravado, LocalDate data, LocalDate hoje) {
        if (data == null) return gravado;
        if (data.isBefore(hoje)) return CONCLUIDO;
        if (CONCLUIDO.equals(gravado)) return CONCLUIDO;
        if (data.isEqual(hoje)) return EM_ANDAMENTO;
        return EM_ANDAMENTO.equals(gravado) ? EM_ANDAMENTO : PREVISTO;
    }
}

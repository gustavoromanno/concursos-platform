package com.gustavo.concursos.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.gustavo.concursos.service.SituacaoCalculadora.*;
import static org.assertj.core.api.Assertions.assertThat;

class SituacaoCalculadoraTest {

    static final LocalDate DE = LocalDate.of(2026, 9, 15);
    static final LocalDate ATE = LocalDate.of(2026, 10, 14);

    @Test
    void situacaoPeloPeriodoDeInscricao() {
        assertThat(situacao(INSCRICOES_ABERTAS, DE, ATE, DE.minusDays(1))).isEqualTo(PREVISTO);
        assertThat(situacao(PREVISTO, DE, ATE, DE)).isEqualTo(INSCRICOES_ABERTAS);
        assertThat(situacao(PREVISTO, DE, ATE, ATE)).isEqualTo(INSCRICOES_ABERTAS);   // ultimo dia ainda aberto
        assertThat(situacao(INSCRICOES_ABERTAS, DE, ATE, ATE.plusDays(1))).isEqualTo(EM_ANDAMENTO);
    }

    @Test
    void encerradoGravadoPrevalece() {
        assertThat(situacao(ENCERRADO, DE, ATE, DE)).isEqualTo(ENCERRADO);
        assertThat(situacao(ENCERRADO, null, null, DE)).isEqualTo(ENCERRADO);
    }

    @Test
    void semDatasFicaOGravado() {
        assertThat(situacao(PREVISTO, null, null, DE)).isEqualTo(PREVISTO);
        assertThat(situacao(INSCRICOES_ABERTAS, null, ATE, ATE)).isEqualTo(INSCRICOES_ABERTAS);
        assertThat(situacao(INSCRICOES_ABERTAS, null, ATE, ATE.plusDays(1))).isEqualTo(EM_ANDAMENTO);
        assertThat(situacao(PREVISTO, DE, null, DE.minusDays(1))).isEqualTo(PREVISTO);
    }

    @Test
    void statusDaEtapaPelaData() {
        LocalDate prova = LocalDate.of(2026, 12, 13);
        assertThat(statusEtapa(PREVISTO, prova, prova.minusDays(1))).isEqualTo(PREVISTO);
        assertThat(statusEtapa(PREVISTO, prova, prova)).isEqualTo(EM_ANDAMENTO);
        assertThat(statusEtapa(PREVISTO, prova, prova.plusDays(1))).isEqualTo(CONCLUIDO);
        assertThat(statusEtapa(EM_ANDAMENTO, prova, prova.plusDays(1))).isEqualTo(CONCLUIDO);
    }

    @Test
    void etapaEmCursoGravadaContinuaEmCursoAteAData() {
        // "Inscrições (até)": gravada EM_ANDAMENTO enquanto o prazo corre.
        assertThat(statusEtapa(EM_ANDAMENTO, ATE, ATE.minusDays(10))).isEqualTo(EM_ANDAMENTO);
        // Concluida a mao antes da data continua concluida.
        assertThat(statusEtapa(CONCLUIDO, ATE, ATE.minusDays(10))).isEqualTo(CONCLUIDO);
    }

    @Test
    void etapaSemDataFicaComOGravado() {
        assertThat(statusEtapa(EM_ANDAMENTO, null, ATE)).isEqualTo(EM_ANDAMENTO);
        assertThat(statusEtapa(PREVISTO, null, ATE)).isEqualTo(PREVISTO);
    }
}

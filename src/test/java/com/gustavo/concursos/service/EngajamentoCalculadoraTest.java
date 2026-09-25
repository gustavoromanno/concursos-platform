package com.gustavo.concursos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gustavo.concursos.service.EngajamentoCalculadora.*;
import static org.assertj.core.api.Assertions.assertThat;

class EngajamentoCalculadoraTest {

    private static final int META = 10;
    private static final LocalDate HOJE = LocalDate.of(2026, 9, 25);

    private Map<LocalDate, long[]> porDia;

    @BeforeEach
    void setUp() {
        porDia = new HashMap<>();
    }

    /** Registra quantas questoes foram respondidas N dias atras (0 = hoje). */
    private void estudou(int diasAtras, long total) {
        porDia.put(HOJE.minusDays(diasAtras), new long[]{total, 0});
    }

    // --- Ofensiva atual ---

    @Test
    void semHistoricoOfensivaEZero() {
        assertThat(ofensivaAtual(porDia, META, HOJE)).isZero();
    }

    @Test
    void metaBatidaHojeContaHoje() {
        estudou(0, 10);
        estudou(1, 12);
        estudou(2, 15);

        assertThat(ofensivaAtual(porDia, META, HOJE)).isEqualTo(3);
    }

    @Test
    void hojeAindaSemMetaNaoZeraOfensiva() {
        // Manha: ainda nao estudou o suficiente, mas ontem e anteontem bateu.
        estudou(0, 3);
        estudou(1, 10);
        estudou(2, 10);

        assertThat(ofensivaAtual(porDia, META, HOJE)).isEqualTo(2);
    }

    @Test
    void hojeSemNenhumaRespostaTambemComecaDeOntem() {
        estudou(1, 10);

        assertThat(ofensivaAtual(porDia, META, HOJE)).isEqualTo(1);
    }

    @Test
    void ontemSemMetaZeraOfensiva() {
        // Pulou ontem: a sequencia antiga nao conta mais.
        estudou(2, 10);
        estudou(3, 10);

        assertThat(ofensivaAtual(porDia, META, HOJE)).isZero();
    }

    @Test
    void diaSemEstudoNoMeioInterrompeSequencia() {
        estudou(0, 10);
        estudou(1, 10);
        // dia 2: nada
        estudou(3, 10);
        estudou(4, 10);

        assertThat(ofensivaAtual(porDia, META, HOJE)).isEqualTo(2);
    }

    @Test
    void diaAbaixoDaMetaNoMeioInterrompeSequencia() {
        estudou(0, 10);
        estudou(1, 9);   // estudou, mas nao bateu a meta
        estudou(2, 10);

        assertThat(ofensivaAtual(porDia, META, HOJE)).isEqualTo(1);
    }

    @Test
    void exatamenteAMetaConta() {
        estudou(0, META);

        assertThat(ofensivaAtual(porDia, META, HOJE)).isEqualTo(1);
    }

    // --- Melhor ofensiva ---

    @Test
    void melhorOfensivaPegaAMaiorSequencia() {
        for (int i = 20; i <= 24; i++) estudou(i, 10);   // sequencia antiga de 5
        for (int i = 0; i <= 2; i++) estudou(i, 10);     // atual de 3

        assertThat(melhorOfensiva(porDia, META)).isEqualTo(5);
        assertThat(ofensivaAtual(porDia, META, HOJE)).isEqualTo(3);
    }

    @Test
    void melhorOfensivaIgnoraDiasAbaixoDaMeta() {
        estudou(0, 10);
        estudou(1, 5);
        estudou(2, 10);

        assertThat(melhorOfensiva(porDia, META)).isEqualTo(1);
    }

    @Test
    void melhorOfensivaSemHistoricoEZero() {
        assertThat(melhorOfensiva(porDia, META)).isZero();
    }

    @Test
    void melhorOfensivaNuncaEMenorQueAAtual() {
        for (int i = 0; i <= 6; i++) estudou(i, 10);

        assertThat(melhorOfensiva(porDia, META))
                .isGreaterThanOrEqualTo(ofensivaAtual(porDia, META, HOJE));
    }

    // --- Nivel do mapa ---

    @Test
    void nivelDoMapaSegueProporcaoDaMeta() {
        assertThat(nivel(0, META)).isEqualTo(0);    // nada
        assertThat(nivel(4, META)).isEqualTo(1);    // < 50%
        assertThat(nivel(5, META)).isEqualTo(2);    // 50% a 99%
        assertThat(nivel(10, META)).isEqualTo(3);   // meta batida
        assertThat(nivel(19, META)).isEqualTo(3);
        assertThat(nivel(20, META)).isEqualTo(4);   // o dobro da meta ou mais
    }
}

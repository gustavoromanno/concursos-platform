package com.gustavo.concursos.importacao;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class CargoCorrespondenciaTest {

    // Os cargos do Bacen 2013 como a V32 cadastra.
    static final List<String> BACEN_2013 = List.of(
            "Analista – Área 1: Análise e Desenvolvimento de Sistemas",
            "Analista – Área 2: Suporte à Infraestrutura de TI",
            "Analista – Área 3: Política Econômica e Monetária",
            "Analista – Área 4: Contabilidade e Finanças",
            "Analista – Área 5: Infraestrutura e Logística",
            "Analista – Área 6: Gestão e Análise Processual",
            "Técnico – Área 1: Suporte Técnico-Administrativo",
            "Técnico – Área 2: Segurança Institucional");

    private Optional<String> achar(List<String> cargos, String lido) {
        return CargoCorrespondencia.encontrar(cargos, Function.identity(), lido);
    }

    @Test
    void nomeIgualIgnorandoAcentoCaixaEPontuacao() {
        assertThat(achar(BACEN_2013, "ANALISTA - AREA 4: CONTABILIDADE E FINANCAS"))
                .contains("Analista – Área 4: Contabilidade e Finanças");
    }

    @Test
    void mesmaAreaDoMesmoCargoBase() {
        assertThat(achar(BACEN_2013, "Analista - Area 4")).contains("Analista – Área 4: Contabilidade e Finanças");
        assertThat(achar(BACEN_2013, "Técnico – Área 1")).contains("Técnico – Área 1: Suporte Técnico-Administrativo");
    }

    @Test
    void areaDeOutroCargoBaseNaoServe() {
        assertThat(achar(BACEN_2013, "Técnico - Área 4")).isEmpty();
    }

    @Test
    void nomeDaEspecialidadeSemNumeroDaArea() {
        assertThat(achar(BACEN_2013, "Analista – Contabilidade e Finanças"))
                .contains("Analista – Área 4: Contabilidade e Finanças");
        assertThat(achar(BACEN_2013, "Técnico do Banco Central - Segurança Institucional")).isEmpty();
    }

    @Test
    void nomeLidoMaisLongoQueOCadastrado() {
        assertThat(achar(List.of("Juiz de Direito Substituto"), "Juiz de Direito Substituto do TJRS"))
                .contains("Juiz de Direito Substituto");
    }

    @Test
    void ambiguoOuVazioNaoViraPalpite() {
        assertThat(achar(BACEN_2013, "Analista")).isEmpty();
        assertThat(achar(BACEN_2013, "Analista de Infraestrutura")).isEmpty();   // áreas 2 e 5
        assertThat(achar(BACEN_2013, "")).isEmpty();
        assertThat(achar(BACEN_2013, null)).isEmpty();
        assertThat(achar(List.of(), "Analista - Área 4")).isEmpty();
    }

    @Test
    void area1NaoConfundeComArea10() {
        List<String> cargos = List.of("Analista – Área 10: Auditoria", "Analista – Área 1: Sistemas");
        assertThat(achar(cargos, "Analista Área 1")).contains("Analista – Área 1: Sistemas");
    }
}

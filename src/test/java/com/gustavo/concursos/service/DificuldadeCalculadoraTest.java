package com.gustavo.concursos.service;

import org.junit.jupiter.api.Test;

import static com.gustavo.concursos.service.DificuldadeCalculadora.Dificuldade.*;
import static com.gustavo.concursos.service.DificuldadeCalculadora.classificar;
import static com.gustavo.concursos.service.DificuldadeCalculadora.percentual;
import static org.assertj.core.api.Assertions.assertThat;

class DificuldadeCalculadoraTest {

    @Test
    void comMenosDeCincoRespostasNaoClassifica() {
        assertThat(classificar(0, 0)).isNull();
        assertThat(classificar(4, 4)).isNull();
    }

    @Test
    void faixasNosLimitesExatos() {
        assertThat(classificar(5, 4)).isEqualTo(FACIL);          // 80%
        assertThat(classificar(5, 3)).isEqualTo(MEDIA);          // 60%
        assertThat(classificar(5, 2)).isEqualTo(DIFICIL);        // 40%
        assertThat(classificar(5, 1)).isEqualTo(MUITO_DIFICIL);  // 20%
    }

    @Test
    void logoAbaixoDeCadaLimiteCaiNaFaixaSeguinte() {
        assertThat(classificar(1000, 799)).isEqualTo(MEDIA);         // 79,9%
        assertThat(classificar(1000, 599)).isEqualTo(DIFICIL);       // 59,9%
        assertThat(classificar(1000, 399)).isEqualTo(MUITO_DIFICIL); // 39,9%
    }

    @Test
    void percentualUsaUmaCasaDecimalComoATela() {
        // 7 de 9 = 77,77...% -> 77,8%, exatamente o numero exibido ao usuario.
        assertThat(percentual(9, 7)).isEqualTo(77.8);
        assertThat(percentual(0, 0)).isEqualTo(0.0);
    }

    @Test
    void arredondamentoDaTelaDecideAFaixa() {
        // 7996 de 10000 = 79,96% -> exibido como 80,0% -> Facil. A faixa segue
        // o numero que o usuario ve, para o rotulo nunca contradizer o percentual.
        assertThat(classificar(10000, 7996)).isEqualTo(FACIL);
    }

    @Test
    void rotulosExibidos() {
        assertThat(FACIL.rotulo()).isEqualTo("Fácil");
        assertThat(MUITO_DIFICIL.rotulo()).isEqualTo("Muito difícil");
    }
}

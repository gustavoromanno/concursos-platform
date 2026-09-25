package com.gustavo.concursos.service;

import com.gustavo.concursos.dto.ObjetivoDTO.DisciplinaProgressoDTO;
import com.gustavo.concursos.dto.ObjetivoDTO.ProgressoDTO;
import com.gustavo.concursos.repository.ProgressoRepository.ProgressoAssunto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.gustavo.concursos.service.ObjetivoCalculadora.agruparPorDisciplina;
import static com.gustavo.concursos.service.ObjetivoCalculadora.classificar;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ObjetivoCalculadoraTest {

    /** Simula uma linha da consulta: um assunto com N respondidas e M acertos. */
    private ProgressoAssunto linha(String disciplina, long respondidas, long acertos) {
        ProgressoAssunto l = mock(ProgressoAssunto.class);
        when(l.getDisciplina()).thenReturn(disciplina);
        when(l.getRespondidas()).thenReturn(respondidas);
        when(l.getAcertos()).thenReturn(acertos);
        return l;
    }

    private ProgressoAssunto linha(long respondidas, long acertos) {
        return linha("Portugues", respondidas, acertos);
    }

    // Ordem do ProgressoDTO: total, iniciados, percentual, dominado, atencao, revisar, vaiCair

    // --- Limites de cada faixa ---

    @Test
    void oitentaPorCentoExatoEDominado() {
        assertThat(classificar(List.of(linha(5, 4))))
                .isEqualTo(new ProgressoDTO(1, 1, 100.0, 1, 0, 0, 0));
    }

    @Test
    void logoAbaixoDeOitentaPorCentoEAtencao() {
        assertThat(classificar(List.of(linha(100, 79))))
                .isEqualTo(new ProgressoDTO(1, 1, 100.0, 0, 1, 0, 0));
    }

    @Test
    void cinquentaPorCentoExatoEAtencao() {
        assertThat(classificar(List.of(linha(2, 1))))
                .isEqualTo(new ProgressoDTO(1, 1, 100.0, 0, 1, 0, 0));
    }

    @Test
    void logoAbaixoDeCinquentaPorCentoERevisar() {
        assertThat(classificar(List.of(linha(100, 49))))
                .isEqualTo(new ProgressoDTO(1, 1, 100.0, 0, 0, 1, 0));
    }

    @Test
    void cemPorCentoEDominado() {
        assertThat(classificar(List.of(linha(3, 3))))
                .isEqualTo(new ProgressoDTO(1, 1, 100.0, 1, 0, 0, 0));
    }

    // --- "Vai cair" vs "revisar" ---

    @Test
    void semRespostaEVaiCair() {
        assertThat(classificar(List.of(linha(0, 0))))
                .isEqualTo(new ProgressoDTO(1, 0, 0.0, 0, 0, 0, 1));
    }

    @Test
    void respondeuEErrouTudoERevisarNaoVaiCair() {
        // Zero acertos nao e o mesmo que nunca ter tentado.
        assertThat(classificar(List.of(linha(4, 0))))
                .isEqualTo(new ProgressoDTO(1, 1, 100.0, 0, 0, 1, 0));
    }

    // --- Totais e percentual ---

    @Test
    void semConteudoProgramaticoTudoZerado() {
        assertThat(classificar(List.of()))
                .isEqualTo(new ProgressoDTO(0, 0, 0.0, 0, 0, 0, 0));
    }

    @Test
    void cenarioMistoSomaCadaFaixa() {
        var linhas = List.of(
                linha(10, 9),   // dominado
                linha(10, 6),   // atencao
                linha(10, 2),   // revisar
                linha(0, 0)     // vai cair
        );

        assertThat(classificar(linhas))
                .isEqualTo(new ProgressoDTO(4, 3, 75.0, 1, 1, 1, 1));
    }

    @Test
    void percentualArredondaParaUmaCasa() {
        var linhas = List.of(linha(1, 1), linha(1, 1), linha(0, 0));   // 2 de 3 = 66,666...

        assertThat(classificar(linhas).percentual()).isEqualTo(66.7);
    }

    // --- Agrupamento por disciplina ---

    @Test
    void agrupaPorDisciplinaMantendoOrdem() {
        var linhas = List.of(
                linha("Portugues", 5, 3),
                linha("Portugues", 0, 0),
                linha("Portugues", 2, 2),
                linha("Direito Administrativo", 0, 0)
        );

        assertThat(agruparPorDisciplina(linhas)).containsExactly(
                new DisciplinaProgressoDTO("Portugues", 3, 2, 66.7),
                new DisciplinaProgressoDTO("Direito Administrativo", 1, 0, 0.0)
        );
    }

    @Test
    void agrupamentoSemLinhasEVazio() {
        assertThat(agruparPorDisciplina(List.of())).isEmpty();
    }
}

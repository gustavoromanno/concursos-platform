package com.gustavo.concursos.service;

import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Revisao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.RevisaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevisaoServiceTest {

    @Mock
    private RevisaoRepository revisaoRepository;

    @Mock
    private Usuario usuario;

    @Mock
    private Questao questao;

    @InjectMocks
    private RevisaoService service;

    // Revisao ja existente, reaproveitada a cada chamada — simula o estado salvo no banco.
    private Revisao revisao;

    @BeforeEach
    void setUp() {
        revisao = new Revisao();
        revisao.setRepeticoes(0);
        revisao.setIntervaloDias(0);
        revisao.setFacilidade(new BigDecimal("2.50"));

        lenient().when(revisaoRepository.findByUsuarioIdAndQuestaoId(any(), any()))
                .thenReturn(Optional.of(revisao));
    }

    private void acertar() { service.registrar(usuario, questao, true); }
    private void errar()   { service.registrar(usuario, questao, false); }

    // --- Progressao de acertos ---

    @Test
    void acertosSeguidosSeguemSequenciaDoSm2() {
        int[] esperado = {1, 6, 16, 45, 126};

        for (int dias : esperado) {
            acertar();
            assertThat(revisao.getIntervaloDias()).isEqualTo(dias);
        }
    }

    @Test
    void intervaloNuncaPassaDeUmAno() {
        for (int i = 0; i < 7; i++) acertar();   // 1, 6, 16, 45, 126, 353, 988 -> 365

        assertThat(revisao.getIntervaloDias()).isEqualTo(365);
    }

    @Test
    void facilidadeNaoPassaDoMaximo() {
        for (int i = 0; i < 10; i++) acertar();

        assertThat(revisao.getFacilidade()).isEqualByComparingTo("2.80");
    }

    // --- Erro ---

    @Test
    void erroVoltaParaUmDiaEZeraRepeticoes() {
        acertar(); acertar(); acertar();          // intervalo 16
        errar();

        assertThat(revisao.getIntervaloDias()).isEqualTo(1);
        assertThat(revisao.getRepeticoes()).isZero();
    }

    @Test
    void erroReduzFacilidade() {
        errar();

        assertThat(revisao.getFacilidade()).isEqualByComparingTo("2.30");
    }

    @Test
    void facilidadeNaoCaiAbaixoDoMinimo() {
        for (int i = 0; i < 10; i++) errar();

        assertThat(revisao.getFacilidade()).isEqualByComparingTo("1.30");
    }

    @Test
    void depoisDoErroSequenciaRecomecaDoInicio() {
        acertar(); acertar(); acertar();
        errar();
        acertar();

        assertThat(revisao.getRepeticoes()).isEqualTo(1);
        assertThat(revisao.getIntervaloDias()).isEqualTo(1);
    }

    // --- Agenda ---

    @Test
    void proximaRevisaoEHojeMaisIntervalo() {
        acertar(); acertar();                     // intervalo 6

        assertThat(revisao.getProximaRevisao()).isEqualTo(LocalDate.now().plusDays(6));
    }

    @Test
    void primeiraRespostaCriaRevisaoNova() {
        when(revisaoRepository.findByUsuarioIdAndQuestaoId(any(), any()))
                .thenReturn(Optional.empty());

        acertar();

        ArgumentCaptor<Revisao> salva = ArgumentCaptor.forClass(Revisao.class);
        verify(revisaoRepository).save(salva.capture());

        assertThat(salva.getValue().getRepeticoes()).isEqualTo(1);
        assertThat(salva.getValue().getIntervaloDias()).isEqualTo(1);
        assertThat(salva.getValue().getProximaRevisao()).isEqualTo(LocalDate.now().plusDays(1));
    }
}

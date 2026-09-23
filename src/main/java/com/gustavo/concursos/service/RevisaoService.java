package com.gustavo.concursos.service;

import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Revisao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.RevisaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Agenda de revisão baseada no SM-2, o algoritmo por trás do Anki.
 *
 * A ideia: revisar no momento em que você está prestes a esquecer. Acertos
 * seguidos afastam a questão progressivamente; um erro traz de volta amanhã.
 */
@Service
public class RevisaoService {

    private static final BigDecimal FACILIDADE_MINIMA = new BigDecimal("1.30");
    private static final BigDecimal FACILIDADE_MAXIMA = new BigDecimal("2.80");
    private static final BigDecimal PREMIO_ACERTO = new BigDecimal("0.10");
    private static final BigDecimal PENALIDADE_ERRO = new BigDecimal("0.20");

    private final RevisaoRepository revisaoRepository;

    public RevisaoService(RevisaoRepository revisaoRepository) {
        this.revisaoRepository = revisaoRepository;
    }

    /**
     * Chamado toda vez que o usuário responde uma questão.
     * Cria a agenda na primeira vez e a recalcula nas seguintes.
     */
    @Transactional
    public void registrar(Usuario usuario, Questao questao, boolean acertou) {
        Revisao revisao = revisaoRepository
                .findByUsuarioIdAndQuestaoId(usuario.getId(), questao.getId())
                .orElseGet(() -> {
                    Revisao nova = new Revisao();
                    nova.setUsuario(usuario);
                    nova.setQuestao(questao);
                    return nova;
                });

        if (acertou) {
            aplicarAcerto(revisao);
        } else {
            aplicarErro(revisao);
        }

        revisao.setProximaRevisao(LocalDate.now().plusDays(revisao.getIntervaloDias()));
        revisao.setAtualizadoEm(LocalDateTime.now());
        revisaoRepository.save(revisao);
    }

    private void aplicarAcerto(Revisao r) {
        int repeticoes = r.getRepeticoes() + 1;
        r.setRepeticoes(repeticoes);

        // Os dois primeiros intervalos são fixos (1 e 6 dias); a partir do
        // terceiro, o intervalo anterior é multiplicado pelo fator de facilidade.
        int intervalo = switch (repeticoes) {
            case 1 -> 1;
            case 2 -> 6;
            default -> BigDecimal.valueOf(r.getIntervaloDias())
                    .multiply(r.getFacilidade())
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValue();
        };

        // Teto de um ano: além disso o agendamento perde sentido prático.
        r.setIntervaloDias(Math.min(intervalo, 365));
        r.setFacilidade(limitar(r.getFacilidade().add(PREMIO_ACERTO)));
    }

    private void aplicarErro(Revisao r) {
        // Errou: recomeça a contagem e volta amanhã. A facilidade cai, então
        // mesmo quando voltar a acertar, essa questão será revisada com mais
        // frequência do que uma que nunca foi errada.
        r.setRepeticoes(0);
        r.setIntervaloDias(1);
        r.setFacilidade(limitar(r.getFacilidade().subtract(PENALIDADE_ERRO)));
    }

    private BigDecimal limitar(BigDecimal valor) {
        return valor.max(FACILIDADE_MINIMA).min(FACILIDADE_MAXIMA);
    }
}

package com.gustavo.concursos.service;

import com.gustavo.concursos.dto.ObjetivoDTO;
import com.gustavo.concursos.repository.ProgressoRepository.ProgressoAssunto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Classificacao do progresso no objetivo de estudo.
 *
 * Regras por assunto do conteudo programatico:
 *   dominado  — taxa de acerto >= 80%
 *   atencao   — 50% a 79%
 *   revisar   — abaixo de 50%
 *   vai cair  — nenhuma resposta ainda
 *
 * Funcoes puras: sem banco nem Spring, testaveis isoladamente.
 */
public final class ObjetivoCalculadora {

    static final double LIMITE_DOMINADO = 0.8;
    static final double LIMITE_ATENCAO = 0.5;

    private ObjetivoCalculadora() {
    }

    public static ObjetivoDTO.ProgressoDTO classificar(List<? extends ProgressoAssunto> linhas) {
        int dominado = 0, atencao = 0, revisar = 0, vaiCair = 0;

        for (var l : linhas) {
            if (l.getRespondidas() == 0) {
                vaiCair++;
                continue;
            }
            double taxa = (double) l.getAcertos() / l.getRespondidas();
            if (taxa >= LIMITE_DOMINADO) dominado++;
            else if (taxa >= LIMITE_ATENCAO) atencao++;
            else revisar++;
        }

        int total = linhas.size();
        int iniciados = total - vaiCair;
        double percentual = total == 0 ? 0.0 : Math.round(iniciados * 1000.0 / total) / 10.0;

        return new ObjetivoDTO.ProgressoDTO(total, iniciados, percentual, dominado, atencao, revisar, vaiCair);
    }

    public static List<ObjetivoDTO.DisciplinaProgressoDTO> agruparPorDisciplina(
            List<? extends ProgressoAssunto> linhas
    ) {
        // [0] = total de topicos, [1] = topicos ja iniciados
        Map<String, int[]> acumulado = new LinkedHashMap<>();
        for (var l : linhas) {
            int[] c = acumulado.computeIfAbsent(l.getDisciplina(), k -> new int[2]);
            c[0]++;
            if (l.getRespondidas() > 0) c[1]++;
        }

        List<ObjetivoDTO.DisciplinaProgressoDTO> resultado = new ArrayList<>();
        acumulado.forEach((disciplina, c) -> resultado.add(new ObjetivoDTO.DisciplinaProgressoDTO(
                disciplina, c[0], c[1],
                c[0] == 0 ? 0.0 : Math.round(c[1] * 1000.0 / c[0]) / 10.0
        )));
        return resultado;
    }
}

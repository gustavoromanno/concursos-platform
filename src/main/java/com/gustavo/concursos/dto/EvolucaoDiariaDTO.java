package com.gustavo.concursos.dto;

import java.time.LocalDate;

// Um ponto da serie temporal: quantas questoes o usuario respondeu naquele dia
// e quantas acertou. Alimenta um grafico de evolucao.
public record EvolucaoDiariaDTO(
        LocalDate dia,
        long respondidas,
        long acertos,
        double percentualAcerto
) {
    public static EvolucaoDiariaDTO de(LocalDate dia, long total, long acertos) {
        return new EvolucaoDiariaDTO(
                dia,
                total,
                acertos,
                DesempenhoDisciplinaDTO.percentual(acertos, total)
        );
    }
}

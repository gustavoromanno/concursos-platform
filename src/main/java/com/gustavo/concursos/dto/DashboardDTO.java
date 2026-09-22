package com.gustavo.concursos.dto;

import java.util.List;

// Visao consolidada do desempenho do usuario: numeros gerais + quebra por disciplina.
public record DashboardDTO(
        long totalRespondidas,
        long totalAcertos,
        long totalErros,
        double percentualAcertoGeral,
        List<DesempenhoDisciplinaDTO> porDisciplina
) {
    public static DashboardDTO de(long total, long acertos, List<DesempenhoDisciplinaDTO> porDisciplina) {
        return new DashboardDTO(
                total,
                acertos,
                total - acertos,
                DesempenhoDisciplinaDTO.percentual(acertos, total),
                porDisciplina
        );
    }
}

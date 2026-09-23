package com.gustavo.concursos.dto;

import java.util.List;

// Como a comunidade se saiu nesta questao.
public record EstatisticaQuestaoDTO(
        long totalRespostas,
        long totalAcertos,
        double percentualAcerto,
        String dificuldade,
        List<FatiaAlternativa> distribuicao
) {
    public record FatiaAlternativa(
            Long alternativaId,
            String letra,
            long total,
            double percentual,
            boolean correta
    ) {}
}

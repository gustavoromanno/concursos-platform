package com.gustavo.concursos.dto;

// Desempenho do usuario em uma disciplina especifica.
public record DesempenhoDisciplinaDTO(
        String disciplina,
        long respondidas,
        long acertos,
        long erros,
        double percentualAcerto
) {
    public static DesempenhoDisciplinaDTO de(String disciplina, long total, long acertos) {
        return new DesempenhoDisciplinaDTO(
                disciplina,
                total,
                acertos,
                total - acertos,
                percentual(acertos, total)
        );
    }

    static double percentual(long acertos, long total) {
        if (total == 0) {
            return 0.0;
        }
        // Arredonda para uma casa decimal.
        return Math.round((acertos * 1000.0 / total)) / 10.0;
    }
}

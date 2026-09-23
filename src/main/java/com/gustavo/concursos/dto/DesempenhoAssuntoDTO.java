package com.gustavo.concursos.dto;

// Desempenho por topico. Vem ordenado do pior para o melhor, entao o topo
// da lista e exatamente o que o usuario precisa revisar.
public record DesempenhoAssuntoDTO(
        String assunto,
        String disciplina,
        long respondidas,
        long acertos,
        long erros,
        double percentualAcerto
) {
    public static DesempenhoAssuntoDTO de(String assunto, String disciplina, long total, long acertos) {
        return new DesempenhoAssuntoDTO(
                assunto,
                disciplina,
                total,
                acertos,
                total - acertos,
                DesempenhoDisciplinaDTO.percentual(acertos, total)
        );
    }
}

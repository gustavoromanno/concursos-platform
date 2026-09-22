package com.gustavo.concursos.dto;

// Retornado depois que o usuario responde: aqui SIM mostramos se acertou e a explicacao.
public record RespostaResultDTO(
        boolean correta,
        Long alternativaCorretaId,
        String explicacao
) {
}

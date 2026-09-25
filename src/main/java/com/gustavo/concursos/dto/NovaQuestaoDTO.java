package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Cadastro de questão.
 *
 * Em CERTO_ERRADO, "alternativas" é ignorado e o campo "gabaritoCerto" define
 * a resposta — o sistema cria as duas alternativas sozinho.
 * Em MULTIPLA_ESCOLHA, vale o contrário.
 */
public record NovaQuestaoDTO(
        @NotBlank String enunciado,
        @NotNull Long disciplinaId,
        @NotNull Long bancaId,
        Long orgaoId,
        Long assuntoId,
        @NotNull Integer ano,
        String tipo,
        String explicacao,
        Boolean gabaritoCerto,
        List<AlternativaEntradaDTO> alternativas
) {
    public record AlternativaEntradaDTO(String texto, Boolean correta) {}
}

package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotNull;

public record ResponderQuestaoRequestDTO(
        @NotNull Long alternativaId
) {
}

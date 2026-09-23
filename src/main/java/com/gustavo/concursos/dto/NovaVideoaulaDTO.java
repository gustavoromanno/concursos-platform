package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// urlOuId aceita tanto a URL completa do YouTube quanto so o ID —
// o controller extrai o que precisa.
public record NovaVideoaulaDTO(
        @NotBlank String titulo,
        @NotBlank String urlOuId,
        String canal,
        Integer duracaoMinutos,
        @NotNull Long disciplinaId,
        Long assuntoId
) {}

package com.gustavo.concursos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// disciplinaId e bancaId sao opcionais: sem eles, sorteia de todo o banco.
public record CriarSimuladoRequestDTO(
        @NotNull @Min(1) @Max(100) Integer quantidade,
        @NotNull @Min(1) @Max(600) Integer duracaoMinutos,
        Long disciplinaId,
        Long bancaId
) {}

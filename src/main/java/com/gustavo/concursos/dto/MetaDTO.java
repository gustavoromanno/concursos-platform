package com.gustavo.concursos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MetaDTO(@NotNull @Min(1) @Max(500) Integer questoesPorDia) {}

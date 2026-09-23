package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotNull;

public record DefinirObjetivoDTO(@NotNull Long concursoId, Long cargoId) {}

package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NovoComentarioDTO(@NotBlank @Size(max = 2000) String texto) {}

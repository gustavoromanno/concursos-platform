package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NovoCadernoDTO(@NotBlank @Size(max = 120) String nome) {}

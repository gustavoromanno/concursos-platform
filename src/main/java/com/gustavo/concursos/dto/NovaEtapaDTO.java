package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record NovaEtapaDTO(
        @NotBlank String nome,
        LocalDate dataPrevista,
        String status,
        Integer ordem
) {}

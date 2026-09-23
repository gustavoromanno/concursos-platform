package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record NovoConcursoDTO(
        @NotBlank String nome,
        String orgao,
        Long bancaId,
        @NotNull Integer ano,
        String situacao,
        Integer vagas,
        LocalDate inscricoesAte
) {}

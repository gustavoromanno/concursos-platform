package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record NovoCargoDTO(
        @NotBlank String nome,
        String nivel,
        Integer vagas,
        Integer cadastroReserva,
        BigDecimal salario,
        List<Long> disciplinaIds
) {}

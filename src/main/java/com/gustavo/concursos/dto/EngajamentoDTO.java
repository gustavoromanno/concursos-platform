package com.gustavo.concursos.dto;

// Visao de constancia do usuario.
public record EngajamentoDTO(
        int metaDiaria,
        long respondidasHoje,
        boolean metaBatidaHoje,
        int ofensivaAtual,
        int melhorOfensiva,
        int diasEstudadosNoMes,
        long totalDiasComEstudo
) {}

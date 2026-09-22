package com.gustavo.concursos.dto;

import java.util.List;

// Resultado final, entregue quando o simulado e finalizado.
public record ResultadoSimuladoDTO(
        Long simuladoId,
        int totalQuestoes,
        int respondidas,
        int naoRespondidas,
        int acertos,
        int erros,
        double percentualAcerto,
        long minutosGastos,
        List<DesempenhoDisciplinaDTO> porDisciplina
) {}

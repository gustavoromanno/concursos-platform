package com.gustavo.concursos.dto;

import java.time.LocalDateTime;
import java.util.List;

// Estado do simulado em andamento. As questoes vem sem o gabarito,
// como na listagem normal.
public record SimuladoResponseDTO(
        Long id,
        LocalDateTime criadoEm,
        LocalDateTime prazo,
        long segundosRestantes,
        boolean aberto,
        int totalQuestoes,
        int respondidas,
        List<QuestaoResponseDTO> questoes
) {}

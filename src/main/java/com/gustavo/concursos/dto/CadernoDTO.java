package com.gustavo.concursos.dto;

import java.time.LocalDateTime;
import java.util.List;

// questoes vem nulo na listagem (so o resumo) e preenchido ao abrir um caderno.
public record CadernoDTO(
        Long id,
        String nome,
        LocalDateTime criadoEm,
        long totalQuestoes,
        List<QuestaoResponseDTO> questoes
) {
    public static CadernoDTO resumo(Long id, String nome, LocalDateTime criadoEm, long total) {
        return new CadernoDTO(id, nome, criadoEm, total, null);
    }
}

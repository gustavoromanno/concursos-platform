package com.gustavo.concursos.dto;

import java.util.List;

// Prova aberta: cabecalho + questoes numeradas como na prova original.
public record ProvaDTO(
        Long id,
        String concurso,
        String orgao,
        String banca,
        Integer ano,
        String cargo,
        String nivel,
        int totalQuestoes,
        List<ItemProvaDTO> questoes
) {
    public record ItemProvaDTO(int numero, QuestaoResponseDTO questao) {}
}

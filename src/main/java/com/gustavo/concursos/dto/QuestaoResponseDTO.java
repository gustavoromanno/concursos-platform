package com.gustavo.concursos.dto;

import com.gustavo.concursos.entity.Questao;

import java.util.List;

public record QuestaoResponseDTO(
        Long id,
        String enunciado,
        String disciplina,
        String banca,
        Integer ano,
        String assunto,
        List<AlternativaResponseDTO> alternativas
) {

    public static QuestaoResponseDTO fromEntity(Questao questao) {
        return new QuestaoResponseDTO(
                questao.getId(),
                questao.getEnunciado(),
                questao.getDisciplina().getNome(),
                questao.getBanca().getNome(),
                questao.getAno(),
                questao.getAssunto(),
                questao.getAlternativas().stream()
                        .map(AlternativaResponseDTO::fromEntity)
                        .toList()
        );
    }
}

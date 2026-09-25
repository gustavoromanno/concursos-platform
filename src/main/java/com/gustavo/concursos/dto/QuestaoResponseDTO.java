package com.gustavo.concursos.dto;

import com.gustavo.concursos.entity.Questao;

import java.util.List;

public record QuestaoResponseDTO(
        Long id,
        String enunciado,
        String disciplina,
        String banca,
        String orgao,
        Integer ano,
        String assunto,
        String tipo,
        List<AlternativaResponseDTO> alternativas
) {
    public static QuestaoResponseDTO fromEntity(Questao questao) {
        return new QuestaoResponseDTO(
                questao.getId(),
                questao.getEnunciado(),
                questao.getDisciplina().getNome(),
                questao.getBanca().getNome(),
                questao.getOrgao() != null ? questao.getOrgao().getNome() : null,
                questao.getAno(),
                questao.getAssunto(),
                questao.getTipo(),
                questao.getAlternativas().stream()
                        .map(AlternativaResponseDTO::fromEntity)
                        .toList()
        );
    }
}

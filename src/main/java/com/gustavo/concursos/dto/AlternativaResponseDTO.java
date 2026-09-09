package com.gustavo.concursos.dto;

import com.gustavo.concursos.entity.Alternativa;

// Propositalmente NAO inclui o campo "correta" -- quem consome /questoes
// para responder nao pode ver o gabarito antes de enviar a resposta.
// O gabarito so aparece na resposta do endpoint de correcao (Sprint 3).
public record AlternativaResponseDTO(Long id, String texto) {

    public static AlternativaResponseDTO fromEntity(Alternativa alternativa) {
        return new AlternativaResponseDTO(alternativa.getId(), alternativa.getTexto());
    }
}

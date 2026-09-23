package com.gustavo.concursos.dto;

import com.gustavo.concursos.entity.Assunto;

import java.util.List;

public record AssuntoDTO(Long id, String nome, List<AssuntoDTO> subassuntos) {

    public static AssuntoDTO fromEntity(Assunto assunto) {
        return new AssuntoDTO(
                assunto.getId(),
                assunto.getNome(),
                assunto.getSubassuntos().stream().map(AssuntoDTO::fromEntity).toList()
        );
    }
}

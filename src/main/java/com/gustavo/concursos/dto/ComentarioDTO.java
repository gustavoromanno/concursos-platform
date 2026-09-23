package com.gustavo.concursos.dto;

import com.gustavo.concursos.entity.Comentario;

import java.time.LocalDateTime;

public record ComentarioDTO(
        Long id,
        String autor,
        String texto,
        LocalDateTime criadoEm,
        boolean meu
) {
    public static ComentarioDTO fromEntity(Comentario c, Long usuarioLogadoId) {
        return new ComentarioDTO(
                c.getId(),
                c.getUsuario().getNome(),
                c.getTexto(),
                c.getCriadoEm(),
                c.getUsuario().getId().equals(usuarioLogadoId)
        );
    }
}

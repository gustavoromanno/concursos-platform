package com.gustavo.concursos.dto;

import com.gustavo.concursos.entity.Videoaula;

public record VideoaulaDTO(
        Long id,
        String titulo,
        String youtubeId,
        String url,
        String thumbnail,
        String canal,
        Integer duracaoMinutos,
        String disciplina,
        String assunto
) {
    public static VideoaulaDTO fromEntity(Videoaula v) {
        return new VideoaulaDTO(
                v.getId(),
                v.getTitulo(),
                v.getYoutubeId(),
                "https://www.youtube.com/watch?v=" + v.getYoutubeId(),
                "https://img.youtube.com/vi/" + v.getYoutubeId() + "/mqdefault.jpg",
                v.getCanal(),
                v.getDuracaoMinutos(),
                v.getDisciplina().getNome(),
                v.getAssunto() != null ? v.getAssunto().getNome() : null
        );
    }
}

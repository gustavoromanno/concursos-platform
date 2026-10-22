package com.gustavo.concursos.youtube;

import java.util.List;

// Busca de videos no YouTube. Interface para os testes usarem uma busca falsa (sem rede).
public interface YoutubeBusca {

    record VideoEncontrado(String youtubeId, String titulo, String canal, String miniatura, Integer duracaoMinutos) {
    }

    List<VideoEncontrado> buscar(String termo, int maximo);
}

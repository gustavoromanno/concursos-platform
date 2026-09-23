package com.gustavo.concursos.dto;

import java.time.LocalDate;

// Um quadradinho do mapa de estudo. "nivel" (0 a 4) e o quanto pintar,
// calculado a partir do progresso em relacao a meta diaria.
public record DiaEstudoDTO(LocalDate dia, long respondidas, long acertos, int nivel) {}

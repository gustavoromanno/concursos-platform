package com.gustavo.concursos.dto;

import java.time.LocalDate;
import java.util.List;

public record ResumoRevisaoDTO(
        long paraHoje,
        long agendadasTotal,
        List<DiaAgendaDTO> proximosDias
) {
    public record DiaAgendaDTO(LocalDate dia, long total) {}
}

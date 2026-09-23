package com.gustavo.concursos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ConcursoDTO(
        Long id,
        String nome,
        String orgao,
        String banca,
        Integer ano,
        String situacao,
        Integer vagas,
        LocalDate inscricoesDe,
        LocalDate inscricoesAte,
        BigDecimal taxa,
        String editalUrl,
        String observacoes,
        List<ProvaResumoDTO> provas,
        List<EtapaResumoDTO> etapas,
        List<CargoDTO> cargos
) {
    public record ProvaResumoDTO(Long id, String cargo, String nivel, LocalDate aplicadaEm, int totalQuestoes) {}
    public record EtapaResumoDTO(Long id, String nome, LocalDate dataPrevista, String status, Integer diasRestantes) {}
}

package com.gustavo.concursos.dto;

import java.time.LocalDate;
import java.util.List;

// "Seu objetivo atual": concurso alvo + progresso real no conteúdo programático.
public record ObjetivoDTO(
        Long concursoId,
        String concurso,
        String orgao,
        String banca,
        Integer ano,
        String situacao,
        Long cargoId,
        String cargo,
        String nivel,
        Integer vagas,
        LocalDate inscricoesAte,
        ProgressoDTO progresso,
        List<DisciplinaProgressoDTO> porDisciplina,
        EtapaDTO proximaEtapa
) {
    // Os quatro contadores saem da taxa de acerto por assunto:
    //   dominado  >= 80%   | atencao 50–79%   | revisar < 50%   | vaiCair: sem resposta
    public record ProgressoDTO(
            int totalTopicos,
            int topicosIniciados,
            double percentual,
            int dominado,
            int atencao,
            int revisar,
            int vaiCair
    ) {}

    public record DisciplinaProgressoDTO(
            String disciplina,
            int totalTopicos,
            int iniciados,
            double percentual
    ) {}

    public record EtapaDTO(String nome, LocalDate data, String status, Integer diasRestantes) {}
}

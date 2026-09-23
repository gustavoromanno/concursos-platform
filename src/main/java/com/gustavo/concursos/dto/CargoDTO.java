package com.gustavo.concursos.dto;

import java.math.BigDecimal;
import java.util.List;

public record CargoDTO(
        Long id,
        String nome,
        String nivel,
        Integer vagas,
        Integer cadastroReserva,
        BigDecimal salario,
        List<ConteudoDTO> conteudo
) {
    public record ConteudoDTO(String disciplina, Integer totalTopicos) {}
}

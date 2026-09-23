package com.gustavo.concursos.dto;

import java.util.List;

// Disciplina com seus assuntos aninhados: e o que o frontend usa para montar
// os selects de filtro em cascata (escolhe disciplina -> carrega assuntos).
public record DisciplinaDTO(Long id, String nome, List<AssuntoDTO> assuntos) {}

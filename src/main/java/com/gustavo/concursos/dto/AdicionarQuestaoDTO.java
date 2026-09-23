package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotNull;

public record AdicionarQuestaoDTO(@NotNull Long questaoId) {}

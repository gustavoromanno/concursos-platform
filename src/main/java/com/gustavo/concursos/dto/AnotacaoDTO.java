package com.gustavo.concursos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class AnotacaoDTO {

    private AnotacaoDTO() {
    }

    // Texto em branco apaga a anotacao.
    public record Salvar(@NotNull @Size(max = 5000) String texto) {
    }

    public record Resposta(String texto, LocalDateTime atualizadoEm) {
    }
}

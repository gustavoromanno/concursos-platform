package com.gustavo.concursos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Informe seu e-mail") @Email(message = "Informe um e-mail válido") String email,
        @NotBlank(message = "Informe sua senha") String senha
) {
    // Mesmo tratamento do cadastro: o login aceita o e-mail escrito de qualquer jeito.
    public LoginRequestDTO {
        email = com.gustavo.concursos.entity.Usuario.normalizarEmail(email);
    }
}

package com.gustavo.concursos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Informe seu e-mail") @Email(message = "Informe um e-mail válido") String email,
        @NotBlank(message = "Informe sua senha") String senha
) {
}

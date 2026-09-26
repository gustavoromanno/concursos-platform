package com.gustavo.concursos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroRequestDTO(
        @NotBlank(message = "Informe seu nome")
        @Size(max = 150, message = "Nome muito longo")
        String nome,

        @NotBlank(message = "Informe seu e-mail")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 150, message = "E-mail muito longo")
        String email,

        @NotBlank(message = "Informe uma senha")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\b\\[\\]{};':\"\\|,.<>\\/?]).{6,}$",
                message = "A senha deve ter pelo menos 6 caracteres, 1 letra maiúscula, 1 número e 1 caractere especial"
        )
        String senha,

        Boolean aceitaMarketing
) {
    public RegistroRequestDTO {
        email = com.gustavo.concursos.entity.Usuario.normalizarEmail(email);
        nome = nome == null ? null : nome.strip();
    }
}

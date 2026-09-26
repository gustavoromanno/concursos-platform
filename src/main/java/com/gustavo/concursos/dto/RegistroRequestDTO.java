package com.gustavo.concursos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
        @Size(min = 6, max = 100, message = "A senha deve ter pelo menos 6 caracteres")
        String senha,

        // Opt-in para e-mails promocionais. Ausente = nao aceita.
        Boolean aceitaMarketing
) {
}

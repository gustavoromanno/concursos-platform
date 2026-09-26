package com.gustavo.concursos.dto;

/**
 * Regra unica de senha, usada no cadastro e na troca de senha (o frontend
 * aplica a mesma regra para dar retorno imediato).
 *
 * - 6 a 64 caracteres: o BCrypt so considera os primeiros 72 bytes e o
 *   Spring recusa senhas maiores; 64 caracteres deixa folga para acentos.
 * - ao menos 1 letra maiuscula, 1 numero e 1 simbolo (qualquer caractere
 *   que nao seja letra sem acento, numero ou espaco).
 */
public final class RegrasSenha {

    public static final String REGEX = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s]).{6,64}$";

    public static final String MENSAGEM =
            "A senha deve ter de 6 a 64 caracteres, com pelo menos 1 letra maiúscula, 1 número e 1 caractere especial";

    private RegrasSenha() {
    }
}

package com.gustavo.concursos.email;

import java.util.Map;

// Envio de e-mail. Interface para os testes capturarem o e-mail sem enviar.
public interface EmailCliente {

    void enviar(String para, String assunto, String html);

    // Com cabecalhos extras (ex.: List-Unsubscribe nos e-mails promocionais).
    default void enviar(String para, String assunto, String html, Map<String, String> cabecalhos) {
        enviar(para, assunto, html);
    }
}

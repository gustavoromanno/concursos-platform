package com.gustavo.concursos.email;

// Envio de e-mail transacional. Interface para os testes capturarem o e-mail sem enviar.
public interface EmailCliente {

    void enviar(String para, String assunto, String html);
}

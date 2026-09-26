package com.gustavo.concursos.ia;

// Falha ao falar com o modelo (chave ausente, erro da API, resposta cortada...).
public class IaException extends RuntimeException {

    public IaException(String mensagem) {
        super(mensagem);
    }

    public IaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

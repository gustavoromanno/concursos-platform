package com.gustavo.concursos.ia;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Acesso ao modelo de IA. A resposta sempre vem pelo uso forcado de UMA
 * ferramenta, cujo esquema JSON define exatamente o formato devolvido —
 * assim o backend nunca precisa "interpretar" texto livre.
 *
 * E uma interface para os testes trocarem a API real por respostas prontas.
 */
public interface IaCliente {

    RespostaFerramenta chamar(PedidoFerramenta pedido);

    record PedidoFerramenta(
            String sistema,
            List<Map<String, Object>> conteudo,
            String nomeFerramenta,
            String descricaoFerramenta,
            Map<String, Object> esquema,
            int maxTokens
    ) {
    }

    record RespostaFerramenta(JsonNode dados, long tokensEntrada, long tokensSaida) {
    }

    // PDF como bloco de documento. cache_control permite reaproveitar o mesmo
    // PDF nas chamadas seguintes da importacao pagando menos por ele.
    static Map<String, Object> pdf(byte[] bytes) {
        return Map.of(
                "type", "document",
                "source", Map.of(
                        "type", "base64",
                        "media_type", "application/pdf",
                        "data", Base64.getEncoder().encodeToString(bytes)
                ),
                "cache_control", Map.of("type", "ephemeral")
        );
    }

    static Map<String, Object> texto(String texto) {
        return Map.of("type", "text", "text", texto);
    }
}

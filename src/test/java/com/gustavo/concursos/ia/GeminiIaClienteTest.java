package com.gustavo.concursos.ia;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Traducao do pedido comum para o formato do Gemini e leitura da resposta (sem rede). */
class GeminiIaClienteTest {

    private final ObjectMapper json = new ObjectMapper();
    private final GeminiIaCliente cliente = new GeminiIaCliente("chave-falsa", "gemini-3.8-flash", "gemini-3.6-flash", json);

    private IaCliente.PedidoFerramenta pedido() {
        Map<String, Object> esquema = Map.of("type", "object",
                "properties", Map.of("sigla", Map.of("type", "string", "description", "Sigla")),
                "required", List.of("sigla"));
        return new IaCliente.PedidoFerramenta("Voce extrai dados de provas.",
                List.of(IaCliente.pdf(new byte[]{1, 2, 3}), IaCliente.texto("Leia a prova.")),
                "registrar_metadados", "Registra os dados gerais da prova.", esquema, 2000);
    }

    @Test
    void montaPedidoComPdfInlineEFuncaoObrigatoria() {
        JsonNode corpo = json.valueToTree(cliente.montarCorpo(pedido()));

        assertThat(corpo.at("/systemInstruction/parts/0/text").asString()).isEqualTo("Voce extrai dados de provas.");
        JsonNode partes = corpo.at("/contents/0/parts");
        assertThat(partes.get(0).at("/inline_data/mime_type").asString()).isEqualTo("application/pdf");
        assertThat(partes.get(0).at("/inline_data/data").asString()).isEqualTo("AQID");
        assertThat(partes.get(1).path("text").asString()).isEqualTo("Leia a prova.");
        assertThat(corpo.at("/tools/0/functionDeclarations/0/name").asString()).isEqualTo("registrar_metadados");
        assertThat(corpo.at("/tools/0/functionDeclarations/0/parameters/required/0").asString()).isEqualTo("sigla");
        assertThat(corpo.at("/toolConfig/functionCallingConfig/mode").asString()).isEqualTo("ANY");
        assertThat(corpo.at("/toolConfig/functionCallingConfig/allowedFunctionNames/0").asString()).isEqualTo("registrar_metadados");
        assertThat(corpo.at("/generationConfig/maxOutputTokens").asInt()).isGreaterThan(2000);
    }

    @Test
    void leArgumentosDaFuncaoETokens() {
        String resposta = """
                {"candidates":[{"content":{"parts":[{"functionCall":{"name":"registrar_metadados","args":{"sigla":"Bacen"}}}]},
                  "finishReason":"STOP"}],
                 "usageMetadata":{"promptTokenCount":1200,"candidatesTokenCount":40,"thoughtsTokenCount":300}}""";
        IaCliente.RespostaFerramenta r = cliente.interpretar(resposta, "registrar_metadados");
        assertThat(r.dados().path("sigla").asString()).isEqualTo("Bacen");
        assertThat(r.tokensEntrada()).isEqualTo(1200);
        assertThat(r.tokensSaida()).isEqualTo(340);
    }

    @Test
    void respostaCortadaOuBloqueadaViraErroClaro() {
        assertThatThrownBy(() -> cliente.interpretar(
                "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"...\"}]},\"finishReason\":\"MAX_TOKENS\"}]}",
                "registrar_metadados"))
                .isInstanceOf(IaException.class).hasMessageContaining("cortada");
        assertThatThrownBy(() -> cliente.interpretar(
                "{\"promptFeedback\":{\"blockReason\":\"SAFETY\"}}", "registrar_metadados"))
                .isInstanceOf(IaException.class).hasMessageContaining("SAFETY");
    }

    @Test
    void semChaveAvisaSemChamarARede() {
        GeminiIaCliente semChave = new GeminiIaCliente("", "gemini-3.8-flash", "gemini-3.6-flash", json);
        assertThatThrownBy(() -> semChave.chamar(pedido()))
                .isInstanceOf(IaException.class).hasMessageContaining("GEMINI_API_KEY");
    }
}

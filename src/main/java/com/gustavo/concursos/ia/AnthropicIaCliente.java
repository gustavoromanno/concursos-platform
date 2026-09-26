package com.gustavo.concursos.ia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cliente da API de Mensagens da Anthropic, via HTTP puro (sem SDK).
 *
 * A chave vem da variavel ANTHROPIC_API_KEY e nunca e registrada em log.
 * Erros transitorios (limite de taxa, sobrecarga) sao repetidos com espera
 * crescente; os demais viram IaException com a mensagem da API.
 */
@Component
public class AnthropicIaCliente implements IaCliente {

    private static final Logger log = LoggerFactory.getLogger(AnthropicIaCliente.class);
    private static final URI URL = URI.create("https://api.anthropic.com/v1/messages");
    private static final String VERSAO_API = "2023-06-01";
    private static final Set<Integer> TRANSITORIOS = Set.of(429, 500, 502, 503, 504, 529);
    private static final int TENTATIVAS = 4;

    private final String chave;
    private final String modelo;
    private final ObjectMapper json;
    private final HttpClient http;

    public AnthropicIaCliente(
            @Value("${ia.anthropic.api-key:}") String chave,
            @Value("${ia.modelo:claude-sonnet-5}") String modelo,
            ObjectMapper json
    ) {
        this.chave = chave;
        this.modelo = modelo;
        this.json = json;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build();
    }

    @Override
    public RespostaFerramenta chamar(PedidoFerramenta pedido) {
        if (chave == null || chave.isBlank()) {
            throw new IaException("A chave da API (ANTHROPIC_API_KEY) nao esta configurada no servidor.");
        }

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("model", modelo);
        corpo.put("max_tokens", pedido.maxTokens());
        corpo.put("system", pedido.sistema());
        corpo.put("tools", List.of(Map.of(
                "name", pedido.nomeFerramenta(),
                "description", pedido.descricaoFerramenta(),
                "input_schema", pedido.esquema()
        )));
        corpo.put("tool_choice", Map.of("type", "tool", "name", pedido.nomeFerramenta()));
        corpo.put("messages", List.of(Map.of("role", "user", "content", pedido.conteudo())));

        String corpoJson;
        try {
            corpoJson = json.writeValueAsString(corpo);
        } catch (IOException e) {
            throw new IaException("Falha ao montar a requisicao para a IA.", e);
        }

        HttpRequest requisicao = HttpRequest.newBuilder(URL)
                .timeout(Duration.ofMinutes(10))
                .header("content-type", "application/json")
                .header("x-api-key", chave)
                .header("anthropic-version", VERSAO_API)
                .POST(HttpRequest.BodyPublishers.ofString(corpoJson))
                .build();

        HttpResponse<String> resposta = enviarComRepeticao(requisicao);
        return interpretar(resposta.body(), pedido.nomeFerramenta());
    }

    private HttpResponse<String> enviarComRepeticao(HttpRequest requisicao) {
        for (int tentativa = 1; ; tentativa++) {
            HttpResponse<String> resposta;
            try {
                resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                if (tentativa >= TENTATIVAS) throw new IaException("Sem conexao com a API da IA: " + e.getMessage(), e);
                esperar(tentativa, null);
                continue;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IaException("Chamada a IA interrompida.", e);
            }

            int status = resposta.statusCode();
            if (status >= 200 && status < 300) return resposta;

            if (TRANSITORIOS.contains(status) && tentativa < TENTATIVAS) {
                log.warn("API da IA respondeu {} (tentativa {}/{}); repetindo.", status, tentativa, TENTATIVAS);
                esperar(tentativa, resposta.headers().firstValue("retry-after").orElse(null));
                continue;
            }
            throw new IaException("A API da IA respondeu " + status + ": " + mensagemDeErro(resposta.body()));
        }
    }

    private void esperar(int tentativa, String retryAfter) {
        long segundos = 5L << (tentativa - 1);   // 5, 10, 20...
        if (retryAfter != null) {
            try {
                segundos = Math.max(segundos, Long.parseLong(retryAfter.trim()));
            } catch (NumberFormatException ignorado) {
                // cabecalho em formato de data: fica a espera padrao
            }
        }
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IaException("Espera entre tentativas interrompida.", e);
        }
    }

    private String mensagemDeErro(String corpo) {
        try {
            JsonNode raiz = json.readTree(corpo);
            String msg = raiz.path("error").path("message").asText("");
            return msg.isBlank() ? "erro sem detalhes" : msg;
        } catch (IOException e) {
            return "erro sem detalhes";
        }
    }

    private RespostaFerramenta interpretar(String corpo, String nomeFerramenta) {
        JsonNode raiz;
        try {
            raiz = json.readTree(corpo);
        } catch (IOException e) {
            throw new IaException("Resposta da IA em formato inesperado.", e);
        }

        if ("max_tokens".equals(raiz.path("stop_reason").asText())) {
            throw new IaException("A resposta da IA foi cortada por excesso de tamanho.");
        }

        JsonNode uso = raiz.path("usage");
        long entrada = uso.path("input_tokens").asLong(0)
                + uso.path("cache_creation_input_tokens").asLong(0)
                + uso.path("cache_read_input_tokens").asLong(0);
        long saida = uso.path("output_tokens").asLong(0);

        for (JsonNode bloco : raiz.path("content")) {
            if ("tool_use".equals(bloco.path("type").asText())
                    && nomeFerramenta.equals(bloco.path("name").asText())) {
                return new RespostaFerramenta(bloco.path("input"), entrada, saida);
            }
        }
        throw new IaException("A IA nao devolveu os dados no formato esperado.");
    }
}

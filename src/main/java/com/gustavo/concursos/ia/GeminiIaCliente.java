package com.gustavo.concursos.ia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cliente da API Gemini (Google AI Studio), via HTTP puro (sem SDK).
 * Ativo com IA_PROVEDOR=gemini; a chave vem de GEMINI_API_KEY e nunca vai para o log.
 *
 * Traduz o pedido do formato comum (IaCliente) para o do Gemini:
 *  - sistema           -> systemInstruction
 *  - bloco "document"  -> inline_data (PDF em base64)
 *  - ferramenta        -> functionDeclarations + chamada obrigatoria (mode ANY)
 * A resposta volta como functionCall.args, no mesmo esquema JSON pedido.
 */
@Component
@ConditionalOnProperty(name = "ia.provedor", havingValue = "gemini")
public class GeminiIaCliente implements IaCliente {

    private static final Logger log = LoggerFactory.getLogger(GeminiIaCliente.class);
    private static final String BASE = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final Set<Integer> TRANSITORIOS = Set.of(429, 500, 502, 503, 504);
    private static final int TENTATIVAS = 4;
    // Limite de dados enviados direto na requisicao (PDFs em base64 inclusos).
    static final long LIMITE_REQUISICAO = 20L * 1024 * 1024;

    private final String chave;
    private final String modelo;
    private final ObjectMapper json;
    private final HttpClient http;

    public GeminiIaCliente(
            @Value("${ia.gemini.api-key:}") String chave,
            @Value("${ia.gemini.modelo:gemini-3.8-flash}") String modelo,
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
            throw new IaException("A chave da API do Gemini (GEMINI_API_KEY) nao esta configurada no servidor.");
        }
        String corpoJson;
        try {
            corpoJson = json.writeValueAsString(montarCorpo(pedido));
        } catch (IOException e) {
            throw new IaException("Falha ao montar a requisicao para a IA.", e);
        }
        if (corpoJson.length() > LIMITE_REQUISICAO) {
            throw new IaException("Os PDFs de prova e gabarito juntos passam do limite do Gemini (cerca de 14 MB). "
                    + "Reduza o tamanho dos arquivos (ex.: \"Reduzir tamanho do arquivo\" no leitor de PDF) e envie de novo.");
        }

        URI url = URI.create(BASE + URLEncoder.encode(modelo, StandardCharsets.UTF_8) + ":generateContent");
        HttpRequest requisicao = HttpRequest.newBuilder(url)
                .timeout(Duration.ofMinutes(10))
                .header("content-type", "application/json")
                .header("x-goog-api-key", chave)
                .POST(HttpRequest.BodyPublishers.ofString(corpoJson))
                .build();

        HttpResponse<String> resposta = enviarComRepeticao(requisicao);
        return interpretar(resposta.body(), pedido.nomeFerramenta());
    }

    Map<String, Object> montarCorpo(PedidoFerramenta pedido) {
        List<Map<String, Object>> partes = new ArrayList<>();
        for (Map<String, Object> bloco : pedido.conteudo()) {
            Object tipo = bloco.get("type");
            if ("document".equals(tipo)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> fonte = (Map<String, Object>) bloco.get("source");
                partes.add(Map.of("inline_data", Map.of(
                        "mime_type", fonte.get("media_type"),
                        "data", fonte.get("data"))));
            } else if ("text".equals(tipo)) {
                partes.add(Map.of("text", bloco.get("text")));
            }
        }

        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("systemInstruction", Map.of("parts", List.of(Map.of("text", pedido.sistema()))));
        corpo.put("contents", List.of(Map.of("role", "user", "parts", partes)));
        corpo.put("tools", List.of(Map.of("functionDeclarations", List.of(Map.of(
                "name", pedido.nomeFerramenta(),
                "description", pedido.descricaoFerramenta(),
                "parameters", pedido.esquema())))));
        corpo.put("toolConfig", Map.of("functionCallingConfig", Map.of(
                "mode", "ANY",
                "allowedFunctionNames", List.of(pedido.nomeFerramenta()))));
        // Os modelos atuais "pensam" antes de responder e esse raciocinio conta no limite de saida:
        // da folga para a resposta nao ser cortada.
        corpo.put("generationConfig", Map.of("maxOutputTokens", pedido.maxTokens() * 2 + 8192));
        return corpo;
    }

    private HttpResponse<String> enviarComRepeticao(HttpRequest requisicao) {
        for (int tentativa = 1; ; tentativa++) {
            HttpResponse<String> resposta;
            try {
                resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                if (tentativa >= TENTATIVAS) throw new IaException("Sem conexao com a API do Gemini: " + e.getMessage(), e);
                esperar(tentativa);
                continue;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IaException("Chamada a IA interrompida.", e);
            }

            int status = resposta.statusCode();
            if (status >= 200 && status < 300) return resposta;

            if (TRANSITORIOS.contains(status) && tentativa < TENTATIVAS) {
                log.warn("API do Gemini respondeu {} (tentativa {}/{}); repetindo.", status, tentativa, TENTATIVAS);
                esperar(tentativa);
                continue;
            }
            throw new IaException("A API do Gemini respondeu " + status + ": " + mensagemDeErro(resposta.body()));
        }
    }

    private void esperar(int tentativa) {
        long segundos = 10L << (tentativa - 1);   // 10, 20, 40: o limite gratuito e por minuto
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IaException("Espera entre tentativas interrompida.", e);
        }
    }

    private String mensagemDeErro(String corpo) {
        try {
            String msg = json.readTree(corpo).path("error").path("message").asText("");
            return msg.isBlank() ? "erro sem detalhes" : msg;
        } catch (IOException e) {
            return "erro sem detalhes";
        }
    }

    RespostaFerramenta interpretar(String corpo, String nomeFerramenta) {
        JsonNode raiz;
        try {
            raiz = json.readTree(corpo);
        } catch (IOException e) {
            throw new IaException("Resposta da IA em formato inesperado.", e);
        }

        JsonNode uso = raiz.path("usageMetadata");
        long entrada = uso.path("promptTokenCount").asLong(0);
        long saida = uso.path("candidatesTokenCount").asLong(0) + uso.path("thoughtsTokenCount").asLong(0);

        JsonNode candidato = raiz.path("candidates").path(0);
        if (candidato.isMissingNode()) {
            String bloqueio = raiz.path("promptFeedback").path("blockReason").asText("");
            throw new IaException(bloqueio.isBlank()
                    ? "O Gemini nao devolveu resposta."
                    : "O Gemini recusou o pedido (" + bloqueio + ").");
        }
        for (JsonNode parte : candidato.path("content").path("parts")) {
            JsonNode chamada = parte.path("functionCall");
            if (nomeFerramenta.equals(chamada.path("name").asText())) {
                return new RespostaFerramenta(chamada.path("args"), entrada, saida);
            }
        }
        if ("MAX_TOKENS".equals(candidato.path("finishReason").asText())) {
            throw new IaException("A resposta da IA foi cortada por excesso de tamanho.");
        }
        throw new IaException("A IA nao devolveu os dados no formato esperado (" + candidato.path("finishReason").asText("?") + ").");
    }
}

package com.gustavo.concursos.ia;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
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
    private static final int TENTATIVAS = 6;   // espera 15+30+60+120+120 s, cerca de 6 min
    // Limite de dados enviados direto na requisicao (PDFs em base64 inclusos).
    static final long LIMITE_REQUISICAO = 20L * 1024 * 1024;

    private final String chave;
    private final String modelo;
    private final String modeloReserva;
    private final ObjectMapper json;
    private final HttpClient http;

    public GeminiIaCliente(
            @Value("${ia.gemini.api-key:}") String chave,
            @Value("${ia.gemini.modelo:gemini-3.8-flash}") String modelo,
            @Value("${ia.gemini.modelo-reserva:gemini-3.6-flash}") String modeloReserva,
            ObjectMapper json
    ) {
        this.chave = chave;
        this.modelo = modelo;
        this.modeloReserva = modeloReserva == null ? "" : modeloReserva.strip();
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
        } catch (JacksonException e) {
            throw new IaException("Falha ao montar a requisicao para a IA.", e);
        }
        if (corpoJson.length() > LIMITE_REQUISICAO) {
            throw new IaException("Os PDFs de prova e gabarito juntos passam do limite do Gemini (cerca de 14 MB). "
                    + "Reduza o tamanho dos arquivos (ex.: \"Reduzir tamanho do arquivo\" no leitor de PDF) e envie de novo.");
        }

        // Se o modelo principal continuar sobrecarregado depois de todas as tentativas,
        // tenta o modelo reserva (GEMINI_MODELO_RESERVA; vazio desliga).
        List<String> modelos = new ArrayList<>(List.of(modelo));
        if (!modeloReserva.isEmpty() && !modeloReserva.equals(modelo)) modelos.add(modeloReserva);

        for (int i = 0; ; i++) {
            String m = modelos.get(i);
            URI url = URI.create(BASE + URLEncoder.encode(m, StandardCharsets.UTF_8) + ":generateContent");
            HttpRequest requisicao = HttpRequest.newBuilder(url)
                    .timeout(Duration.ofMinutes(10))
                    .header("content-type", "application/json")
                    .header("x-goog-api-key", chave)
                    .POST(HttpRequest.BodyPublishers.ofString(corpoJson))
                    .build();
            try {
                HttpResponse<String> resposta = enviarComRepeticao(requisicao);
                return interpretar(resposta.body(), pedido.nomeFerramenta());
            } catch (Sobrecarga e) {
                if (i + 1 >= modelos.size()) throw e;
                log.warn("Modelo {} {}; tentando o reserva {}.", m,
                        e instanceof CotaDiaria ? "sem cota diaria" : "sobrecarregado", modelos.get(i + 1));
            }
        }
    }

    // Erro temporario (sobrecarga/limite) que persistiu em todas as tentativas.
    static class Sobrecarga extends IaException {
        Sobrecarga(String mensagem) {
            super(mensagem);
        }
    }

    // Cota do dia acabou: so volta quando o dia virar no horario do Pacifico.
    static class CotaDiaria extends Sobrecarga {
        CotaDiaria(String mensagem) {
            super(mensagem);
        }
    }

    static final String MENSAGEM_COTA_DIARIA = "A cota diária da API do Gemini acabou (o plano gratuito permite "
            + "poucas chamadas por dia). Ela renova à meia-noite do horário do Pacífico (04:00 ou 05:00 em Brasília); "
            + "depois disso use \"Continuar de onde parou\". Para não depender desse limite, ative o faturamento "
            + "da chave no Google AI Studio.";

    /**
     * O 429 do Gemini traz em error.details as cotas estouradas (QuotaFailure), com ids como
     * "GenerateRequestsPerDayPerProjectPerModel-FreeTier". Limite por minuto ("PerMinute")
     * passa sozinho e segue a repeticao normal.
     */
    boolean cotaDiariaEsgotada(String corpo) {
        try {
            for (JsonNode detalhe : json.readTree(corpo).path("error").path("details")) {
                for (JsonNode v : detalhe.path("violations")) {
                    if (v.path("quotaId").asString("").contains("PerDay")) return true;
                }
            }
        } catch (JacksonException e) {
            return false;
        }
        return false;
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

            // Cota DIARIA esgotada (ex.: plano gratuito, 20 chamadas/dia por modelo): repetir
            // nao adianta ate o dia virar. Para na hora e deixa o modelo reserva tentar.
            if (status == 429 && cotaDiariaEsgotada(resposta.body())) {
                throw new CotaDiaria(MENSAGEM_COTA_DIARIA);
            }
            if (TRANSITORIOS.contains(status)) {
                if (tentativa < TENTATIVAS) {
                    log.warn("API do Gemini respondeu {} (tentativa {}/{}); repetindo.", status, tentativa, TENTATIVAS);
                    esperar(tentativa);
                    continue;
                }
                throw new Sobrecarga("A API do Gemini respondeu " + status + ": " + mensagemDeErro(resposta.body())
                        + " Use \"Continuar de onde parou\" daqui a alguns minutos.");
            }
            throw new IaException("A API do Gemini respondeu " + status + ": " + mensagemDeErro(resposta.body()));
        }
    }

    private void esperar(int tentativa) {
        long segundos = Math.min(120L, 15L << (tentativa - 1));   // 15, 30, 60, 120, 120
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IaException("Espera entre tentativas interrompida.", e);
        }
    }

    private String mensagemDeErro(String corpo) {
        try {
            String msg = json.readTree(corpo).path("error").path("message").asString("");
            return msg.isBlank() ? "erro sem detalhes" : msg;
        } catch (JacksonException e) {
            return "erro sem detalhes";
        }
    }

    RespostaFerramenta interpretar(String corpo, String nomeFerramenta) {
        JsonNode raiz;
        try {
            raiz = json.readTree(corpo);
        } catch (JacksonException e) {
            throw new IaException("Resposta da IA em formato inesperado.", e);
        }

        JsonNode uso = raiz.path("usageMetadata");
        long entrada = uso.path("promptTokenCount").asLong(0);
        long saida = uso.path("candidatesTokenCount").asLong(0) + uso.path("thoughtsTokenCount").asLong(0);

        JsonNode candidato = raiz.path("candidates").path(0);
        if (candidato.isMissingNode()) {
            String bloqueio = raiz.path("promptFeedback").path("blockReason").asString("");
            throw new IaException(bloqueio.isBlank()
                    ? "O Gemini nao devolveu resposta."
                    : "O Gemini recusou o pedido (" + bloqueio + ").");
        }
        for (JsonNode parte : candidato.path("content").path("parts")) {
            JsonNode chamada = parte.path("functionCall");
            if (nomeFerramenta.equals(chamada.path("name").asString())) {
                return new RespostaFerramenta(chamada.path("args"), entrada, saida);
            }
        }
        if ("MAX_TOKENS".equals(candidato.path("finishReason").asString())) {
            throw new IaException("A resposta da IA foi cortada por excesso de tamanho.");
        }
        throw new IaException("A IA nao devolveu os dados no formato esperado (" + candidato.path("finishReason").asString("?") + ").");
    }
}

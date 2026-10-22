package com.gustavo.concursos.youtube;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Busca pela YouTube Data API v3 (chave em YOUTUBE_API_KEY), so videos que podem
 * ser incorporados no site (videoEmbeddable=true), priorizando conteudo em portugues.
 * Custo de cota por busca: 100 unidades (search) + 1 (videos). A cota gratis e de
 * 10.000 unidades por dia, ou seja, cerca de 99 buscas por dia.
 */
@Component
public class YoutubeBuscaHttp implements YoutubeBusca {

    private static final String API = "https://www.googleapis.com/youtube/v3";

    private final String chave;
    private final ObjectMapper json;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();

    public YoutubeBuscaHttp(@Value("${youtube.api-key:}") String chave, ObjectMapper json) {
        this.chave = chave;
        this.json = json;
    }

    @Override
    public List<VideoEncontrado> buscar(String termo, int maximo) {
        if (chave == null || chave.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "A busca no YouTube não está configurada (YOUTUBE_API_KEY).");
        }
        JsonNode busca = get("/search?part=snippet&type=video&videoEmbeddable=true&safeSearch=strict"
                + "&regionCode=BR&relevanceLanguage=pt&maxResults=" + Math.min(Math.max(maximo, 1), 15)
                + "&q=" + codificar(termo));
        List<String> ids = new ArrayList<>();
        for (JsonNode item : busca.path("items")) {
            String id = item.path("id").path("videoId").asString("");
            if (!id.isBlank()) ids.add(id);
        }
        if (ids.isEmpty()) return List.of();

        // Duracao vem em outro endpoint (formato ISO 8601, ex.: PT1H6M59S).
        Map<String, Integer> duracoes = new HashMap<>();
        JsonNode detalhes = get("/videos?part=contentDetails&id=" + String.join(",", ids));
        for (JsonNode item : detalhes.path("items")) {
            try {
                long segundos = Duration.parse(item.path("contentDetails").path("duration").asString("")).getSeconds();
                duracoes.put(item.path("id").asString(""), (int) Math.max(1, Math.round(segundos / 60.0)));
            } catch (RuntimeException ignorada) {
                // sem duracao (ex.: transmissao ao vivo): fica nula
            }
        }

        List<VideoEncontrado> resultado = new ArrayList<>();
        for (JsonNode item : busca.path("items")) {
            String id = item.path("id").path("videoId").asString("");
            if (id.isBlank()) continue;
            JsonNode s = item.path("snippet");
            resultado.add(new VideoEncontrado(id,
                    textoHtml(s.path("title").asString("")),
                    textoHtml(s.path("channelTitle").asString("")),
                    s.path("thumbnails").path("medium").path("url").asString(""),
                    duracoes.get(id)));
        }
        return resultado;
    }

    private JsonNode get(String caminho) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(API + caminho + "&key=" + codificar(chave)))
                .timeout(Duration.ofSeconds(20)).GET().build();
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode corpo = json.readTree(resp.body());
            if (resp.statusCode() / 100 != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "O YouTube recusou a busca: " + corpo.path("error").path("message").asString("erro desconhecido"));
            }
            return corpo;
        } catch (IOException | JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Sem conexão com o YouTube. Tente de novo.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Busca interrompida.");
        }
    }

    private static String codificar(String texto) {
        return URLEncoder.encode(texto, StandardCharsets.UTF_8);
    }

    // A API devolve titulos com entidades HTML (&quot; &#39; &amp;).
    static String textoHtml(String s) {
        return s.replace("&quot;", "\"").replace("&#39;", "'").replace("&lt;", "<")
                .replace("&gt;", ">").replace("&amp;", "&");
    }
}

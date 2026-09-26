package com.gustavo.concursos.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Envio pelo Resend (https://resend.com), via HTTP.
 * RESEND_API_KEY e EMAIL_REMETENTE vem de variaveis de ambiente. Sem a chave,
 * o envio e so registrado em log (util em desenvolvimento).
 */
@Component
public class ResendEmailCliente implements EmailCliente {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailCliente.class);
    private static final URI URL = URI.create("https://api.resend.com/emails");

    private final String chave;
    private final String remetente;
    private final ObjectMapper json;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();

    public ResendEmailCliente(
            @Value("${email.resend.api-key:}") String chave,
            @Value("${email.remetente:Concursos Platform <onboarding@resend.dev>}") String remetente,
            ObjectMapper json
    ) {
        this.chave = chave;
        this.remetente = remetente;
        this.json = json;
    }

    @Override
    public void enviar(String para, String assunto, String html) {
        if (chave == null || chave.isBlank()) {
            log.warn("RESEND_API_KEY nao configurada: e-mail \"{}\" para {} NAO foi enviado.", assunto, para);
            return;
        }
        try {
            String corpo = json.writeValueAsString(Map.of(
                    "from", remetente, "to", List.of(para), "subject", assunto, "html", html));
            HttpRequest req = HttpRequest.newBuilder(URL)
                    .timeout(Duration.ofSeconds(20))
                    .header("Authorization", "Bearer " + chave)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(corpo))
                    .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() / 100 != 2) {
                log.error("Resend recusou o e-mail ({}): {}", resp.statusCode(), resp.body());
            }
        } catch (Exception e) {
            // Falha de envio nao derruba a requisicao: a pessoa pode pedir de novo.
            log.error("Falha ao enviar e-mail pelo Resend: {}", e.getMessage());
        }
    }
}

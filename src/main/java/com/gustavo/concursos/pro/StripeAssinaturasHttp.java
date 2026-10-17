package com.gustavo.concursos.pro;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Assinaturas pela API da Stripe via HTTP (sem SDK), como o StripeHttpCliente.
 *
 * Permissoes da chave restrita: alem de "Checkout Sessions: Write", precisa de
 * "Subscriptions: Write" para cancelar e reativar.
 */
@Component
public class StripeAssinaturasHttp implements StripeAssinaturas {

    private static final String API = "https://api.stripe.com/v1";

    private final String chave;
    private final ObjectMapper json;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    public StripeAssinaturasHttp(@Value("${stripe.chave-secreta:}") String chave, ObjectMapper json) {
        this.chave = chave;
        this.json = json;
    }

    @Override
    public StripeCliente.Sessao criarCheckoutAssinatura(long usuarioId, String email, String descricao, int valorCentavos,
                                                        Long inicioCobrancaEpoch, String urlSucesso, String urlCancelamento) {
        List<String> campos = new ArrayList<>();
        campos.add(par("mode", "subscription"));
        campos.add(par("customer_email", email));
        campos.add(par("client_reference_id", String.valueOf(usuarioId)));
        campos.add(par("metadata[usuario_id]", String.valueOf(usuarioId)));
        campos.add(par("subscription_data[metadata][usuario_id]", String.valueOf(usuarioId)));
        if (inicioCobrancaEpoch != null) campos.add(par("subscription_data[trial_end]", String.valueOf(inicioCobrancaEpoch)));
        campos.add(par("line_items[0][quantity]", "1"));
        campos.add(par("line_items[0][price_data][currency]", "brl"));
        campos.add(par("line_items[0][price_data][unit_amount]", String.valueOf(valorCentavos)));
        campos.add(par("line_items[0][price_data][recurring][interval]", "month"));
        campos.add(par("line_items[0][price_data][product_data][name]", descricao));
        campos.add(par("success_url", urlSucesso));
        campos.add(par("cancel_url", urlCancelamento));
        JsonNode corpo = enviar("POST", "/checkout/sessions", campos);
        return new StripeCliente.Sessao(corpo.path("id").asString(), corpo.path("url").asString());
    }

    @Override
    public void cancelarNoFimDoPeriodo(String assinaturaId, boolean cancelar) {
        enviar("POST", "/subscriptions/" + URLEncoder.encode(assinaturaId, StandardCharsets.UTF_8),
                List.of(par("cancel_at_period_end", String.valueOf(cancelar))));
    }

    @Override
    public void cancelarAgora(String assinaturaId) {
        enviar("DELETE", "/subscriptions/" + URLEncoder.encode(assinaturaId, StandardCharsets.UTF_8), List.of());
    }

    private JsonNode enviar(String metodo, String caminho, List<String> campos) {
        if (chave == null || chave.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Pagamentos ainda não estão disponíveis. Tente mais tarde.");
        }
        HttpRequest req = HttpRequest.newBuilder(URI.create(API + caminho))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString((chave + ":").getBytes(StandardCharsets.UTF_8)))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .method(metodo, HttpRequest.BodyPublishers.ofString(String.join("&", campos)))
                .build();
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode corpo = json.readTree(resp.body());
            if (resp.statusCode() / 100 != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "A Stripe recusou a operação: " + corpo.path("error").path("message").asString("erro desconhecido"));
            }
            return corpo;
        } catch (IOException | JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Sem conexão com a Stripe. Tente de novo.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Operação interrompida.");
        }
    }

    private static String par(String nome, String valor) {
        return URLEncoder.encode(nome, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }
}

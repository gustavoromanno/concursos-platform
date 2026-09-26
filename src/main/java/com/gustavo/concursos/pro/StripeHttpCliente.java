package com.gustavo.concursos.pro;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Cliente da API do Stripe via HTTP (formato form-urlencoded, sem SDK).
 * Checkout hospedado pelo Stripe, pagamento unico em reais.
 *
 * Formas de pagamento vem de STRIPE_METODOS (padrao: card,boleto). O Pix no
 * Stripe para empresas brasileiras depende de liberacao da propria Stripe:
 * quando a conta tiver Pix, acrescente "pix" na variavel.
 */
@Component
public class StripeHttpCliente implements StripeCliente {

    private static final URI URL = URI.create("https://api.stripe.com/v1/checkout/sessions");

    private final String chave;
    private final List<String> metodos;
    private final ObjectMapper json;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    public StripeHttpCliente(
            @Value("${stripe.chave-secreta:}") String chave,
            @Value("${stripe.metodos:card,boleto}") String metodos,
            ObjectMapper json
    ) {
        this.chave = chave;
        this.metodos = List.of(metodos.split("\\s*,\\s*"));
        this.json = json;
    }

    @Override
    public Sessao criarCheckout(long pagamentoId, String email, String descricao, int valorCentavos,
                                String urlSucesso, String urlCancelamento) {
        if (chave == null || chave.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Pagamentos ainda não estão disponíveis. Tente mais tarde.");
        }

        List<String> campos = new ArrayList<>();
        campos.add(par("mode", "payment"));
        campos.add(par("customer_email", email));
        campos.add(par("client_reference_id", String.valueOf(pagamentoId)));
        campos.add(par("metadata[pagamento_id]", String.valueOf(pagamentoId)));
        campos.add(par("line_items[0][quantity]", "1"));
        campos.add(par("line_items[0][price_data][currency]", "brl"));
        campos.add(par("line_items[0][price_data][unit_amount]", String.valueOf(valorCentavos)));
        campos.add(par("line_items[0][price_data][product_data][name]", descricao));
        for (int i = 0; i < metodos.size(); i++) {
            campos.add(par("payment_method_types[" + i + "]", metodos.get(i)));
        }
        if (metodos.contains("boleto")) campos.add(par("payment_method_options[boleto][expires_after_days]", "3"));
        campos.add(par("success_url", urlSucesso));
        campos.add(par("cancel_url", urlCancelamento));

        HttpRequest req = HttpRequest.newBuilder(URL)
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Basic " + Base64.getEncoder()
                        .encodeToString((chave + ":").getBytes(StandardCharsets.UTF_8)))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(String.join("&", campos)))
                .build();
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            JsonNode corpo = json.readTree(resp.body());
            if (resp.statusCode() / 100 != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "O Stripe recusou o pagamento: " + corpo.path("error").path("message").asText("erro desconhecido"));
            }
            return new Sessao(corpo.path("id").asText(), corpo.path("url").asText());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Sem conexão com o Stripe. Tente de novo.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Pagamento interrompido.");
        }
    }

    private static String par(String nome, String valor) {
        return URLEncoder.encode(nome, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }
}

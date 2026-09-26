package com.gustavo.concursos.pro;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

class AssinaturaWebhookTest {

    private static final String SEGREDO = "whsec_teste";
    private static final String CORPO = "{\"id\":\"evt_1\"}";

    static String assinar(String corpo, String segredo, long t) {
        return "t=" + t + ",v1=" + HexFormat.of().formatHex(AssinaturaWebhook.hmac(segredo, t + "." + corpo));
    }

    @Test
    void aceitaAssinaturaCorreta() {
        assertThat(AssinaturaWebhook.valida(CORPO, assinar(CORPO, SEGREDO, 1000), SEGREDO, 1000)).isTrue();
    }

    @Test
    void recusaSegredoErradoOuCorpoAlterado() {
        assertThat(AssinaturaWebhook.valida(CORPO, assinar(CORPO, "outro", 1000), SEGREDO, 1000)).isFalse();
        assertThat(AssinaturaWebhook.valida("{\"id\":\"evt_2\"}", assinar(CORPO, SEGREDO, 1000), SEGREDO, 1000)).isFalse();
    }

    @Test
    void recusaEventoAntigoReenviadoPorTerceiros() {
        assertThat(AssinaturaWebhook.valida(CORPO, assinar(CORPO, SEGREDO, 1000), SEGREDO, 1000 + 301)).isFalse();
    }

    @Test
    void recusaCabecalhoAusenteOuMalFormadoESegredoNaoConfigurado() {
        assertThat(AssinaturaWebhook.valida(CORPO, null, SEGREDO, 1000)).isFalse();
        assertThat(AssinaturaWebhook.valida(CORPO, "lixo", SEGREDO, 1000)).isFalse();
        assertThat(AssinaturaWebhook.valida(CORPO, "t=abc,v1=00", SEGREDO, 1000)).isFalse();
        assertThat(AssinaturaWebhook.valida(CORPO, assinar(CORPO, SEGREDO, 1000), "", 1000)).isFalse();
    }
}

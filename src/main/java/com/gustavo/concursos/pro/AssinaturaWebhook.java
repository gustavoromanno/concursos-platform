package com.gustavo.concursos.pro;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Verifica a assinatura do cabecalho Stripe-Signature ("t=...,v1=...").
 * Sem isso, qualquer pessoa poderia chamar o webhook e se dar dias de Pro.
 * Funcao pura, testada sem Spring.
 */
public final class AssinaturaWebhook {

    /** Janela aceita entre o envio pelo Stripe e o recebimento (anti-replay). */
    public static final long TOLERANCIA_SEGUNDOS = 300;

    private AssinaturaWebhook() {
    }

    public static boolean valida(String corpo, String cabecalho, String segredo, long agoraEpochSegundos) {
        if (corpo == null || cabecalho == null || segredo == null || segredo.isBlank()) return false;

        Long timestamp = null;
        java.util.List<String> assinaturas = new java.util.ArrayList<>();
        for (String parte : cabecalho.split(",")) {
            String[] kv = parte.trim().split("=", 2);
            if (kv.length != 2) continue;
            if (kv[0].equals("t")) {
                try {
                    timestamp = Long.parseLong(kv[1]);
                } catch (NumberFormatException e) {
                    return false;
                }
            } else if (kv[0].equals("v1")) {
                assinaturas.add(kv[1]);
            }
        }
        if (timestamp == null || assinaturas.isEmpty()) return false;
        if (Math.abs(agoraEpochSegundos - timestamp) > TOLERANCIA_SEGUNDOS) return false;

        byte[] esperada = hmac(segredo, timestamp + "." + corpo);
        for (String a : assinaturas) {
            try {
                if (MessageDigest.isEqual(esperada, HexFormat.of().parseHex(a))) return true;
            } catch (IllegalArgumentException ignorada) {
                // assinatura mal formada: tenta a proxima
            }
        }
        return false;
    }

    public static byte[] hmac(String segredo, String mensagem) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(mensagem.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("HMAC indisponivel", e);
        }
    }
}

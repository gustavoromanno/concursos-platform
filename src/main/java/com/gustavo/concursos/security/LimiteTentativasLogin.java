package com.gustavo.concursos.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Freio contra forca bruta no login: depois de muitas senhas erradas numa
 * janela de 15 minutos, bloqueia novas tentativas por um tempo.
 *
 * Conta separado por e-mail+IP (uma pessoa errando) e so por e-mail (varios
 * IPs atacando a mesma conta). Guardado em memoria: o site roda numa unica
 * instancia; um reinicio zera os contadores, o que e aceitavel.
 */
@Component
public class LimiteTentativasLogin {

    static final Duration JANELA = Duration.ofMinutes(15);
    static final int MAX_POR_EMAIL_E_IP = 5;
    static final int MAX_POR_EMAIL = 20;

    private final Map<String, Deque<Instant>> falhas = new ConcurrentHashMap<>();

    /** Minutos que ainda faltam de bloqueio, ou 0 se pode tentar. */
    public long minutosBloqueado(String email, String ip) {
        long a = restante("e+ip:" + email + "|" + ip, MAX_POR_EMAIL_E_IP);
        long b = restante("e:" + email, MAX_POR_EMAIL);
        return Math.max(a, b);
    }

    public void registrarFalha(String email, String ip) {
        Instant agora = Instant.now();
        for (String chave : new String[]{"e+ip:" + email + "|" + ip, "e:" + email}) {
            Deque<Instant> fila = falhas.computeIfAbsent(chave, k -> new ArrayDeque<>());
            synchronized (fila) {
                limpar(fila, agora);
                fila.addLast(agora);
            }
        }
    }

    public void registrarSucesso(String email, String ip) {
        falhas.remove("e+ip:" + email + "|" + ip);
    }

    private long restante(String chave, int maximo) {
        Deque<Instant> fila = falhas.get(chave);
        if (fila == null) return 0;
        synchronized (fila) {
            Instant agora = Instant.now();
            limpar(fila, agora);
            if (fila.size() < maximo) return 0;
            Duration falta = Duration.between(agora, fila.peekFirst().plus(JANELA));
            return Math.max(1, (falta.getSeconds() + 59) / 60);
        }
    }

    private static void limpar(Deque<Instant> fila, Instant agora) {
        while (!fila.isEmpty() && fila.peekFirst().isBefore(agora.minus(JANELA))) fila.pollFirst();
    }
}

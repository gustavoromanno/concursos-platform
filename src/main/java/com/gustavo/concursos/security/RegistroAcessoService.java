package com.gustavo.concursos.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Registros de acesso exigidos pelo Marco Civil da Internet (art. 15): data, hora e IP
 * de cada login, mantidos em sigilo por 6 meses. Uma limpeza diaria apaga o que passou
 * do prazo, para nao guardar mais do que a lei pede (LGPD, minimizacao).
 *
 * Falha ao gravar nunca impede o login: o registro e best-effort e vai para o log.
 */
@Service
public class RegistroAcessoService {

    private static final Logger log = LoggerFactory.getLogger(RegistroAcessoService.class);
    static final int MESES_GUARDA = 6;

    private final JdbcTemplate jdbc;

    public RegistroAcessoService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void registrar(Long usuarioId, String ip) {
        try {
            String valor = ip == null || ip.isBlank() ? "desconhecido" : ip.strip();
            if (valor.length() > 255) valor = valor.substring(0, 255);
            jdbc.update("INSERT INTO registro_acesso (usuario_id, ip, criado_em) VALUES (?, ?, ?)",
                    usuarioId, valor, LocalDateTime.now());
        } catch (Exception e) {
            log.error("Nao foi possivel gravar o registro de acesso do usuario {}: {}", usuarioId, e.getMessage());
        }
    }

    // Todo dia as 3h: apaga registros com mais de 6 meses.
    @Scheduled(cron = "0 0 3 * * *", zone = "America/Sao_Paulo")
    public int apagarVencidos() {
        int apagados = jdbc.update("DELETE FROM registro_acesso WHERE criado_em < ?",
                LocalDateTime.now().minusMonths(MESES_GUARDA));
        if (apagados > 0) log.info("Registros de acesso vencidos apagados: {}", apagados);
        return apagados;
    }
}

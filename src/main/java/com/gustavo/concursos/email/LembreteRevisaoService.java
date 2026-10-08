package com.gustavo.concursos.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Todo dia as 8h (horario de Brasilia): e-mail para quem ligou o lembrete,
 * e Pro, e tem revisoes vencendo hoje. Quem nao tem nada para revisar nao
 * recebe nada — o lembrete so chega quando e util.
 */
@Service
public class LembreteRevisaoService {

    private static final Logger log = LoggerFactory.getLogger(LembreteRevisaoService.class);

    private final JdbcTemplate jdbc;
    private final EmailCliente email;
    private final String urlBase;
    private final String nomeMarca;

    public LembreteRevisaoService(JdbcTemplate jdbc, EmailCliente email,
                                  @Value("${app.url-base:http://localhost:8080}") String urlBase,
                                  @Value("${app.nome:Romano Concursos}") String nomeMarca) {
        this.jdbc = jdbc;
        this.email = email;
        this.urlBase = urlBase.replaceAll("/+$", "");
        this.nomeMarca = nomeMarca;
    }

    @Scheduled(cron = "${lembrete.cron:0 0 8 * * *}", zone = "America/Sao_Paulo")
    public void enviarDoDia() {
        int enviados = enviar(LocalDate.now(java.time.ZoneId.of("America/Sao_Paulo")));
        if (enviados > 0) log.info("Lembretes de revisao enviados: {}", enviados);
    }

    /** Envia os lembretes de uma data e devolve quantos foram. */
    public int enviar(LocalDate hoje) {
        List<Map<String, Object>> pessoas = jdbc.queryForList("""
                SELECT u.email, u.nome, COUNT(r.id) AS pendentes
                FROM usuario u
                JOIN revisao r ON r.usuario_id = u.id AND r.proxima_revisao <= ?
                WHERE u.lembrete_revisao = TRUE
                  AND (u.papel = 'ADMIN' OR u.pro_ate > ?)
                GROUP BY u.id, u.email, u.nome""", Date.valueOf(hoje), Timestamp.valueOf(LocalDateTime.now()));
        for (Map<String, Object> p : pessoas) {
            long n = ((Number) p.get("pendentes")).longValue();
            String nome = String.valueOf(p.get("nome")).trim().split("\\s+")[0];
            email.enviar(String.valueOf(p.get("email")),
                    n + (n == 1 ? " questão para revisar hoje" : " questões para revisar hoje"),
                    """
                    <p>Olá, %s!</p>
                    <p>Você tem <strong>%d %s</strong> hoje. Revisar agora, antes de esquecer, é o que fixa o conteúdo.</p>
                    <p><a href="%s">Fazer as revisões</a></p>
                    <hr><p style="font-size:12px;color:#6B8880">Você recebe este lembrete porque o ativou na %s.
                    Para desligar, vá em "Minha conta".</p>
                    """.formatted(escapar(nome), n, n == 1 ? "questão para revisar" : "questões para revisar",
                            urlBase, escapar(nomeMarca)));
        }
        return pessoas.size();
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

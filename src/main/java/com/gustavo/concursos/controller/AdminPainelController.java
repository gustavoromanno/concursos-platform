package com.gustavo.concursos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Painel do administrador (so ADMIN, via /admin/**): numeros do negocio,
 * busca de usuarios e ajuste manual do Pro (cortesia, suporte, reembolso).
 */
@RestController
@RequestMapping("/admin")
public class AdminPainelController {

    private final JdbcTemplate jdbc;

    public AdminPainelController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record AjustePro(int dias) {
    }

    @Transactional(readOnly = true)
    @GetMapping("/painel")
    public Map<String, Object> painel() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("usuarios", n("SELECT COUNT(*) FROM usuario"));
        r.put("novos7dias", n("SELECT COUNT(*) FROM usuario WHERE criado_em >= ?", agora.minusDays(7)));
        r.put("novos30dias", n("SELECT COUNT(*) FROM usuario WHERE criado_em >= ?", agora.minusDays(30)));
        r.put("proAtivos", n("SELECT COUNT(*) FROM usuario WHERE pro_ate > ?", agora));
        r.put("aceitamMarketing", n("SELECT COUNT(*) FROM usuario WHERE aceita_marketing = TRUE"));
        r.put("ativos7dias", n("SELECT COUNT(DISTINCT usuario_id) FROM resposta WHERE respondida_em >= ?", agora.minusDays(7)));
        r.put("respostas30dias", n("SELECT COUNT(*) FROM resposta WHERE respondida_em >= ?", agora.minusDays(30)));
        r.put("receitaMesCentavos", n("SELECT COALESCE(SUM(valor_centavos), 0) FROM pagamento WHERE status = 'PAGO' AND pago_em >= ?", inicioMes));
        r.put("vendasMes", n("SELECT COUNT(*) FROM pagamento WHERE status = 'PAGO' AND pago_em >= ?", inicioMes));
        r.put("reembolsosMes", n("SELECT COUNT(*) FROM pagamento WHERE status = 'REEMBOLSADO' AND reembolsado_em >= ?", inicioMes));
        r.put("questoes", n("SELECT COUNT(*) FROM questao"));
        r.put("ineditas", n("SELECT COUNT(*) FROM questao WHERE origem = 'IA'"));
        r.put("rascunhosPendentes", n("SELECT COUNT(*) FROM questao_rascunho WHERE status = 'PENDENTE'"));
        r.put("pedidosNovos", n("SELECT COUNT(*) FROM solicitacao_conteudo WHERE status = 'NOVA'"));
        r.put("ultimosPagamentos", jdbc.queryForList("""
                SELECT email, plano, valor_centavos AS "valorCentavos", status, criado_em AS "criadoEm"
                FROM pagamento ORDER BY criado_em DESC LIMIT 10"""));
        return r;
    }

    @Transactional(readOnly = true)
    @GetMapping("/usuarios")
    public List<Map<String, Object>> usuarios(@RequestParam(defaultValue = "") String busca) {
        String termo = "%" + busca.trim().toLowerCase() + "%";
        return jdbc.queryForList("""
                SELECT u.id, u.nome, u.email, u.papel, u.pro_ate AS "proAte", u.criado_em AS "criadoEm",
                       u.aceita_marketing AS "aceitaMarketing",
                       (SELECT COUNT(*) FROM resposta r WHERE r.usuario_id = u.id) AS respondidas
                FROM usuario u
                WHERE LOWER(u.nome) LIKE ? OR LOWER(u.email) LIKE ?
                ORDER BY u.criado_em DESC
                LIMIT 50""", termo, termo);
    }

    // Dias positivos somam ao Pro (a partir do fim atual ou de agora); zero remove o Pro.
    @Transactional
    @PostMapping("/usuarios/{id}/pro")
    public Map<String, Object> ajustarPro(@PathVariable Long id, @RequestBody AjustePro ajuste) {
        if (ajuste.dias() < 0 || ajuste.dias() > 730) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe de 0 a 730 dias");
        }
        List<Map<String, Object>> achado = jdbc.queryForList("SELECT pro_ate FROM usuario WHERE id = ?", id);
        if (achado.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime novo = null;
        if (ajuste.dias() > 0) {
            Object atual = achado.get(0).get("pro_ate");
            // O driver pode devolver Timestamp (PostgreSQL) ou LocalDateTime (H2).
            LocalDateTime fim = atual instanceof java.sql.Timestamp ts ? ts.toLocalDateTime()
                    : atual instanceof LocalDateTime ldt ? ldt : null;
            LocalDateTime base = fim != null && fim.isAfter(agora) ? fim : agora;
            novo = base.plusDays(ajuste.dias());
        }
        jdbc.update("UPDATE usuario SET pro_ate = ? WHERE id = ?", novo, id);
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("id", id);
        r.put("proAte", novo);
        return r;
    }

    private long n(String sql, Object... args) {
        Long v = jdbc.queryForObject(sql, Long.class, args);
        return v == null ? 0 : v;
    }
}

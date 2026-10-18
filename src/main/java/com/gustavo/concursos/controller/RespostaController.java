package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.ResponderQuestaoRequestDTO;
import com.gustavo.concursos.dto.RespostaResultDTO;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.pro.AcessoPro;
import com.gustavo.concursos.service.RevisaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Resposta de uma questao, otimizada para latencia: o servidor (Render) e o banco
 * (Neon) ficam longe um do outro, e cada ida ao banco custa ~170 ms. Por isso a
 * resposta faz so DUAS idas, em vez de ~8 com JPA:
 *
 * 1. uma leitura com tudo: alternativas, explicacao, plano da pessoa e a agenda de revisao atual;
 * 2. uma escrita em lote (resposta + agenda de revisao), enviada de uma vez pelo driver.
 *
 * O calculo da repeticao espacada e o mesmo do RevisaoService (RevisaoService.calcular).
 */
@RestController
@RequestMapping("/questoes")
public class RespostaController {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final JdbcTemplate jdbc;

    public RespostaController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostMapping("/{id}/responder")
    public ResponseEntity<RespostaResultDTO> responder(
            @PathVariable Long id,
            @Valid @RequestBody ResponderQuestaoRequestDTO request,
            Authentication authentication
    ) {
        // 1) Uma ida ao banco: uma linha por alternativa, com os dados da questao,
        //    da pessoa e da revisao (se ja existir) repetidos em cada linha.
        List<Map<String, Object>> linhas = jdbc.queryForList("""
                SELECT a.id AS alt_id, a.correta AS alt_correta,
                       q.origem AS origem, q.explicacao AS explicacao,
                       u.id AS usuario_id, u.papel AS papel, u.pro_ate AS pro_ate,
                       r.id AS revisao_id, r.repeticoes AS repeticoes,
                       r.intervalo_dias AS intervalo_dias, r.facilidade AS facilidade
                FROM questao q
                JOIN alternativa a ON a.questao_id = q.id
                JOIN usuario u ON u.email = ?
                LEFT JOIN revisao r ON r.questao_id = q.id AND r.usuario_id = u.id
                WHERE q.id = ?""", authentication.getName(), id);
        if (linhas.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Questao nao encontrada");

        Map<String, Object> base = linhas.get(0);
        if (Questao.ORIGEM_IA.equals(base.get("origem")) && !ehPro(authentication, base)) {
            throw AcessoPro.bloqueio("As questões inéditas das provas");
        }

        Long escolhida = request.alternativaId();
        Map<String, Object> alternativa = linhas.stream()
                .filter(l -> escolhida != null && escolhida.equals(numero(l.get("alt_id"))))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Essa alternativa nao pertence a essa questao"));
        boolean correta = Boolean.TRUE.equals(alternativa.get("alt_correta"));
        Long alternativaCorretaId = linhas.stream()
                .filter(l -> Boolean.TRUE.equals(l.get("alt_correta")))
                .map(l -> numero(l.get("alt_id")))
                .findFirst().orElse(null);

        // 2) Repeticao espacada calculada aqui e gravada junto com a resposta.
        long usuarioId = numero(base.get("usuario_id"));
        boolean temRevisao = base.get("revisao_id") != null;
        RevisaoService.Agenda agenda = RevisaoService.calcular(
                temRevisao ? numero(base.get("repeticoes")).intValue() : null,
                temRevisao ? numero(base.get("intervalo_dias")).intValue() : null,
                temRevisao ? new BigDecimal(base.get("facilidade").toString()) : null,
                correta);

        // Valores so numericos, booleanos e datas geradas aqui: seguro montar o SQL
        // literal, o que permite mandar os dois comandos num unico lote (uma ida ao banco).
        String agora = "TIMESTAMP '" + LocalDateTime.now().format(TS) + "'";
        String proxima = "DATE '" + agenda.proximaRevisao() + "'";
        String facilidade = agenda.facilidade().toPlainString();
        String gravarResposta = "INSERT INTO resposta (usuario_id, questao_id, alternativa_escolhida_id, correta, respondida_em) "
                + "VALUES (" + usuarioId + ", " + id + ", " + escolhida + ", " + correta + ", " + agora + ")";
        String gravarRevisao = temRevisao
                ? "UPDATE revisao SET repeticoes = " + agenda.repeticoes() + ", intervalo_dias = " + agenda.intervaloDias()
                  + ", facilidade = " + facilidade + ", proxima_revisao = " + proxima + ", atualizado_em = " + agora
                  + " WHERE usuario_id = " + usuarioId + " AND questao_id = " + id
                : "INSERT INTO revisao (usuario_id, questao_id, proxima_revisao, intervalo_dias, facilidade, repeticoes, atualizado_em) "
                  + "VALUES (" + usuarioId + ", " + id + ", " + proxima + ", " + agenda.intervaloDias() + ", " + facilidade
                  + ", " + agenda.repeticoes() + ", " + agora + ")";
        jdbc.batchUpdate(gravarResposta, gravarRevisao);

        return ResponseEntity.ok(new RespostaResultDTO(correta, alternativaCorretaId, (String) base.get("explicacao")));
    }

    // Mesma regra do AcessoPro/Usuario.ehPro, com os dados ja lidos (sem outra ida ao banco).
    private static boolean ehPro(Authentication authentication, Map<String, Object> linha) {
        boolean admin = authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (admin || "ADMIN".equals(linha.get("papel"))) return true;
        Object proAte = linha.get("pro_ate");
        LocalDateTime ate = proAte instanceof Timestamp t ? t.toLocalDateTime()
                : proAte instanceof LocalDateTime l ? l : null;
        return ate != null && ate.isAfter(LocalDateTime.now());
    }

    private static Long numero(Object valor) {
        return valor == null ? null : ((Number) valor).longValue();
    }
}

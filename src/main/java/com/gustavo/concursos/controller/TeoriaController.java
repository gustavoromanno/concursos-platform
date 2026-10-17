package com.gustavo.concursos.controller;

import com.gustavo.concursos.pro.AcessoPro;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Teoria por assunto: resumos autorais ligados aos assuntos das questoes.
 *
 * GET /teoria lista o que existe (sem o texto) para montar o indice e os atalhos
 * nas questoes. GET /teoria/{assuntoId} entrega o resumo: os marcados como gratuitos
 * para todos; os demais so para o Pro (402 no plano gratuito).
 * Edicao em /admin/teoria/** (so ADMIN, pela configuracao de seguranca).
 */
@RestController
public class TeoriaController {

    private final JdbcTemplate jdbc;
    private final AcessoPro acessoPro;

    public TeoriaController(JdbcTemplate jdbc, AcessoPro acessoPro) {
        this.jdbc = jdbc;
        this.acessoPro = acessoPro;
    }

    public record ResumoDTO(
            @NotBlank(message = "Informe o titulo") @Size(max = 200, message = "Titulo muito longo") String titulo,
            @NotBlank(message = "Escreva o conteudo") @Size(max = 60000, message = "Conteudo muito longo") String conteudo,
            boolean gratuito
    ) {
    }

    @Transactional(readOnly = true)
    @GetMapping("/teoria")
    public List<Map<String, Object>> indice() {
        return jdbc.queryForList("""
                SELECT r.assunto_id AS "assuntoId", a.nome AS assunto, d.id AS "disciplinaId", d.nome AS disciplina,
                       r.titulo, r.gratuito,
                       (SELECT COUNT(*) FROM questao q WHERE q.assunto_id = r.assunto_id) AS "totalQuestoes"
                FROM resumo_assunto r
                JOIN assunto a ON a.id = r.assunto_id
                JOIN disciplina d ON d.id = a.disciplina_id
                ORDER BY d.nome, a.nome""");
    }

    @Transactional(readOnly = true)
    @GetMapping("/teoria/{assuntoId}")
    public Map<String, Object> resumo(@PathVariable Long assuntoId, Authentication authentication) {
        List<Map<String, Object>> achado = jdbc.queryForList("""
                SELECT r.assunto_id AS "assuntoId", a.nome AS assunto, d.id AS "disciplinaId", d.nome AS disciplina,
                       r.titulo, r.conteudo, r.gratuito, r.atualizado_em AS "atualizadoEm",
                       (SELECT COUNT(*) FROM questao q WHERE q.assunto_id = r.assunto_id) AS "totalQuestoes"
                FROM resumo_assunto r
                JOIN assunto a ON a.id = r.assunto_id
                JOIN disciplina d ON d.id = a.disciplina_id
                WHERE r.assunto_id = ?""", assuntoId);
        if (achado.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Este assunto ainda não tem teoria");
        Map<String, Object> r = achado.get(0);
        if (!Boolean.TRUE.equals(r.get("gratuito"))) acessoPro.exigir(authentication, "A teoria completa");
        return r;
    }

    // Cria ou substitui o resumo do assunto.
    @Transactional
    @PutMapping("/admin/teoria/{assuntoId}")
    public Map<String, Object> salvar(@PathVariable Long assuntoId, @Valid @RequestBody ResumoDTO dto) {
        Integer existe = jdbc.queryForObject("SELECT COUNT(*) FROM assunto WHERE id = ?", Integer.class, assuntoId);
        if (existe == null || existe == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assunto não encontrado");
        LocalDateTime agora = LocalDateTime.now();
        int alterados = jdbc.update("UPDATE resumo_assunto SET titulo = ?, conteudo = ?, gratuito = ?, atualizado_em = ? WHERE assunto_id = ?",
                dto.titulo().strip(), dto.conteudo().strip(), dto.gratuito(), agora, assuntoId);
        if (alterados == 0) {
            jdbc.update("INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito, atualizado_em) VALUES (?, ?, ?, ?, ?)",
                    assuntoId, dto.titulo().strip(), dto.conteudo().strip(), dto.gratuito(), agora);
        }
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("assuntoId", assuntoId);
        r.put("salvo", true);
        return r;
    }

    @Transactional
    @DeleteMapping("/admin/teoria/{assuntoId}")
    public ResponseEntity<Void> excluir(@PathVariable Long assuntoId) {
        jdbc.update("DELETE FROM resumo_assunto WHERE assunto_id = ?", assuntoId);
        return ResponseEntity.noContent().build();
    }
}

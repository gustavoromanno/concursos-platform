package com.gustavo.concursos.youtube;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ferramenta do admin para preencher a biblioteca de videoaulas a partir do YouTube
 * (so ADMIN, pela regra /admin/** do SecurityConfig).
 *
 * GET /admin/videoaulas/cobertura: assuntos com questoes e quantos videos cada um tem.
 * GET /admin/videoaulas/buscar?assuntoId=..&termo=..: videos do YouTube para o assunto,
 *     marcando os que ja estao cadastrados.
 * O cadastro em si usa o POST /videoaulas que ja existe.
 */
@RestController
@RequestMapping("/admin/videoaulas")
public class AdminVideoaulaController {

    public record ResultadoBusca(String termo, List<Map<String, Object>> videos) {
    }

    private final JdbcTemplate jdbc;
    private final YoutubeBusca youtube;

    public AdminVideoaulaController(JdbcTemplate jdbc, YoutubeBusca youtube) {
        this.jdbc = jdbc;
        this.youtube = youtube;
    }

    @GetMapping("/cobertura")
    public List<Map<String, Object>> cobertura() {
        return jdbc.queryForList("""
                SELECT a.id AS "assuntoId", a.nome AS assunto, d.id AS "disciplinaId", d.nome AS disciplina,
                       (SELECT COUNT(*) FROM questao q WHERE q.assunto_id = a.id) AS questoes,
                       (SELECT COUNT(*) FROM videoaula v WHERE v.assunto_id = a.id) AS videos
                FROM assunto a JOIN disciplina d ON d.id = a.disciplina_id
                WHERE EXISTS (SELECT 1 FROM questao q WHERE q.assunto_id = a.id)
                ORDER BY d.nome, a.nome""");
    }

    @GetMapping("/buscar")
    public ResultadoBusca buscar(@RequestParam Long assuntoId, @RequestParam(required = false) String termo) {
        List<Map<String, Object>> achado = jdbc.queryForList("""
                SELECT a.nome AS assunto, d.nome AS disciplina FROM assunto a
                JOIN disciplina d ON d.id = a.disciplina_id WHERE a.id = ?""", assuntoId);
        if (achado.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assunto não encontrado");
        String busca = termo != null && !termo.isBlank() ? termo.strip()
                : achado.get(0).get("assunto") + " " + achado.get(0).get("disciplina") + " aula concurso";

        Set<String> cadastrados = new HashSet<>(jdbc.queryForList("SELECT youtube_id FROM videoaula", String.class));
        List<Map<String, Object>> videos = youtube.buscar(busca, 8).stream()
                .map(v -> {
                    Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("youtubeId", v.youtubeId());
                    m.put("titulo", v.titulo());
                    m.put("canal", v.canal());
                    m.put("miniatura", v.miniatura());
                    m.put("duracaoMinutos", v.duracaoMinutos());
                    m.put("jaCadastrado", cadastrados.contains(v.youtubeId()));
                    return m;
                })
                .toList();
        return new ResultadoBusca(busca, videos);
    }
}

package com.gustavo.concursos.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Numeros reais para a pagina inicial publica (sem login).
@RestController
public class PublicoController {

    private final JdbcTemplate jdbc;
    private final String nomeMarca;

    public PublicoController(JdbcTemplate jdbc,
                             @org.springframework.beans.factory.annotation.Value("${app.nome:Romano Concursos}") String nomeMarca) {
        this.jdbc = jdbc;
        this.nomeMarca = nomeMarca;
    }

    // Nome da marca num lugar so: trocar APP_NOME no Render muda o site inteiro.
    @GetMapping("/publico/config")
    public Map<String, String> config() {
        return Map.of("nome", nomeMarca);
    }

    @GetMapping("/publico/numeros")
    public Map<String, Long> numeros() {
        return Map.of(
                "questoes", n("SELECT COUNT(*) FROM questao"),
                "bancas", n("SELECT COUNT(DISTINCT banca_id) FROM questao"),
                "disciplinas", n("SELECT COUNT(DISTINCT disciplina_id) FROM questao"),
                "concursos", n("SELECT COUNT(*) FROM concurso"));
    }

    private long n(String sql) {
        Long v = jdbc.queryForObject(sql, Long.class);
        return v == null ? 0 : v;
    }
}

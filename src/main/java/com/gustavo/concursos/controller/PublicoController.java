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

    // Os numeros mudam pouco (so quando entra conteudo novo): ficam 10 min em memoria.
    // Assim a pagina inicial nao consulta o banco a cada visita, nem acorda o Neon
    // suspenso so para mostrar contadores. Uma consulta so, em vez de quatro.
    private static final long VALIDADE_MS = 10 * 60 * 1000L;
    private volatile Map<String, Long> numerosEmCache;
    private volatile long numerosCalculadosEm;

    @GetMapping("/publico/numeros")
    public Map<String, Long> numeros() {
        Map<String, Long> atual = numerosEmCache;
        if (atual != null && System.currentTimeMillis() - numerosCalculadosEm < VALIDADE_MS) return atual;
        Map<String, Object> linha = jdbc.queryForMap("""
                SELECT (SELECT COUNT(*) FROM questao)                      AS questoes,
                       (SELECT COUNT(DISTINCT banca_id) FROM questao)      AS bancas,
                       (SELECT COUNT(DISTINCT disciplina_id) FROM questao) AS disciplinas,
                       (SELECT COUNT(*) FROM concurso)                     AS concursos""");
        Map<String, Long> novo = Map.of(
                "questoes", n(linha.get("questoes")),
                "bancas", n(linha.get("bancas")),
                "disciplinas", n(linha.get("disciplinas")),
                "concursos", n(linha.get("concursos")));
        numerosEmCache = novo;
        numerosCalculadosEm = System.currentTimeMillis();
        return novo;
    }

    private static long n(Object valor) {
        return valor instanceof Number num ? num.longValue() : 0;
    }
}

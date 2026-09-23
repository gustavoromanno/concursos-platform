package com.gustavo.concursos.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// A hospedagem chama este endereço para saber se a aplicação está viva.
// Checa também o banco: de nada adianta o processo responder se o
// PostgreSQL estiver fora.
@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        String banco;
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            banco = "ok";
        } catch (Exception e) {
            banco = "indisponivel";
        }
        return Map.of("status", "ok", "banco", banco);
    }
}

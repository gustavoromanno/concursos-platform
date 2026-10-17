package com.gustavo.concursos.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Exclusao definitiva de uma conta (direito do titular na LGPD), usada pela propria
 * pessoa em "Meu perfil" e pelo administrador no painel.
 *
 * Apaga perfil e dados de estudo. Mantem, sem vinculo com a conta: pagamentos
 * (obrigacao fiscal), pedidos de conteudo e reportes de erro. Os registros de acesso
 * (Marco Civil) nao tem vinculo de chave e seguem ate o fim do prazo legal.
 */
@Service
public class ExclusaoContaService {

    // Cada categoria de dados de estudo e os comandos que a apagam (na ordem das chaves estrangeiras).
    public static final Map<String, List<String>> APAGAR = new LinkedHashMap<>();

    static {
        APAGAR.put("ESTATISTICAS", List.of("DELETE FROM resposta WHERE usuario_id = ?"));
        APAGAR.put("CADERNOS", List.of(
                "DELETE FROM caderno_questao WHERE caderno_id IN (SELECT id FROM caderno WHERE usuario_id = ?)",
                "DELETE FROM caderno WHERE usuario_id = ?",
                "DELETE FROM anotacao WHERE usuario_id = ?"));
        APAGAR.put("SIMULADOS", List.of(
                "DELETE FROM simulado_questao WHERE simulado_id IN (SELECT id FROM simulado WHERE usuario_id = ?)",
                "DELETE FROM simulado WHERE usuario_id = ?"));
        APAGAR.put("MARCADORES", List.of("DELETE FROM marcador WHERE usuario_id = ?"));
        APAGAR.put("PROGRESSO", List.of(
                "DELETE FROM revisao WHERE usuario_id = ?",
                "DELETE FROM objetivo_usuario WHERE usuario_id = ?",
                "DELETE FROM meta_estudo WHERE usuario_id = ?"));
    }

    private final JdbcTemplate jdbc;

    public ExclusaoContaService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void apagarCategoria(String categoria, Long id) {
        for (String sql : APAGAR.get(categoria)) jdbc.update(sql, id);
    }

    @Transactional
    public void excluir(Long id) {
        APAGAR.keySet().forEach(c -> apagarCategoria(c, id));
        jdbc.update("DELETE FROM comentario WHERE usuario_id = ?", id);
        jdbc.update("DELETE FROM filtro_salvo WHERE usuario_id = ?", id);
        jdbc.update("UPDATE pagamento SET usuario_id = NULL WHERE usuario_id = ?", id);
        jdbc.update("UPDATE solicitacao_conteudo SET usuario_id = NULL WHERE usuario_id = ?", id);
        jdbc.update("UPDATE reporte_questao SET usuario_id = NULL WHERE usuario_id = ?", id);
        jdbc.update("DELETE FROM usuario WHERE id = ?", id);
    }
}

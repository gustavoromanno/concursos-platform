package com.gustavo.concursos.controller;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

// Filtros de busca salvos pelo aluno. Sempre do usuario logado.
@RestController
@RequestMapping("/filtros")
public class FiltroSalvoController {

    static final int LIMITE = 20;

    private final JdbcTemplate jdbc;
    private final UsuarioRepository usuarioRepository;

    public FiltroSalvoController(JdbcTemplate jdbc, UsuarioRepository usuarioRepository) {
        this.jdbc = jdbc;
        this.usuarioRepository = usuarioRepository;
    }

    public record NovoFiltroDTO(
            @NotBlank(message = "Dê um nome ao filtro") @Size(max = 60, message = "Nome com até 60 caracteres") String nome,
            // So pares chave=valor simples de query string: nada de HTML ou script.
            @NotBlank(message = "Escolha ao menos um filtro antes de salvar")
            @Size(max = 2000)
            @Pattern(regexp = "^[A-Za-z0-9_=&%.+\\-]*$", message = "Filtro inválido") String parametros
    ) {
    }

    @Transactional(readOnly = true)
    @GetMapping
    public List<Map<String, Object>> listar(Authentication authentication) {
        return jdbc.queryForList(
                "SELECT id, nome, parametros FROM filtro_salvo WHERE usuario_id = ? ORDER BY criado_em",
                usuarioId(authentication));
    }

    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> salvar(@Valid @RequestBody NovoFiltroDTO dto, Authentication authentication) {
        Long uid = usuarioId(authentication);
        Long total = jdbc.queryForObject("SELECT COUNT(*) FROM filtro_salvo WHERE usuario_id = ?", Long.class, uid);
        if (total != null && total >= LIMITE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já tem " + LIMITE + " filtros salvos. Apague algum para salvar outro.");
        }
        jdbc.update("INSERT INTO filtro_salvo (usuario_id, nome, parametros, criado_em) VALUES (?, ?, ?, CURRENT_TIMESTAMP)",
                uid, dto.nome().strip(), dto.parametros());
        return Map.of("nome", dto.nome().strip(), "parametros", dto.parametros());
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id, Authentication authentication) {
        int apagados = jdbc.update("DELETE FROM filtro_salvo WHERE id = ? AND usuario_id = ?", id, usuarioId(authentication));
        if (apagados == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Filtro não encontrado");
        return ResponseEntity.noContent().build();
    }

    private Long usuarioId(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName()).map(Usuario::getId)
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

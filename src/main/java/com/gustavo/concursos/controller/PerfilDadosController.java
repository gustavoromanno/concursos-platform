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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Perfil completo: dados pessoais, foto, resumo de uso e a "zona de perigo"
 * (zerar dados por categoria e excluir a conta — direito do titular na LGPD).
 * Tudo sempre do usuario logado.
 */
@RestController
@RequestMapping("/perfil")
public class PerfilDadosController {

    private static final Set<String> UFS = Set.of("AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS",
            "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO");
    private static final int FOTO_MAX_CARACTERES = 400_000;   // ~300 KB de imagem em base64

    // Cada categoria da zona de perigo e os comandos que a apagam (na ordem das chaves estrangeiras).
    private static final Map<String, List<String>> APAGAR = new LinkedHashMap<>();
    private static final Map<String, String> CONTAR = new LinkedHashMap<>();

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

        CONTAR.put("ESTATISTICAS", "SELECT COUNT(*) FROM resposta WHERE usuario_id = ?");
        CONTAR.put("CADERNOS", "SELECT (SELECT COUNT(*) FROM caderno WHERE usuario_id = ?) + (SELECT COUNT(*) FROM anotacao WHERE usuario_id = ?)");
        CONTAR.put("SIMULADOS", "SELECT COUNT(*) FROM simulado WHERE usuario_id = ?");
        CONTAR.put("MARCADORES", "SELECT COUNT(*) FROM marcador WHERE usuario_id = ?");
        CONTAR.put("PROGRESSO", "SELECT (SELECT COUNT(*) FROM revisao WHERE usuario_id = ?) + (SELECT COUNT(*) FROM objetivo_usuario WHERE usuario_id = ?)");
    }

    private final UsuarioRepository usuarioRepository;
    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;

    public PerfilDadosController(UsuarioRepository usuarioRepository, JdbcTemplate jdbc, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    public record DadosPessoaisDTO(
            @NotBlank(message = "Informe seu nome") @Size(max = 150, message = "Nome muito longo") String nome,
            String uf,
            @Size(max = 60, message = "Carreira muito longa") String carreiraAlvo,
            @Size(max = 500, message = "O texto de objetivos pode ter até 500 caracteres") String bio
    ) {
    }

    public record FotoDTO(
            @NotBlank @Pattern(regexp = "^data:image/(png|jpeg|webp);base64,[A-Za-z0-9+/=]+$",
                    message = "Envie uma imagem PNG, JPG ou WEBP") String foto
    ) {
    }

    public record ExcluirContaDTO(@NotBlank(message = "Confirme com sua senha") String senha) {
    }

    // Dados do cabecalho do perfil: foto, extras, forca do perfil, numeros e plano.
    @Transactional(readOnly = true)
    @GetMapping("/completo")
    public Map<String, Object> completo(Authentication authentication) {
        Usuario u = usuarioLogado(authentication);
        Long id = u.getId();

        int preenchidos = 1;   // nome e sempre obrigatorio
        if (u.getFoto() != null) preenchidos++;
        if (u.getUf() != null) preenchidos++;
        if (u.getCarreiraAlvo() != null) preenchidos++;
        if (u.getBio() != null) preenchidos++;

        long respondidas = contar("SELECT COUNT(*) FROM resposta WHERE usuario_id = ?", id);
        long acertos = contar("SELECT COUNT(*) FROM resposta WHERE usuario_id = ? AND correta = TRUE", id);

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("uf", u.getUf());
        r.put("carreiraAlvo", u.getCarreiraAlvo());
        r.put("bio", u.getBio());
        r.put("foto", u.getFoto());
        r.put("forcaPerfil", preenchidos * 20);
        r.put("pro", u.ehPro());
        r.put("proAte", "ADMIN".equals(u.getPapel()) ? null : u.getProAte());
        r.put("respondidas", respondidas);
        r.put("taxaAcerto", respondidas == 0 ? 0 : Math.round(acertos * 100.0 / respondidas));
        r.put("simulados", contar("SELECT COUNT(*) FROM simulado WHERE usuario_id = ?", id));
        r.put("cadernos", contar("SELECT COUNT(*) FROM caderno WHERE usuario_id = ?", id));
        return r;
    }

    @Transactional
    @PutMapping("/dados")
    public Map<String, Object> salvarDados(@Valid @RequestBody DadosPessoaisDTO dto, Authentication authentication) {
        Usuario u = usuarioLogado(authentication);
        String uf = vazioParaNulo(dto.uf());
        if (uf != null && !UFS.contains(uf.toUpperCase())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UF inválida");
        u.setNome(dto.nome().strip());
        u.setUf(uf == null ? null : uf.toUpperCase());
        u.setCarreiraAlvo(vazioParaNulo(dto.carreiraAlvo()));
        u.setBio(vazioParaNulo(dto.bio()));
        usuarioRepository.save(u);
        return completo(authentication);
    }

    @Transactional
    @PutMapping("/foto")
    public Map<String, Object> salvarFoto(@Valid @RequestBody FotoDTO dto, Authentication authentication) {
        if (dto.foto().length() > FOTO_MAX_CARACTERES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A foto precisa ter no máximo 300 KB");
        }
        Usuario u = usuarioLogado(authentication);
        u.setFoto(dto.foto());
        usuarioRepository.save(u);
        return completo(authentication);
    }

    @Transactional
    @DeleteMapping("/foto")
    public Map<String, Object> removerFoto(Authentication authentication) {
        Usuario u = usuarioLogado(authentication);
        u.setFoto(null);
        usuarioRepository.save(u);
        return completo(authentication);
    }

    // Quantos registros cada categoria da zona de perigo apagaria.
    @Transactional(readOnly = true)
    @GetMapping("/dados-estudo")
    public Map<String, Long> contagens(Authentication authentication) {
        Long id = usuarioLogado(authentication).getId();
        Map<String, Long> r = new LinkedHashMap<>();
        CONTAR.forEach((categoria, sql) -> r.put(categoria, contar(sql, id)));
        return r;
    }

    // DELETE /perfil/dados-estudo/{categoria} — categoria ou TUDO. A conta continua ativa.
    @Transactional
    @DeleteMapping("/dados-estudo/{categoria}")
    public Map<String, Long> zerar(@PathVariable String categoria, Authentication authentication) {
        Long id = usuarioLogado(authentication).getId();
        String alvo = categoria.toUpperCase();
        if (alvo.equals("TUDO")) {
            APAGAR.values().forEach(cmds -> executar(cmds, id));
        } else if (APAGAR.containsKey(alvo)) {
            executar(APAGAR.get(alvo), id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria inválida");
        }
        return contagens(authentication);
    }

    // Exclui a conta e todos os dados de estudo. Exige a senha.
    // Pagamentos ficam (sem vinculo com a conta) por obrigacao fiscal.
    @Transactional
    @PostMapping("/excluir-conta")
    public ResponseEntity<Void> excluirConta(@Valid @RequestBody ExcluirContaDTO dto, Authentication authentication) {
        Usuario u = usuarioLogado(authentication);
        if (!passwordEncoder.matches(dto.senha(), u.getSenhaHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha incorreta");
        }
        if ("ADMIN".equals(u.getPapel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A conta de administrador não pode ser excluída por aqui");
        }
        Long id = u.getId();
        APAGAR.values().forEach(cmds -> executar(cmds, id));
        jdbc.update("DELETE FROM comentario WHERE usuario_id = ?", id);
        jdbc.update("UPDATE pagamento SET usuario_id = NULL WHERE usuario_id = ?", id);
        jdbc.update("UPDATE solicitacao_conteudo SET usuario_id = NULL WHERE usuario_id = ?", id);
        jdbc.update("DELETE FROM usuario WHERE id = ?", id);
        return ResponseEntity.noContent().build();
    }

    private void executar(List<String> comandos, Long id) {
        for (String sql : comandos) jdbc.update(sql, id);
    }

    private long contar(String sql, Long id) {
        int parametros = (int) sql.chars().filter(c -> c == '?').count();
        Object[] args = new Object[parametros];
        java.util.Arrays.fill(args, id);
        Long n = jdbc.queryForObject(sql, Long.class, args);
        return n == null ? 0 : n;
    }

    private static String vazioParaNulo(String s) {
        return s == null || s.isBlank() ? null : s.strip();
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

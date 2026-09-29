package com.gustavo.concursos.email;

import com.gustavo.concursos.dto.RegrasSenha;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;

/**
 * "Esqueci minha senha" (rotas publicas em /auth/**).
 *
 * - A resposta e sempre a mesma, exista ou nao a conta: nao revela quais
 *   e-mails estao cadastrados.
 * - Link de uso unico, valido por 30 minutos; no banco fica so o hash SHA-256.
 * - No maximo 3 pedidos por hora por conta.
 */
@RestController
@RequestMapping("/auth")
public class RedefinicaoSenhaController {

    static final int VALIDADE_MINUTOS = 30;
    static final int LIMITE_POR_HORA = 3;
    private static final String RESPOSTA_PADRAO =
            "Se existir uma conta com este e-mail, enviamos um link para criar uma nova senha. Confira também o spam.";

    private final UsuarioRepository usuarioRepository;
    private final TokenRedefinicaoRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailCliente email;
    private final String urlBase;
    private final SecureRandom aleatorio = new SecureRandom();
    private final String nomeMarca;

    public RedefinicaoSenhaController(
            UsuarioRepository usuarioRepository,
            TokenRedefinicaoRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            EmailCliente email,
            @Value("${app.url-base:http://localhost:8080}") String urlBase,
            @Value("${app.nome:Concursos Platform}") String nomeMarca
    ) {
        this.nomeMarca = nomeMarca;
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.urlBase = urlBase.replaceAll("/+$", "");
    }

    public record EsqueciDTO(@NotBlank(message = "Informe seu e-mail") String email) {
    }

    public record RedefinirDTO(
            @NotBlank(message = "Link inválido") String token,
            @NotBlank(message = "Informe a nova senha")
            @Pattern(regexp = RegrasSenha.REGEX, message = RegrasSenha.MENSAGEM) String novaSenha
    ) {
    }

    @Transactional
    @PostMapping("/esqueci-senha")
    public Map<String, String> esqueci(@Valid @RequestBody EsqueciDTO dto) {
        usuarioRepository.findByEmail(Usuario.normalizarEmail(dto.email())).ifPresent(u -> {
            long recentes = tokenRepository.countByUsuarioIdAndCriadoEmGreaterThanEqual(u.getId(), LocalDateTime.now().minusHours(1));
            if (recentes >= LIMITE_POR_HORA) return;   // silencioso: mesma resposta

            byte[] bruto = new byte[32];
            aleatorio.nextBytes(bruto);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bruto);

            TokenRedefinicao t = new TokenRedefinicao();
            t.setUsuario(u);
            t.setTokenHash(hash(token));
            t.setExpiraEm(LocalDateTime.now().plusMinutes(VALIDADE_MINUTOS));
            tokenRepository.save(t);

            String link = urlBase + "/?redefinir=" + token;
            email.enviar(u.getEmail(), "Crie uma nova senha — " + nomeMarca, """
                    <p>Olá, %s!</p>
                    <p>Recebemos um pedido para criar uma nova senha na %s.</p>
                    <p><a href="%s">Clique aqui para criar sua nova senha</a>. O link vale por %d minutos e só pode ser usado uma vez.</p>
                    <p>Se não foi você, ignore este e-mail: sua senha continua a mesma.</p>
                    """.formatted(escapar(u.getNome()), escapar(nomeMarca), link, VALIDADE_MINUTOS));
        });
        return Map.of("message", RESPOSTA_PADRAO);
    }

    @Transactional
    @PostMapping("/redefinir-senha")
    public Map<String, String> redefinir(@Valid @RequestBody RedefinirDTO dto) {
        TokenRedefinicao t = tokenRepository.findByTokenHash(hash(dto.token()))
                .filter(x -> x.getUsadoEm() == null && x.getExpiraEm().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Este link expirou ou já foi usado. Peça um novo em \"Esqueci minha senha\"."));

        Usuario u = t.getUsuario();
        u.setSenhaHash(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(u);
        tokenRepository.invalidarPendentes(u.getId(), LocalDateTime.now());
        return Map.of("message", "Senha alterada. Entre com a nova senha.");
    }

    static String hash(String token) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String escapar(String texto) {
        return texto == null ? "" : texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

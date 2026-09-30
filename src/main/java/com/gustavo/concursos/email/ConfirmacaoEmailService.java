package com.gustavo.concursos.email;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Confirmacao de e-mail: link de uso unico, valido por 48 horas, com so o hash
 * guardado no banco (mesmo esquema do "esqueci minha senha").
 */
@Service
public class ConfirmacaoEmailService {

    static final int VALIDADE_HORAS = 48;
    static final int REENVIOS_POR_HORA = 3;

    private final TokenConfirmacaoRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailCliente email;
    private final String urlBase;
    private final String nomeMarca;
    private final boolean exigir;
    private final SecureRandom aleatorio = new SecureRandom();

    public ConfirmacaoEmailService(TokenConfirmacaoRepository tokenRepository, UsuarioRepository usuarioRepository,
                                   EmailCliente email,
                                   @Value("${app.url-base:http://localhost:8080}") String urlBase,
                                   @Value("${app.nome:Concursos Platform}") String nomeMarca,
                                   @Value("${app.exigir-confirmacao-email:true}") boolean exigir) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.email = email;
        this.urlBase = urlBase.replaceAll("/+$", "");
        this.nomeMarca = nomeMarca;
        this.exigir = exigir;
    }

    public boolean exigida() {
        return exigir;
    }

    // Envia (ou reenvia) o link. Respeita o limite por hora em silencio.
    @Transactional
    public void enviar(Usuario u) {
        if (u.isEmailConfirmado()) return;
        if (tokenRepository.countByUsuarioIdAndCriadoEmGreaterThanEqual(u.getId(), LocalDateTime.now().minusHours(1)) >= REENVIOS_POR_HORA) return;

        byte[] bruto = new byte[32];
        aleatorio.nextBytes(bruto);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bruto);

        TokenConfirmacao t = new TokenConfirmacao();
        t.setUsuario(u);
        t.setTokenHash(RedefinicaoSenhaController.hash(token));
        t.setExpiraEm(LocalDateTime.now().plusHours(VALIDADE_HORAS));
        tokenRepository.save(t);

        String nome = u.getNome() == null ? "" : u.getNome().trim().split("\\s+")[0];
        email.enviar(u.getEmail(), "Confirme seu e-mail — " + nomeMarca, """
                <p>Olá, %s!</p>
                <p>Falta só um passo para começar a estudar na %s.</p>
                <p><a href="%s/?confirmar=%s">Clique aqui para confirmar seu e-mail</a>. O link vale por %d horas.</p>
                <p>Se você não criou esta conta, ignore este e-mail.</p>
                """.formatted(escapar(nome), escapar(nomeMarca), urlBase, token, VALIDADE_HORAS));
    }

    @Transactional
    public void confirmar(String token) {
        TokenConfirmacao t = tokenRepository.findByTokenHash(RedefinicaoSenhaController.hash(token == null ? "" : token))
                .filter(x -> x.getUsadoEm() == null && x.getExpiraEm().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Este link expirou ou já foi usado. Entre com seu e-mail e senha para receber um novo."));
        t.setUsadoEm(LocalDateTime.now());
        Usuario u = t.getUsuario();
        u.setEmailConfirmado(true);
        usuarioRepository.save(u);
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

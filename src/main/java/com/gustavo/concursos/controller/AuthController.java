package com.gustavo.concursos.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

import com.gustavo.concursos.dto.LoginRequestDTO;
import com.gustavo.concursos.dto.LoginResponseDTO;
import com.gustavo.concursos.dto.RegistroRequestDTO;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import com.gustavo.concursos.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final com.gustavo.concursos.email.ConfirmacaoEmailService confirmacaoEmail;
    private final com.gustavo.concursos.security.LimiteTentativasLogin limiteLogin;

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            com.gustavo.concursos.email.ConfirmacaoEmailService confirmacaoEmail,
            com.gustavo.concursos.security.LimiteTentativasLogin limiteLogin
    ) {
        this.confirmacaoEmail = confirmacaoEmail;
        this.limiteLogin = limiteLogin;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<java.util.Map<String, Object>> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        String email = Usuario.normalizarEmail(request.email());
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe uma conta com este e-mail. Use \"Fazer login\".");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome().strip());
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        if (Boolean.TRUE.equals(request.aceitaMarketing())) {
            usuario.setAceitaMarketing(true);
            usuario.setMarketingAtualizadoEm(LocalDateTime.now());
        }

        // Sem exigencia de confirmacao (ex.: testes), a conta ja nasce confirmada.
        usuario.setEmailConfirmado(!confirmacaoEmail.exigida());
        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            // Dois envios quase simultaneos do mesmo cadastro (duplo clique, conexao
            // lenta): o segundo bate na restricao de e-mail unico do banco.
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe uma conta com este e-mail. Use \"Fazer login\".");
        }

        confirmacaoEmail.enviar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(java.util.Map.of("confirmacaoNecessaria", !usuario.isEmailConfirmado(), "email", email));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                  jakarta.servlet.http.HttpServletRequest http) {
        String email = Usuario.normalizarEmail(request.email());
        String ip = ipDe(http);

        // Freio contra forca bruta: muitas senhas erradas bloqueiam por um tempo.
        long bloqueio = limiteLogin.minutosBloqueado(email, ip);
        if (bloqueio > 0) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Muitas tentativas de login. Tente de novo em " + bloqueio + " minuto" + (bloqueio > 1 ? "s" : "") + ".");
        }
        try {
            // Lanca excecao (tratada pelo Spring como 401) se a senha estiver errada.
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.senha()));
        } catch (org.springframework.security.core.AuthenticationException e) {
            limiteLogin.registrarFalha(email, ip);
            throw e;
        }
        limiteLogin.registrarSucesso(email, ip);

        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        if (confirmacaoEmail.exigida() && !usuario.isEmailConfirmado()) {
            confirmacaoEmail.enviar(usuario);   // reenvia (respeitando o limite por hora)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Confirme seu e-mail para entrar. Enviamos um link para " + email + " (confira também o spam).");
        }

        String token = jwtService.gerarToken(email);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    public record TokenDTO(@jakarta.validation.constraints.NotBlank String token) {
    }

    // Link do e-mail de confirmacao (rota publica em /auth/**).
    @PostMapping("/confirmar-email")
    public java.util.Map<String, String> confirmarEmail(@Valid @RequestBody TokenDTO dto) {
        confirmacaoEmail.confirmar(dto.token());
        return java.util.Map.of("message", "E-mail confirmado! Agora é só entrar.");
    }

    // O IP real vem no X-Forwarded-For quando ha proxy na frente (Render).
    private static String ipDe(jakarta.servlet.http.HttpServletRequest http) {
        String encaminhado = http.getHeader("X-Forwarded-For");
        if (encaminhado != null && !encaminhado.isBlank()) return encaminhado.split(",")[0].trim();
        return http.getRemoteAddr();
    }
}

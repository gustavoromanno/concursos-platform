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

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@Valid @RequestBody RegistroRequestDTO request) {
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

        try {
            usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException e) {
            // Dois envios quase simultaneos do mesmo cadastro (duplo clique, conexao
            // lenta): o segundo bate na restricao de e-mail unico do banco.
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe uma conta com este e-mail. Use \"Fazer login\".");
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        // Lanca excecao automaticamente (tratada pelo Spring como 401) se a senha estiver errada.
        String email = Usuario.normalizarEmail(request.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.senha())
        );

        String token = jwtService.gerarToken(email);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}

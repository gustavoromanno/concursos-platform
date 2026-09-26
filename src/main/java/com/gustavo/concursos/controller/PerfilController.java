package com.gustavo.concursos.controller;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

// Dados do usuario logado. Fica fora de /auth/** de proposito:
// aquele prefixo e publico, e o perfil exige token.
@RestController
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public record PerfilDTO(Long id, String nome, String email, String papel, boolean aceitaMarketing,
                            boolean pro, java.time.LocalDateTime proAte) {}

    public record AlterarNomeDTO(@NotBlank @Size(max = 150) String nome) {}

    public record AlterarSenhaDTO(
            @NotBlank String senhaAtual,
            // Mesma regra do cadastro: a troca nao pode enfraquecer a senha.
            @NotBlank @jakarta.validation.constraints.Pattern(
                    regexp = com.gustavo.concursos.dto.RegrasSenha.REGEX,
                    message = com.gustavo.concursos.dto.RegrasSenha.MENSAGEM) String novaSenha
    ) {}

    @GetMapping("/perfil")
    public PerfilDTO perfil(Authentication authentication) {
        return paraDTO(usuarioLogado(authentication));
    }

    // PUT /perfil — altera o nome exibido. O e-mail e o login e nao muda por aqui.
    @Transactional
    @PutMapping("/perfil")
    public PerfilDTO alterarNome(@Valid @RequestBody AlterarNomeDTO request, Authentication authentication) {
        Usuario u = usuarioLogado(authentication);
        u.setNome(request.nome().strip());
        usuarioRepository.save(u);
        return paraDTO(u);
    }

    // PUT /perfil/senha — exige a senha atual. Senha atual errada devolve 400,
    // e nao 401: o 401 faria o frontend encerrar a sessao de quem so digitou errado.
    @Transactional
    @PutMapping("/perfil/senha")
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody AlterarSenhaDTO request, Authentication authentication) {
        Usuario u = usuarioLogado(authentication);

        if (!passwordEncoder.matches(request.senhaAtual(), u.getSenhaHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        }
        if (request.senhaAtual().equals(request.novaSenha())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha deve ser diferente da atual");
        }

        u.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(u);
        return ResponseEntity.noContent().build();
    }

    public record PreferenciaMarketingDTO(@jakarta.validation.constraints.NotNull Boolean aceita) {}

    // PUT /perfil/marketing — a pessoa aceita ou deixa de aceitar e-mails promocionais.
    @Transactional
    @PutMapping("/perfil/marketing")
    public PerfilDTO alterarMarketing(@Valid @RequestBody PreferenciaMarketingDTO request, Authentication authentication) {
        Usuario u = usuarioLogado(authentication);
        u.setAceitaMarketing(request.aceita());
        u.setMarketingAtualizadoEm(java.time.LocalDateTime.now());
        usuarioRepository.save(u);
        return paraDTO(u);
    }

    private PerfilDTO paraDTO(Usuario u) {
        return new PerfilDTO(u.getId(), u.getNome(), u.getEmail(), u.getPapel(), u.isAceitaMarketing(),
                u.ehPro(), "ADMIN".equals(u.getPapel()) ? null : u.getProAte());
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

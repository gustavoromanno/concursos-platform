package com.gustavo.concursos.controller;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

// Dados do usuario logado. Fica fora de /auth/** de proposito:
// aquele prefixo e publico, e o perfil exige token.
@RestController
public class PerfilController {

    private final UsuarioRepository usuarioRepository;

    public PerfilController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public record PerfilDTO(Long id, String nome, String email) {}

    @GetMapping("/perfil")
    public PerfilDTO perfil(Authentication authentication) {
        Usuario u = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
        return new PerfilDTO(u.getId(), u.getNome(), u.getEmail());
    }
}

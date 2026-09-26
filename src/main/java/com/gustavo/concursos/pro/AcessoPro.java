package com.gustavo.concursos.pro;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

// Consultas de acesso usadas pelos controllers que liberam parte do recurso.
@Component
public class AcessoPro {

    private final UsuarioRepository usuarioRepository;

    public AcessoPro(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean ehPro(Authentication authentication) {
        if (authentication == null) return false;
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (admin) return true;
        return usuarioRepository.findByEmail(authentication.getName()).map(Usuario::ehPro).orElse(false);
    }

    public void exigir(Authentication authentication, String recurso) {
        if (!ehPro(authentication)) throw bloqueio(recurso);
    }

    public static ResponseStatusException bloqueio(String recurso) {
        return new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED,
                recurso + " é um recurso do plano Pro.");
    }
}

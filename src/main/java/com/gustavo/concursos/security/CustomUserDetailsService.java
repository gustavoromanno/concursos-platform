package com.gustavo.concursos.security;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Tenta o e-mail padronizado (o normal) e, por compatibilidade, o exato:
        // contas antigas que colidiam na padronizacao ficaram como estavam.
        Usuario usuario = usuarioRepository.findByEmail(Usuario.normalizarEmail(email))
                .or(() -> usuarioRepository.findByEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

        // O Spring Security espera o prefixo "ROLE_" nas autoridades;
        // hasRole("ADMIN") procura por "ROLE_ADMIN".
        return new User(
                usuario.getEmail(),
                usuario.getSenhaHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPapel()))
        );
    }
}

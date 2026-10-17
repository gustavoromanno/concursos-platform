package com.gustavo.concursos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Roda uma vez por requisicao, antes do filtro padrao de autenticacao do Spring.
// Le o header "Authorization: Bearer <token>", valida e, se for valido,
// registra o usuario como autenticado para o resto da requisicao.
//
// IMPORTANTE: esta classe NAO leva @Component nem vira @Bean de propósito.
// Se virasse, o Spring Boot a registraria tambem como filtro de servlet, fora
// da cadeia do Spring Security. Nesse caso ela rodaria antes da cadeia, o
// SecurityContextHolderFilter apagaria a autenticacao logo em seguida, e a
// segunda execucao (dentro da cadeia) seria pulada pelo OncePerRequestFilter --
// resultando em 403 mesmo com um token valido. Por isso ela e instanciada
// manualmente no SecurityConfig.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // Cache curto do usuario por e-mail: cada requisicao autenticada deixava de ir
    // ao banco (~170 ms entre o Render e o Neon) so para reler o mesmo usuario.
    // 30 s e o maximo que uma troca de papel ou exclusao de conta leva para valer.
    // Fica aqui (e nao no CustomUserDetailsService) de proposito: o login apaga a
    // senha do objeto que recebe, e um cache compartilhado sairia sem senha.
    static final long VALIDADE_CACHE_MS = 30_000;
    private record UsuarioEmCache(UserDetails detalhes, long expiraEm) {
    }
    private final Map<String, UsuarioEmCache> cache = new ConcurrentHashMap<>();

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extrairEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = carregar(email);

                if (jwtService.tokenValido(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token invalido/expirado: segue sem autenticar, o endpoint protegido vai barrar depois.
        }

        filterChain.doFilter(request, response);
    }

    private UserDetails carregar(String email) {
        long agora = System.currentTimeMillis();
        UsuarioEmCache emCache = cache.get(email);
        if (emCache != null && emCache.expiraEm() > agora) return emCache.detalhes();
        UserDetails detalhes = userDetailsService.loadUserByUsername(email);
        if (cache.size() > 5_000) cache.clear();   // teto simples de memoria
        cache.put(email, new UsuarioEmCache(detalhes, agora + VALIDADE_CACHE_MS));
        return detalhes;
    }
}

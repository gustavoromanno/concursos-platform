package com.gustavo.concursos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Públicos
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/", "/index.html", "/app.js", "/style.css", "/favicon.ico").permitAll()
                        .requestMatchers("/health").permitAll()
                        // Quando um controller responde com erro (400, 404...), o Tomcat
                        // repassa a requisicao para /error. Nesse repasse o token ja nao
                        // e lido, e sem esta liberacao o usuario recebia 401 no lugar do
                        // erro real — e o frontend o deslogava.
                        .requestMatchers("/error").permitAll()

                        // Responder questão é de TODO usuário logado. Precisa vir
                        // antes da regra de admin, senão o padrão /questoes/**
                        // capturaria esta rota e barraria quem estuda.
                        .requestMatchers(HttpMethod.POST, "/questoes/*/responder").authenticated()
                        .requestMatchers(HttpMethod.POST, "/questoes/*/comentarios").authenticated()

                        // Cadastro e remoção de conteúdo: só ADMIN.
                        .requestMatchers(HttpMethod.POST, "/questoes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/questoes/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/concursos/**", "/videoaulas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/concursos/**", "/videoaulas/**", "/cargos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/concursos/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                // Sem token (ou token invalido) -> 401. Logado sem permissao -> 403 (padrao).
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}

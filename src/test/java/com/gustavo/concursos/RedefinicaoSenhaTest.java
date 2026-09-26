package com.gustavo.concursos;

import com.gustavo.concursos.email.EmailCliente;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(RedefinicaoSenhaTest.EmailFalso.class)
class RedefinicaoSenhaTest {

    record Enviado(String para, String assunto, String html) {
    }

    static final List<Enviado> ENVIADOS = new ArrayList<>();

    @TestConfiguration
    static class EmailFalso {
        @Bean
        @Primary
        EmailCliente emailFalso() {
            return (para, assunto, html) -> ENVIADOS.add(new Enviado(para, assunto, html));
        }
    }

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String email;

    @BeforeEach
    void preparar() {
        ENVIADOS.clear();
        email = "esqueceu" + System.nanoTime() + "@teste.com";
        Usuario u = new Usuario();
        u.setNome("Esquecido");
        u.setEmail(email);
        u.setSenhaHash(passwordEncoder.encode("Antiga123!"));
        usuarioRepository.save(u);
    }

    private void pedir(String paraEmail) throws Exception {
        mvc.perform(post("/auth/esqueci-senha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + paraEmail + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    private String tokenDoEmail() {
        Matcher m = java.util.regex.Pattern.compile("redefinir=([A-Za-z0-9_-]+)").matcher(ENVIADOS.get(ENVIADOS.size() - 1).html());
        assertThat(m.find()).isTrue();
        return m.group(1);
    }

    private org.springframework.test.web.servlet.ResultActions redefinir(String token, String senha) throws Exception {
        return mvc.perform(post("/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"" + token + "\",\"novaSenha\":\"" + senha + "\"}"));
    }

    @Test
    void fluxoCompletoTrocaASenhaEOLinkNaoServeDuasVezes() throws Exception {
        pedir(email.toUpperCase());   // e-mail em outra caixa tambem funciona
        assertThat(ENVIADOS).hasSize(1);
        assertThat(ENVIADOS.get(0).para()).isEqualTo(email);
        String token = tokenDoEmail();

        redefinir(token, "Nova1234!").andExpect(status().isOk());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"Nova1234!\"}"))
                .andExpect(status().isOk());

        redefinir(token, "Outra1234!").andExpect(status().isBadRequest());
    }

    @Test
    void emailInexistenteRecebeAMesmaRespostaSemEnvio() throws Exception {
        pedir("ninguem" + System.nanoTime() + "@teste.com");
        assertThat(ENVIADOS).isEmpty();
    }

    @Test
    void recusaTokenInventadoESenhaFraca() throws Exception {
        redefinir("token-inventado", "Nova1234!").andExpect(status().isBadRequest());
        pedir(email);
        redefinir(tokenDoEmail(), "fraca").andExpect(status().isBadRequest());
    }

    @Test
    void limitaPedidosPorHora() throws Exception {
        for (int i = 0; i < 5; i++) pedir(email);
        assertThat(ENVIADOS).hasSize(3);
    }

    @Test
    void numerosEPlanosSaoPublicos() throws Exception {
        mvc.perform(get("/publico/numeros")).andExpect(status().isOk()).andExpect(jsonPath("$.questoes").isNumber());
        mvc.perform(get("/planos")).andExpect(status().isOk()).andExpect(jsonPath("$.planos.length()").value(3));
    }
}

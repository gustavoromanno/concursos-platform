package com.gustavo.concursos;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PerfilTest {

    private static final String EMAIL = "perfil@teste.com";
    private static final String SENHA = "Senha-original1!";

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Cada teste parte da mesma senha, mesmo que outro teste a tenha trocado.
    @BeforeEach
    void preparar() {
        Usuario u = usuarioRepository.findByEmail(EMAIL).orElseGet(Usuario::new);
        u.setNome("Pessoa de Teste");
        u.setEmail(EMAIL);
        u.setSenhaHash(passwordEncoder.encode(SENHA));
        usuarioRepository.save(u);
    }

    private String senhas(String atual, String nova) {
        return "{\"senhaAtual\":\"" + atual + "\",\"novaSenha\":\"" + nova + "\"}";
    }

    @Test
    @WithMockUser(username = EMAIL)
    void alteraONome() throws Exception {
        mvc.perform(put("/perfil").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"  Nome Novo  \"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Novo"))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    @Test
    @WithMockUser(username = EMAIL)
    void nomeEmBrancoRecebe400() throws Exception {
        mvc.perform(put("/perfil").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"   \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void trocaSenhaEONovoLoginFunciona() throws Exception {
        mvc.perform(put("/perfil/senha").contentType(MediaType.APPLICATION_JSON)
                        .content(senhas(SENHA, "Nova-senha-123!")))
                .andExpect(status().isNoContent());

        assertThat(passwordEncoder.matches("Nova-senha-123!",
                usuarioRepository.findByEmail(EMAIL).orElseThrow().getSenhaHash())).isTrue();

        // Login real, pelo endpoint publico, com a senha nova.
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + EMAIL + "\",\"senha\":\"nova-senha-123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void senhaAtualErradaRecebe400ENaoTroca() throws Exception {
        // 400 e nao 401: um 401 faria o frontend deslogar quem so digitou errado.
        mvc.perform(put("/perfil/senha").contentType(MediaType.APPLICATION_JSON)
                        .content(senhas("chute-errado", "Nova-senha-123!")))
                .andExpect(status().isBadRequest());

        assertThat(passwordEncoder.matches(SENHA,
                usuarioRepository.findByEmail(EMAIL).orElseThrow().getSenhaHash())).isTrue();
    }

    @Test
    @WithMockUser(username = EMAIL)
    void novaSenhaCurtaOuIgualRecebe400() throws Exception {
        mvc.perform(put("/perfil/senha").contentType(MediaType.APPLICATION_JSON).content(senhas(SENHA, "123")))
                .andExpect(status().isBadRequest());
        mvc.perform(put("/perfil/senha").contentType(MediaType.APPLICATION_JSON).content(senhas(SENHA, SENHA)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void aceitaEDepoisRecusaEmailsPromocionais() throws Exception {
        mvc.perform(put("/perfil/marketing").contentType(MediaType.APPLICATION_JSON).content("{\"aceita\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aceitaMarketing").value(true));
        mvc.perform(put("/perfil/marketing").contentType(MediaType.APPLICATION_JSON).content("{\"aceita\":false}"))
                .andExpect(jsonPath("$.aceitaMarketing").value(false));
    }

    @Test
    void semTokenRecebe401() throws Exception {
        mvc.perform(put("/perfil").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"x\"}"))
                .andExpect(status().isUnauthorized());
    }
}

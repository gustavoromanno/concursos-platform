package com.gustavo.concursos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SegurancaTest {

    @Autowired
    private MockMvc mvc;

    // --- 401: nao identificado ---

    @Test
    void requisicaoSemTokenRecebe401() throws Exception {
        mvc.perform(get("/estatisticas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoRecebe401() throws Exception {
        mvc.perform(get("/estatisticas")
                        .header("Authorization", "Bearer token-falso-qualquer"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginComCredenciaisErradasRecebe401() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ninguem@teste.com\",\"senha\":\"errada123\"}"))
                .andExpect(status().isUnauthorized());
    }

    // --- 403: identificado, mas sem permissao ---

    @Test
    @WithMockUser(roles = "USUARIO")
    void usuarioComumNaoPodeApagarConcurso() throws Exception {
        mvc.perform(delete("/concursos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void usuarioComumNaoPodeCadastrarQuestao() throws Exception {
        mvc.perform(post("/questoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    // --- ADMIN passa pela seguranca ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPassaPelaSegurancaAoApagarConcurso() throws Exception {
        // O concurso 1 nao existe no H2, entao o controller pode responder 404 ou outro erro.
        // O que importa aqui e que a SEGURANCA nao barrou: nem 401, nem 403.
        mvc.perform(delete("/concursos/1"))
                .andExpect(result -> assertThat(result.getResponse().getStatus())
                        .isNotIn(401, 403));
    }
}

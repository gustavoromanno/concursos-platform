package com.gustavo.concursos;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SolicitacaoConteudoTest {

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;

    private String email;

    @BeforeEach
    void novoUsuario() {
        email = "pede" + System.nanoTime() + "@teste.com";
        Usuario u = new Usuario();
        u.setNome("Quem pede");
        u.setEmail(email);
        u.setSenhaHash("x");
        usuarioRepository.save(u);
    }

    private String pedido(String link) {
        return "{\"concurso\":\"Bacen 2024\",\"cargo\":\"Analista\",\"materia\":\"Economia Bancária\","
                + "\"linkEdital\":\"" + link + "\",\"detalhes\":\"Faltam questões de câmbio\"}";
    }

    @Test
    void enviaEAcompanhaOsProprioPedidos() throws Exception {
        mvc.perform(post("/solicitacoes").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                        .content(pedido("https://www.bcb.gov.br/edital.pdf")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("NOVA"))
                .andExpect(jsonPath("$.emailSolicitante").doesNotExist());

        mvc.perform(get("/solicitacoes").with(user(email)))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].materia").value("Economia Bancária"));
    }

    @Test
    void validaCamposELink() throws Exception {
        mvc.perform(post("/solicitacoes").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"concurso\":\"\",\"materia\":\"X\"}")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Informe o concurso ou edital"));
        mvc.perform(post("/solicitacoes").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content(pedido("javascript:alert(1)"))).andExpect(status().isBadRequest());
    }

    @Test
    void limiteDePedidosPorDia() throws Exception {
        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/solicitacoes").with(user(email)).contentType(MediaType.APPLICATION_JSON).content(pedido("")))
                    .andExpect(status().isCreated());
        }
        mvc.perform(post("/solicitacoes").with(user(email)).contentType(MediaType.APPLICATION_JSON).content(pedido("")))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void adminVeTodosERespondeUsuarioComumNao() throws Exception {
        String corpo = mvc.perform(post("/solicitacoes").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content(pedido(""))).andReturn().getResponse().getContentAsString();
        String id = corpo.replaceAll(".*\"id\":(\\d+).*", "$1");

        mvc.perform(get("/admin/solicitacoes").with(user(email))).andExpect(status().isForbidden());

        mvc.perform(get("/admin/solicitacoes").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        mvc.perform(put("/admin/solicitacoes/" + id).with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ATENDIDA\",\"resposta\":\"Lote publicado!\"}"))
                .andExpect(jsonPath("$.status").value("ATENDIDA"))
                .andExpect(jsonPath("$.emailSolicitante").value(email));

        mvc.perform(get("/solicitacoes").with(user(email)))
                .andExpect(jsonPath("$[0].resposta").value("Lote publicado!"));
    }
}

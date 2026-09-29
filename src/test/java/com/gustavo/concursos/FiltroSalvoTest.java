package com.gustavo.concursos;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
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
class FiltroSalvoTest {

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;

    private String novoUsuario() {
        String email = "filtro" + System.nanoTime() + "@teste.com";
        Usuario u = new Usuario();
        u.setNome("Filtra");
        u.setEmail(email);
        u.setSenhaHash("x");
        usuarioRepository.save(u);
        return email;
    }

    @Test
    void salvaListaEApagaSoOsProprios() throws Exception {
        String dono = novoUsuario();
        String outro = novoUsuario();
        mvc.perform(post("/filtros").with(user(dono)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Bacen CESPE\",\"parametros\":\"disciplinaId=3&bancaId=1\"}"))
                .andExpect(status().isCreated());

        String lista = mvc.perform(get("/filtros").with(user(dono)))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Bacen CESPE"))
                .andReturn().getResponse().getContentAsString();
        String id = lista.replaceAll("(?s).*\"id\":(\\d+).*", "$1");

        mvc.perform(get("/filtros").with(user(outro))).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(delete("/filtros/" + id).with(user(outro))).andExpect(status().isNotFound());
        mvc.perform(delete("/filtros/" + id).with(user(dono))).andExpect(status().isNoContent());
    }

    @Test
    void recusaParametrosComCodigo() throws Exception {
        mvc.perform(post("/filtros").with(user(novoUsuario())).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"x\",\"parametros\":\"<script>alert(1)</script>\"}"))
                .andExpect(status().isBadRequest());
    }
}

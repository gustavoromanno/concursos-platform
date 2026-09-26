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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminPainelTest {

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;

    @Test
    void painelEBuscaSoParaAdmin() throws Exception {
        mvc.perform(get("/admin/painel").with(user("x").roles("USUARIO"))).andExpect(status().isForbidden());
        mvc.perform(get("/admin/painel").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarios").isNumber())
                .andExpect(jsonPath("$.receitaMesCentavos").isNumber());
    }

    @Test
    void adminConcedeERemovePro() throws Exception {
        Usuario u = new Usuario();
        u.setNome("Cortesia");
        u.setEmail("cortesia" + System.nanoTime() + "@teste.com");
        u.setSenhaHash("x");
        usuarioRepository.save(u);

        mvc.perform(get("/admin/usuarios").param("busca", "cortesia").with(user("admin").roles("ADMIN")))
                .andExpect(jsonPath("$[0].email").value(u.getEmail()));

        mvc.perform(post("/admin/usuarios/" + u.getId() + "/pro").with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"dias\":15}")).andExpect(status().isOk());
        assertThat(usuarioRepository.findById(u.getId()).orElseThrow().ehPro()).isTrue();

        mvc.perform(post("/admin/usuarios/" + u.getId() + "/pro").with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"dias\":0}")).andExpect(status().isOk());
        assertThat(usuarioRepository.findById(u.getId()).orElseThrow().ehPro()).isFalse();
    }
}

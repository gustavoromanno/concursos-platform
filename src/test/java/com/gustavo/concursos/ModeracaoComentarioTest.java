package com.gustavo.concursos;

import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.BancaRepository;
import com.gustavo.concursos.repository.DisciplinaRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ModeracaoComentarioTest {

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DisciplinaRepository disciplinaRepository;
    @Autowired private BancaRepository bancaRepository;
    @Autowired private QuestaoRepository questaoRepository;

    private String usuario(String papel) {
        String email = papel.toLowerCase() + System.nanoTime() + "@teste.com";
        Usuario u = new Usuario();
        u.setNome("Pessoa");
        u.setEmail(email);
        u.setSenhaHash("x");
        u.setPapel(papel);
        usuarioRepository.save(u);
        return email;
    }

    @Test
    void outroAlunoNaoApagaMasAdminModera() throws Exception {
        Questao q = ReporteQuestaoTest.questaoDeTeste(disciplinaRepository, bancaRepository, questaoRepository);
        String autor = usuario("USUARIO");
        String outro = usuario("USUARIO");
        String admin = usuario("ADMIN");

        String corpo = mvc.perform(post("/questoes/" + q.getId() + "/comentarios").with(user(autor))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"texto\":\"Comentario a moderar\"}"))
                .andReturn().getResponse().getContentAsString();
        String id = corpo.replaceAll("(?s).*\"id\":(\\d+).*", "$1");

        mvc.perform(delete("/comentarios/" + id).with(user(outro))).andExpect(status().isForbidden());
        mvc.perform(delete("/comentarios/" + id).with(user(admin).roles("ADMIN"))).andExpect(status().isNoContent());
    }
}

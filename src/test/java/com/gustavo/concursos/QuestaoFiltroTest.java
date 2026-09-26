package com.gustavo.concursos;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Garante que os filtros novos da listagem de questoes montam uma consulta
 * valida (sem erro de SQL/Criteria) e que a situacao invalida e recusada.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuestaoFiltroTest {

    private static final String EMAIL = "filtros@teste.com";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void garantirUsuario() {
        if (usuarioRepository.findByEmail(EMAIL).isEmpty()) {
            Usuario u = new Usuario();
            u.setNome("Usuario de Teste");
            u.setEmail(EMAIL);
            u.setSenhaHash("nao-usado-neste-teste");
            usuarioRepository.save(u);
        }
    }

    @Test
    @WithMockUser(username = EMAIL)
    void palavraChaveEComentariosMontamConsultaValida() throws Exception {
        mvc.perform(get("/questoes").param("palavraChave", "Crase").param("comComentarios", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void usuarioSemRespostasNaoTemQuestoesResolvidas() throws Exception {
        mvc.perform(get("/questoes").param("situacao", "RESOLVIDAS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void naoResolvidasSemHistoricoNaoRestringe() throws Exception {
        mvc.perform(get("/questoes").param("situacao", "NAO_RESOLVIDAS"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void certasEErradasMontamConsultaValida() throws Exception {
        mvc.perform(get("/questoes").param("situacao", "CERTAS")).andExpect(status().isOk());
        mvc.perform(get("/questoes").param("situacao", "ERRADAS")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = EMAIL)
    void situacaoInvalidaRecebe400() throws Exception {
        mvc.perform(get("/questoes").param("situacao", "QUALQUER"))
                .andExpect(status().isBadRequest());
    }
}

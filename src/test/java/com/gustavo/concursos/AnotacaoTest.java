package com.gustavo.concursos;

import com.gustavo.concursos.entity.Banca;
import com.gustavo.concursos.entity.Disciplina;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.BancaRepository;
import com.gustavo.concursos.repository.DisciplinaRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnotacaoTest {

    private static final String DONO = "anotador@teste.com";
    private static final String OUTRO = "curioso@teste.com";

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DisciplinaRepository disciplinaRepository;
    @Autowired private BancaRepository bancaRepository;
    @Autowired private QuestaoRepository questaoRepository;

    private Long questaoId;

    @BeforeEach
    void preparar() {
        garantirUsuario(DONO);
        garantirUsuario(OUTRO);

        Disciplina d = new Disciplina();
        d.setNome("Disciplina de teste");
        disciplinaRepository.save(d);

        Banca b = new Banca();
        b.setNome("Banca de teste");
        bancaRepository.save(b);

        Questao q = new Questao();
        q.setEnunciado("Questao usada no teste de anotacoes");
        q.setDisciplina(d);
        q.setBanca(b);
        q.setAno(2025);
        questaoId = questaoRepository.save(q).getId();
    }

    private void garantirUsuario(String email) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Usuario u = new Usuario();
            u.setNome("Teste");
            u.setEmail(email);
            u.setSenhaHash("nao-usado");
            // Anotacoes sao do plano Pro.
            u.setProAte(java.time.LocalDateTime.now().plusDays(30));
            usuarioRepository.save(u);
        }
    }

    private String json(String texto) {
        return "{\"texto\":\"" + texto + "\"}";
    }

    @Test
    @WithMockUser(username = DONO)
    void salvaELeAPropriaAnotacao() throws Exception {
        mvc.perform(put("/questoes/{id}/anotacao", questaoId)
                        .contentType(MediaType.APPLICATION_JSON).content(json("  Cai muito na FGV  ")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.texto").value("Cai muito na FGV"));

        mvc.perform(get("/questoes/{id}/anotacao", questaoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.texto").value("Cai muito na FGV"));

        mvc.perform(get("/anotacoes/ids"))
                .andExpect(jsonPath("$", hasItem(questaoId.intValue())));
    }

    @Test
    @WithMockUser(username = DONO)
    void filtroComAnotacoesTrazAQuestaoAnotada() throws Exception {
        mvc.perform(put("/questoes/{id}/anotacao", questaoId)
                        .contentType(MediaType.APPLICATION_JSON).content(json("revisar")))
                .andExpect(status().isOk());

        mvc.perform(get("/questoes").param("comAnotacoes", "true").param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", hasItem(questaoId.intValue())));
    }

    @Test
    @WithMockUser(username = DONO)
    void textoEmBrancoApagaAAnotacao() throws Exception {
        mvc.perform(put("/questoes/{id}/anotacao", questaoId)
                        .contentType(MediaType.APPLICATION_JSON).content(json("temporaria")))
                .andExpect(status().isOk());

        mvc.perform(put("/questoes/{id}/anotacao", questaoId)
                        .contentType(MediaType.APPLICATION_JSON).content(json("   ")))
                .andExpect(status().isNoContent());

        mvc.perform(get("/questoes/{id}/anotacao", questaoId))
                .andExpect(status().isNoContent());
    }

    @Test
    void anotacaoEPrivadaDoAutor() throws Exception {
        mvc.perform(put("/questoes/{id}/anotacao", questaoId).with(user(DONO))
                        .contentType(MediaType.APPLICATION_JSON).content(json("so minha")))
                .andExpect(status().isOk());

        // Outro usuario nao enxerga a anotacao nem pelo endpoint nem pelo filtro.
        mvc.perform(get("/questoes/{id}/anotacao", questaoId).with(user(OUTRO)))
                .andExpect(status().isNoContent());
        mvc.perform(get("/questoes").param("comAnotacoes", "true").param("size", "100").with(user(OUTRO)))
                .andExpect(jsonPath("$.content[*].id", not(hasItem(questaoId.intValue()))));
    }

    @Test
    @WithMockUser(username = DONO)
    void anotarQuestaoInexistenteRecebe404() throws Exception {
        mvc.perform(put("/questoes/{id}/anotacao", 999999)
                        .contentType(MediaType.APPLICATION_JSON).content(json("nada")))
                .andExpect(status().isNotFound());
    }

    @Test
    void semTokenRecebe401() throws Exception {
        mvc.perform(get("/questoes/{id}/anotacao", questaoId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = DONO)
    void filtroDeDificuldadeValidaOValor() throws Exception {
        mvc.perform(get("/questoes").param("dificuldade", "FACIL")).andExpect(status().isOk());
        mvc.perform(get("/questoes").param("dificuldade", "IMPOSSIVEL")).andExpect(status().isBadRequest());
    }
}

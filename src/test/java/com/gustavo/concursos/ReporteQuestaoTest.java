package com.gustavo.concursos;

import com.gustavo.concursos.entity.Banca;
import com.gustavo.concursos.entity.Disciplina;
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

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReporteQuestaoTest {

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DisciplinaRepository disciplinaRepository;
    @Autowired private BancaRepository bancaRepository;
    @Autowired private QuestaoRepository questaoRepository;

    static Questao questaoDeTeste(DisciplinaRepository dr, BancaRepository br, QuestaoRepository qr) {
        Disciplina d = new Disciplina();
        d.setNome("Disciplina " + System.nanoTime());
        dr.save(d);
        Banca b = new Banca();
        b.setNome("Banca " + System.nanoTime());
        br.save(b);
        Questao q = new Questao();
        q.setEnunciado("Enunciado original da questao de teste");
        q.setExplicacao("Explicacao original");
        q.setDisciplina(d);
        q.setBanca(b);
        q.setAno(2025);
        for (int i = 1; i <= 4; i++) {
            com.gustavo.concursos.entity.Alternativa a = new com.gustavo.concursos.entity.Alternativa();
            a.setQuestao(q);
            a.setTexto("Alternativa " + i);
            a.setCorreta(i == 1);
            a.setOrdem(i);
            q.getAlternativas().add(a);
        }
        return qr.save(q);
    }

    @Test
    void alunoReportaEAdminResolve() throws Exception {
        String email = "reporta" + System.nanoTime() + "@teste.com";
        Usuario u = new Usuario();
        u.setNome("Aluno");
        u.setEmail(email);
        u.setSenhaHash("x");
        usuarioRepository.save(u);
        Questao q = questaoDeTeste(disciplinaRepository, bancaRepository, questaoRepository);

        mvc.perform(post("/questoes/" + q.getId() + "/reportes").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivo\":\"GABARITO\",\"descricao\":\"A correta e a B\"}")).andExpect(status().isCreated());
        mvc.perform(post("/questoes/" + q.getId() + "/reportes").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivo\":\"QUALQUER\"}")).andExpect(status().isBadRequest());

        mvc.perform(get("/admin/reportes").param("status", "NOVO").with(user(email))).andExpect(status().isForbidden());
        String lista = mvc.perform(get("/admin/reportes").param("status", "NOVO").with(user("admin").roles("ADMIN")))
                .andExpect(jsonPath("$[*].questaoId", hasItem(q.getId().intValue())))
                .andReturn().getResponse().getContentAsString();
        String id = lista.replaceAll("(?s).*\"id\":(\\d+),\"questaoId\":" + q.getId() + ".*", "$1");

        mvc.perform(put("/admin/reportes/" + id).with(user("admin").roles("ADMIN")).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"RESOLVIDO\",\"resposta\":\"Corrigido\"}"))
                .andExpect(jsonPath("$.status").value("RESOLVIDO"))
                .andExpect(jsonPath("$.email").value(email));
    }
}

package com.gustavo.concursos;

import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.repository.BancaRepository;
import com.gustavo.concursos.repository.DisciplinaRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminQuestaoEditorTest {

    @Autowired private MockMvc mvc;
    @Autowired private DisciplinaRepository disciplinaRepository;
    @Autowired private BancaRepository bancaRepository;
    @Autowired private QuestaoRepository questaoRepository;
    @Autowired private ObjectMapper json;

    @Test
    void adminCorrigeTextoEGabarito() throws Exception {
        Questao q = ReporteQuestaoTest.questaoDeTeste(disciplinaRepository, bancaRepository, questaoRepository);
        JsonNode atual = json.readTree(mvc.perform(get("/admin/questoes/" + q.getId()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

        StringBuilder alts = new StringBuilder();
        for (int i = 0; i < atual.get("alternativas").size(); i++) {
            long idAlt = atual.get("alternativas").get(i).get("id").asLong();
            if (i > 0) alts.append(',');
            alts.append("{\"id\":").append(idAlt).append(",\"texto\":\"Nova ").append(i + 1)
                .append("\",\"correta\":").append(i == 1).append('}');   // gabarito passa da 1a para a 2a
        }
        String corpo = "{\"enunciado\":\"Enunciado corrigido\",\"explicacao\":\"Nova explicacao\",\"alternativas\":[" + alts + "]}";

        mvc.perform(put("/admin/questoes/" + q.getId()).with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enunciado").value("Enunciado corrigido"))
                .andExpect(jsonPath("$.alternativas[1].correta").value(true))
                .andExpect(jsonPath("$.alternativas[0].correta").value(false))
                .andExpect(jsonPath("$.alternativas[0].texto").value("Nova 1"));

        // Duas corretas: recusa. Usuario comum: 403.
        String duas = corpo.replace("\"correta\":false", "\"correta\":true");
        mvc.perform(put("/admin/questoes/" + q.getId()).with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON).content(duas)).andExpect(status().isBadRequest());
        mvc.perform(get("/admin/questoes/" + q.getId()).with(user("x").roles("USUARIO"))).andExpect(status().isForbidden());
    }
}

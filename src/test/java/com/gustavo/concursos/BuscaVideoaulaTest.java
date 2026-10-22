package com.gustavo.concursos;

import com.gustavo.concursos.entity.Alternativa;
import com.gustavo.concursos.entity.Assunto;
import com.gustavo.concursos.entity.Banca;
import com.gustavo.concursos.entity.Disciplina;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.repository.AssuntoRepository;
import com.gustavo.concursos.repository.BancaRepository;
import com.gustavo.concursos.repository.DisciplinaRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.youtube.YoutubeBusca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Busca de videoaulas no YouTube (ferramenta do admin), com uma busca falsa no lugar da API. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BuscaVideoaulaTest {

    static final AtomicReference<String> ULTIMO_TERMO = new AtomicReference<>();

    @TestConfiguration
    static class BuscaFalsa {
        @Bean
        @Primary
        YoutubeBusca youtubeBuscaFalsa() {
            return (termo, maximo) -> {
                ULTIMO_TERMO.set(termo);
                return List.of(
                        new YoutubeBusca.VideoEncontrado("aaaaaaaaaaa", "Aula A", "Canal A", "https://i.ytimg.com/a.jpg", 20),
                        new YoutubeBusca.VideoEncontrado("bbbbbbbbbbb", "Aula B", "Canal B", "https://i.ytimg.com/b.jpg", null));
            };
        }
    }

    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private DisciplinaRepository disciplinaRepository;
    @Autowired private AssuntoRepository assuntoRepository;
    @Autowired private BancaRepository bancaRepository;
    @Autowired private QuestaoRepository questaoRepository;

    private Disciplina disciplina;
    private Long assuntoId;

    @BeforeEach
    void preparar() {
        jdbc.update("DELETE FROM videoaula WHERE youtube_id IN ('aaaaaaaaaaa', 'bbbbbbbbbbb')");
        disciplina = new Disciplina();
        disciplina.setNome("Disciplina video " + System.nanoTime());
        disciplinaRepository.save(disciplina);
        Assunto a = new Assunto();
        a.setDisciplina(disciplina);
        a.setNome("Crase");
        assuntoId = assuntoRepository.save(a).getId();

        Banca b = new Banca();
        b.setNome("Banca video " + System.nanoTime());
        bancaRepository.save(b);
        Questao q = new Questao();
        q.setEnunciado("Ha crase em 'vou a escola'?");
        q.setDisciplina(disciplina);
        q.setAssuntoRef(a);
        q.setBanca(b);
        q.setAno(2026);
        Alternativa sim = new Alternativa();
        sim.setTexto("Sim");
        sim.setCorreta(true);
        sim.setOrdem(1);
        sim.setQuestao(q);
        q.getAlternativas().add(sim);
        questaoRepository.save(q);
    }

    @Test
    void soAdminBusca() throws Exception {
        mvc.perform(get("/admin/videoaulas/buscar").param("assuntoId", assuntoId.toString())
                .with(user("x").roles("USUARIO"))).andExpect(status().isForbidden());
        mvc.perform(get("/admin/videoaulas/cobertura").with(user("x").roles("USUARIO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void buscaMontaOTermoPeloAssuntoEMarcaOsJaCadastrados() throws Exception {
        mvc.perform(post("/videoaulas").with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Aula A\",\"urlOuId\":\"https://youtu.be/aaaaaaaaaaa\",\"disciplinaId\":"
                        + disciplina.getId() + ",\"assuntoId\":" + assuntoId + "}"))
                .andExpect(status().is2xxSuccessful());

        mvc.perform(get("/admin/videoaulas/buscar").param("assuntoId", assuntoId.toString())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.termo").value("Crase " + disciplina.getNome() + " aula concurso"))
                .andExpect(jsonPath("$.videos[0].youtubeId").value("aaaaaaaaaaa"))
                .andExpect(jsonPath("$.videos[0].jaCadastrado").value(true))
                .andExpect(jsonPath("$.videos[0].duracaoMinutos").value(20))
                .andExpect(jsonPath("$.videos[1].jaCadastrado").value(false));

        mvc.perform(get("/admin/videoaulas/buscar").param("assuntoId", assuntoId.toString())
                        .param("termo", "  crase regra geral ").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
        assertThat(ULTIMO_TERMO.get()).isEqualTo("crase regra geral");
    }

    @Test
    void coberturaContaQuestoesEVideosPorAssunto() throws Exception {
        mvc.perform(get("/admin/videoaulas/cobertura").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.assuntoId == " + assuntoId + ")].questoes").value(1))
                .andExpect(jsonPath("$[?(@.assuntoId == " + assuntoId + ")].videos").value(0));
    }

    @Test
    void assuntoInexistenteDa404() throws Exception {
        mvc.perform(get("/admin/videoaulas/buscar").param("assuntoId", "999999")
                .with(user("admin").roles("ADMIN"))).andExpect(status().isNotFound());
    }
}

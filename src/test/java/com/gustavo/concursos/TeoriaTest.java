package com.gustavo.concursos;

import com.gustavo.concursos.entity.Assunto;
import com.gustavo.concursos.entity.Disciplina;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.AssuntoRepository;
import com.gustavo.concursos.repository.DisciplinaRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Teoria por assunto: resumos gratuitos para todos, os demais so para o Pro; edicao so do admin. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TeoriaTest {

    private static final String GRATIS = "leitor-gratis@teste.com";
    private static final String PRO = "leitor-pro@teste.com";

    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DisciplinaRepository disciplinaRepository;
    @Autowired private AssuntoRepository assuntoRepository;

    private Long assuntoGratis;
    private Long assuntoPro;
    private Long assuntoSemResumo;

    @BeforeEach
    void preparar() {
        garantirUsuario(GRATIS, null);
        garantirUsuario(PRO, LocalDateTime.now().plusDays(30));

        Disciplina d = new Disciplina();
        d.setNome("Conhecimentos de teste " + System.nanoTime());
        disciplinaRepository.save(d);

        assuntoGratis = criarAssunto(d, "Assunto livre");
        assuntoPro = criarAssunto(d, "Assunto do Pro");
        assuntoSemResumo = criarAssunto(d, "Assunto sem resumo");

        inserirResumo(assuntoGratis, "Resumo livre", true);
        inserirResumo(assuntoPro, "Resumo do Pro", false);
    }

    private void garantirUsuario(String email, LocalDateTime proAte) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Usuario u = new Usuario();
            u.setNome("Leitor");
            u.setEmail(email);
            u.setSenhaHash("nao-usado");
            u.setProAte(proAte);
            usuarioRepository.save(u);
        }
    }

    private Long criarAssunto(Disciplina d, String nome) {
        Assunto a = new Assunto();
        a.setDisciplina(d);
        a.setNome(nome);
        return assuntoRepository.save(a).getId();
    }

    private void inserirResumo(Long assuntoId, String titulo, boolean gratuito) {
        jdbc.update("INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito, atualizado_em) VALUES (?, ?, ?, ?, ?)",
                assuntoId, titulo, "## Ideia central\n\nTexto do resumo.", gratuito, LocalDateTime.now());
    }

    @Test
    void indiceListaOsResumosSemOTexto() throws Exception {
        mvc.perform(get("/teoria").with(user(GRATIS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].assuntoId", hasItem(assuntoGratis.intValue())))
                .andExpect(jsonPath("$[*].assuntoId", hasItem(assuntoPro.intValue())))
                .andExpect(jsonPath("$[0].conteudo").doesNotExist());
    }

    @Test
    void semLoginNaoLe() throws Exception {
        mvc.perform(get("/teoria/" + assuntoGratis)).andExpect(status().is4xxClientError());
    }

    @Test
    void planoGratuitoLeOResumoLivreMasNaoODoPro() throws Exception {
        mvc.perform(get("/teoria/" + assuntoGratis).with(user(GRATIS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Resumo livre"))
                .andExpect(jsonPath("$.conteudo").value("## Ideia central\n\nTexto do resumo."));

        mvc.perform(get("/teoria/" + assuntoPro).with(user(GRATIS)))
                .andExpect(status().isPaymentRequired());
    }

    @Test
    void proEAdminLeemTudo() throws Exception {
        mvc.perform(get("/teoria/" + assuntoPro).with(user(PRO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Resumo do Pro"));

        mvc.perform(get("/teoria/" + assuntoPro).with(user("admin@teste.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void assuntoSemResumoDa404() throws Exception {
        mvc.perform(get("/teoria/" + assuntoSemResumo).with(user(PRO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminCriaEditaEExcluiResumo() throws Exception {
        String corpo = "{\"titulo\":\"Novo resumo\",\"conteudo\":\"Primeira versao\",\"gratuito\":false}";
        mvc.perform(put("/admin/teoria/" + assuntoSemResumo).with(user("admin@teste.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk());

        String edicao = "{\"titulo\":\"Novo resumo\",\"conteudo\":\"Segunda versao\",\"gratuito\":true}";
        mvc.perform(put("/admin/teoria/" + assuntoSemResumo).with(user("admin@teste.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON).content(edicao))
                .andExpect(status().isOk());

        // Continua um resumo so por assunto, agora liberado no plano gratuito.
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM resumo_assunto WHERE assunto_id = ?",
                Integer.class, assuntoSemResumo)).isEqualTo(1);
        mvc.perform(get("/teoria/" + assuntoSemResumo).with(user(GRATIS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo").value("Segunda versao"));

        mvc.perform(delete("/admin/teoria/" + assuntoSemResumo).with(user("admin@teste.com").roles("ADMIN")))
                .andExpect(status().isNoContent());
        mvc.perform(get("/teoria/" + assuntoSemResumo).with(user(GRATIS)))
                .andExpect(status().isNotFound());
    }

    @Test
    void usuarioComumNaoEdita() throws Exception {
        String corpo = "{\"titulo\":\"Tentativa\",\"conteudo\":\"Texto\",\"gratuito\":true}";
        mvc.perform(put("/admin/teoria/" + assuntoPro).with(user(PRO))
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isForbidden());
    }

    @Test
    void conteudoVazioERecusado() throws Exception {
        String corpo = "{\"titulo\":\"Sem texto\",\"conteudo\":\"\",\"gratuito\":true}";
        mvc.perform(put("/admin/teoria/" + assuntoPro).with(user("admin@teste.com").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest());
    }
}

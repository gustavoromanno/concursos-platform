package com.gustavo.concursos;

import com.gustavo.concursos.entity.Alternativa;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Resposta de questao em duas idas ao banco: resultado, gravacao da resposta e repeticao espacada. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RespostaTest {

    @Autowired private MockMvc mvc;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DisciplinaRepository disciplinaRepository;
    @Autowired private BancaRepository bancaRepository;
    @Autowired private QuestaoRepository questaoRepository;

    private String email;
    private Long usuarioId;
    private Questao questao;
    private Long certa;
    private Long errada;

    @BeforeEach
    void preparar() {
        email = "respondente" + System.nanoTime() + "@teste.com";
        Usuario u = new Usuario();
        u.setNome("Respondente");
        u.setEmail(email);
        u.setSenhaHash("x");
        usuarioId = usuarioRepository.save(u).getId();
        questao = criarQuestao(null);
    }

    private Questao criarQuestao(String origem) {
        Disciplina d = new Disciplina();
        d.setNome("Disciplina resposta " + System.nanoTime());
        disciplinaRepository.save(d);
        Banca b = new Banca();
        b.setNome("Banca resposta " + System.nanoTime());
        bancaRepository.save(b);

        Questao q = new Questao();
        q.setEnunciado("Quanto e 2 + 2?");
        q.setDisciplina(d);
        q.setBanca(b);
        q.setAno(2026);
        q.setExplicacao("Soma simples: 4.");
        if (origem != null) q.setOrigem(origem);
        Alternativa a1 = new Alternativa();
        a1.setTexto("4");
        a1.setCorreta(true);
        a1.setOrdem(1);
        a1.setQuestao(q);
        Alternativa a2 = new Alternativa();
        a2.setTexto("5");
        a2.setCorreta(false);
        a2.setOrdem(2);
        a2.setQuestao(q);
        q.getAlternativas().add(a1);
        q.getAlternativas().add(a2);
        Questao salva = questaoRepository.save(q);
        certa = salva.getAlternativas().stream().filter(Alternativa::isCorreta).findFirst().orElseThrow().getId();
        errada = salva.getAlternativas().stream().filter(a -> !a.isCorreta()).findFirst().orElseThrow().getId();
        return salva;
    }

    private ResultActions responder(Long questaoId, Long alternativaId) throws Exception {
        return mvc.perform(post("/questoes/" + questaoId + "/responder").with(user(email))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"alternativaId\":" + alternativaId + "}"));
    }

    private Map<String, Object> revisao() {
        return jdbc.queryForMap("SELECT repeticoes, intervalo_dias, facilidade, proxima_revisao FROM revisao "
                + "WHERE usuario_id = ? AND questao_id = ?", usuarioId, questao.getId());
    }

    @Test
    void acertoDevolveResultadoGravaRespostaECriaRevisao() throws Exception {
        responder(questao.getId(), certa)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correta").value(true))
                .andExpect(jsonPath("$.alternativaCorretaId").value(certa))
                .andExpect(jsonPath("$.explicacao").value("Soma simples: 4."));

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM resposta WHERE usuario_id = ? AND questao_id = ? AND correta = TRUE",
                Integer.class, usuarioId, questao.getId())).isEqualTo(1);
        Map<String, Object> r = revisao();
        assertThat(((Number) r.get("repeticoes")).intValue()).isEqualTo(1);
        assertThat(((Number) r.get("intervalo_dias")).intValue()).isEqualTo(1);
        assertThat(new BigDecimal(r.get("facilidade").toString())).isEqualByComparingTo("2.60");
        assertThat(r.get("proxima_revisao").toString()).isEqualTo(LocalDate.now().plusDays(1).toString());
    }

    @Test
    void segundaRespostaAtualizaARevisaoExistente() throws Exception {
        responder(questao.getId(), certa).andExpect(status().isOk());
        responder(questao.getId(), certa).andExpect(status().isOk());
        Map<String, Object> r = revisao();
        assertThat(((Number) r.get("repeticoes")).intValue()).isEqualTo(2);
        assertThat(((Number) r.get("intervalo_dias")).intValue()).isEqualTo(6);

        responder(questao.getId(), errada)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correta").value(false))
                .andExpect(jsonPath("$.alternativaCorretaId").value(certa));
        r = revisao();
        assertThat(((Number) r.get("repeticoes")).intValue()).isZero();
        assertThat(((Number) r.get("intervalo_dias")).intValue()).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM revisao WHERE usuario_id = ? AND questao_id = ?",
                Integer.class, usuarioId, questao.getId())).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM resposta WHERE usuario_id = ?", Integer.class, usuarioId))
                .isEqualTo(3);
    }

    @Test
    void alternativaDeOutraQuestaoERecusada() throws Exception {
        responder(questao.getId(), 999_999L).andExpect(status().isBadRequest());
    }

    @Test
    void questaoInexistenteDa404() throws Exception {
        responder(999_999L, certa).andExpect(status().isNotFound());
    }

    @Test
    void ineditaDaIaSoParaPro() throws Exception {
        Questao ia = criarQuestao(Questao.ORIGEM_IA);
        Long certaIa = certa;
        responder(ia.getId(), certaIa).andExpect(status().isPaymentRequired());

        Usuario u = usuarioRepository.findById(usuarioId).orElseThrow();
        u.setProAte(LocalDateTime.now().plusDays(10));
        usuarioRepository.save(u);
        responder(ia.getId(), certaIa).andExpect(status().isOk()).andExpect(jsonPath("$.correta").value(true));
    }
}

package com.gustavo.concursos.pro;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {"stripe.segredo-webhook=whsec_teste", "pro.simulados-gratis-por-mes=2"})
@Import(PlanoProTest.StripeFalso.class)
class PlanoProTest {

    static final AtomicLong SESSOES = new AtomicLong();

    @TestConfiguration
    static class StripeFalso {
        @Bean
        @Primary
        StripeCliente stripeFalso() {
            return (pagamentoId, email, descricao, valor, sucesso, cancelamento) ->
                    new StripeCliente.Sessao("cs_teste_" + SESSOES.incrementAndGet(), "https://checkout.stripe.test/" + pagamentoId);
        }
    }

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private DisciplinaRepository disciplinaRepository;
    @Autowired private BancaRepository bancaRepository;
    @Autowired private QuestaoRepository questaoRepository;

    private String email;

    @BeforeEach
    void novoUsuario() {
        email = "aluno" + System.nanoTime() + "@teste.com";
        Usuario u = new Usuario();
        u.setNome("Aluno");
        u.setEmail(email);
        u.setSenhaHash("x");
        usuarioRepository.save(u);
    }

    private Usuario usuario() {
        return usuarioRepository.findByEmail(email).orElseThrow();
    }

    private String checkout(String plano) throws Exception {
        mvc.perform(post("/pagamentos/checkout").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plano\":\"" + plano + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").exists());
        return "cs_teste_" + SESSOES.get();
    }

    private ResultActions webhook(String corpo) throws Exception {
        long t = Instant.now().getEpochSecond();
        return mvc.perform(post("/pagamentos/webhook").contentType(MediaType.APPLICATION_JSON).content(corpo)
                .header("Stripe-Signature", AssinaturaWebhookTest.assinar(corpo, "whsec_teste", t)));
    }

    private String evento(String id, String tipo, String objeto) {
        return "{\"id\":\"" + id + "\",\"type\":\"" + tipo + "\",\"data\":{\"object\":" + objeto + "}}";
    }

    @Test
    void gratuitoRecebe402NosRecursosPro() throws Exception {
        mvc.perform(get("/revisoes/hoje").with(user(email))).andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.message").value("A revisão espaçada é um recurso do plano Pro."));
        mvc.perform(get("/objetivo").with(user(email))).andExpect(status().isPaymentRequired());
        mvc.perform(get("/estatisticas/assuntos").with(user(email))).andExpect(status().isPaymentRequired());
        mvc.perform(get("/questoes").param("origem", "IA").with(user(email))).andExpect(status().isPaymentRequired());
        mvc.perform(get("/estatisticas").with(user(email))).andExpect(status().isOk());   // resumo basico e livre
    }

    @Test
    void cartaoPagoLiberaTrintaDias() throws Exception {
        String sessao = checkout("MENSAL");
        webhook(evento("evt_a" + sessao, "checkout.session.completed",
                "{\"id\":\"" + sessao + "\",\"payment_status\":\"paid\",\"payment_intent\":\"pi_" + sessao + "\"}"))
                .andExpect(status().isOk());

        assertThat(usuario().getProAte()).isCloseTo(LocalDateTime.now().plusDays(30), within(1, java.time.temporal.ChronoUnit.MINUTES));
        mvc.perform(get("/revisoes/hoje").with(user(email))).andExpect(status().isOk());
        mvc.perform(get("/perfil").with(user(email))).andExpect(jsonPath("$.pro").value(true));
    }

    @Test
    void eventoRepetidoNaoDaDiasEmDobro() throws Exception {
        String sessao = checkout("MENSAL");
        String corpo = evento("evt_rep" + sessao, "checkout.session.completed",
                "{\"id\":\"" + sessao + "\",\"payment_status\":\"paid\",\"payment_intent\":\"pi_r" + sessao + "\"}");
        webhook(corpo).andExpect(status().isOk());
        LocalDateTime depoisDoPrimeiro = usuario().getProAte();
        webhook(corpo).andExpect(status().isOk());
        assertThat(usuario().getProAte()).isEqualTo(depoisDoPrimeiro);
    }

    @Test
    void boletoSoLiberaQuandoCompensaEDiasSeSomam() throws Exception {
        String sessao = checkout("TRIMESTRAL");
        webhook(evento("evt_b1" + sessao, "checkout.session.completed",
                "{\"id\":\"" + sessao + "\",\"payment_status\":\"unpaid\",\"payment_intent\":\"pi_b" + sessao + "\"}"));
        assertThat(usuario().getProAte()).isNull();
        assertThat(pagamentoRepository.findByStripeSessaoId(sessao).orElseThrow().getStatus()).isEqualTo(Pagamento.AGUARDANDO);

        webhook(evento("evt_b2" + sessao, "checkout.session.async_payment_succeeded", "{\"id\":\"" + sessao + "\"}"));
        assertThat(usuario().getProAte()).isCloseTo(LocalDateTime.now().plusDays(90), within(1, java.time.temporal.ChronoUnit.MINUTES));

        // Segunda compra antes de vencer: soma ao fim do periodo atual.
        String outra = checkout("MENSAL");
        webhook(evento("evt_b3" + outra, "checkout.session.completed",
                "{\"id\":\"" + outra + "\",\"payment_status\":\"paid\",\"payment_intent\":\"pi_o" + outra + "\"}"));
        assertThat(usuario().getProAte()).isCloseTo(LocalDateTime.now().plusDays(120), within(1, java.time.temporal.ChronoUnit.MINUTES));
    }

    @Test
    void reembolsoDevolveOsDias() throws Exception {
        String sessao = checkout("MENSAL");
        webhook(evento("evt_r1" + sessao, "checkout.session.completed",
                "{\"id\":\"" + sessao + "\",\"payment_status\":\"paid\",\"payment_intent\":\"pi_rr" + sessao + "\"}"));
        webhook(evento("evt_r2" + sessao, "charge.refunded", "{\"id\":\"ch_1\",\"payment_intent\":\"pi_rr" + sessao + "\"}"));

        assertThat(pagamentoRepository.findByStripeSessaoId(sessao).orElseThrow().getStatus()).isEqualTo(Pagamento.REEMBOLSADO);
        assertThat(usuario().ehPro()).isFalse();
    }

    @Test
    void webhookSemAssinaturaValidaERecusado() throws Exception {
        String sessao = checkout("MENSAL");
        String corpo = evento("evt_x" + sessao, "checkout.session.completed",
                "{\"id\":\"" + sessao + "\",\"payment_status\":\"paid\"}");
        mvc.perform(post("/pagamentos/webhook").contentType(MediaType.APPLICATION_JSON).content(corpo)
                        .header("Stripe-Signature", "t=1,v1=abc"))
                .andExpect(status().isBadRequest());
        assertThat(usuario().getProAte()).isNull();
    }

    @Test
    void planoInexistenteERecusadoEPlanosSaoListados() throws Exception {
        mvc.perform(post("/pagamentos/checkout").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"plano\":\"VITALICIO\"}")).andExpect(status().isBadRequest());
        mvc.perform(get("/planos").with(user(email)))
                .andExpect(jsonPath("$.planos.length()").value(3))
                .andExpect(jsonPath("$.simuladosGratisPorMes").value(2));
    }

    @Test
    void gratuitoTemLimiteDeSimuladosPorMes() throws Exception {
        Disciplina d = new Disciplina();
        d.setNome("Disciplina simulado " + System.nanoTime());
        disciplinaRepository.save(d);
        Banca b = new Banca();
        b.setNome("Banca simulado " + System.nanoTime());
        bancaRepository.save(b);
        Questao q = new Questao();
        q.setEnunciado("Questao para o teste de limite de simulados");
        q.setDisciplina(d);
        q.setBanca(b);
        q.setAno(2025);
        for (int i = 1; i <= 2; i++) {
            Alternativa a = new Alternativa();
            a.setQuestao(q);
            a.setTexto(i == 1 ? "Certo" : "Errado");
            a.setCorreta(i == 1);
            a.setOrdem(i);
            q.getAlternativas().add(a);
        }
        questaoRepository.save(q);

        String pedido = "{\"quantidade\":1,\"duracaoMinutos\":10,\"disciplinaId\":" + d.getId() + "}";
        for (int i = 0; i < 2; i++) {
            mvc.perform(post("/simulados").with(user(email)).contentType(MediaType.APPLICATION_JSON).content(pedido))
                    .andExpect(status().isCreated());
        }
        mvc.perform(post("/simulados").with(user(email)).contentType(MediaType.APPLICATION_JSON).content(pedido))
                .andExpect(status().isPaymentRequired());
    }
}

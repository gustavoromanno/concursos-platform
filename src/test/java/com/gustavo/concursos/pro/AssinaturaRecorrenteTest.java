package com.gustavo.concursos.pro;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Assinatura mensal com renovacao automatica: checkout, faturas, cancelamento, reembolso e exclusao da conta. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {"stripe.segredo-webhook=whsec_teste", "pro.recorrente.ativo=true",
        "pro.preco.recorrente=1990"})
@Import(AssinaturaRecorrenteTest.StripeFalsa.class)
class AssinaturaRecorrenteTest {

    static final AtomicLong SEQ = new AtomicLong();

    /** Ultima chamada de cada operacao, para os testes conferirem. */
    static final List<String> CHAMADAS = new ArrayList<>();
    static Long ultimoInicioCobranca;
    static int ultimoValor;

    @TestConfiguration
    static class StripeFalsa {
        @Bean
        @Primary
        StripeAssinaturas stripeAssinaturasFalsa() {
            return new StripeAssinaturas() {
                @Override
                public StripeCliente.Sessao criarCheckoutAssinatura(long usuarioId, String email, String descricao, int valor,
                                                                    Long inicio, String sucesso, String cancelamento) {
                    ultimoInicioCobranca = inicio;
                    ultimoValor = valor;
                    CHAMADAS.add("checkout:" + usuarioId);
                    return new StripeCliente.Sessao("cs_sub_" + SEQ.incrementAndGet(), "https://checkout.stripe.test/sub");
                }

                @Override
                public void cancelarNoFimDoPeriodo(String assinaturaId, boolean cancelar) {
                    CHAMADAS.add((cancelar ? "cancelar:" : "reativar:") + assinaturaId);
                }

                @Override
                public void cancelarAgora(String assinaturaId) {
                    CHAMADAS.add("encerrar:" + assinaturaId);
                }
            };
        }
    }

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private AssinaturaRepository assinaturaRepository;

    private String email;
    private Long usuarioId;
    private String sub;

    @BeforeEach
    void novoUsuario() {
        CHAMADAS.clear();
        ultimoInicioCobranca = null;
        email = "assinante" + System.nanoTime() + "@teste.com";
        Usuario u = new Usuario();
        u.setNome("Assinante");
        u.setEmail(email);
        u.setSenhaHash("x");
        usuarioId = usuarioRepository.save(u).getId();
        sub = "sub_" + System.nanoTime();
    }

    private Usuario usuario() {
        return usuarioRepository.findByEmail(email).orElseThrow();
    }

    private ResultActions webhook(String id, String tipo, String objeto) throws Exception {
        String corpo = "{\"id\":\"" + id + "\",\"type\":\"" + tipo + "\",\"data\":{\"object\":" + objeto + "}}";
        long t = Instant.now().getEpochSecond();
        return mvc.perform(post("/pagamentos/webhook").contentType(MediaType.APPLICATION_JSON).content(corpo)
                .header("Stripe-Signature", AssinaturaWebhookTest.assinar(corpo, "whsec_teste", t)));
    }

    private static long epoch(LocalDateTime data) {
        return data.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    // Fatura no formato das versoes recentes da API (assinatura dentro de "parent").
    private String faturaNova(String faturaId, int valor, LocalDateTime fim, String intencao) {
        return "{\"id\":\"" + faturaId + "\",\"amount_paid\":" + valor + ",\"customer\":\"cus_1\",\"customer_email\":\"" + email + "\","
                + "\"parent\":{\"subscription_details\":{\"subscription\":\"" + sub + "\",\"metadata\":{\"usuario_id\":\"" + usuarioId + "\"}}},"
                + "\"lines\":{\"data\":[{\"period\":{\"start\":" + Instant.now().getEpochSecond() + ",\"end\":" + epoch(fim) + "}}]},"
                + "\"payments\":{\"data\":[{\"payment\":{\"payment_intent\":\"" + intencao + "\"}}]}}";
    }

    private void assinarEPagar(LocalDateTime fim) throws Exception {
        webhook("evt_s" + sub, "checkout.session.completed",
                "{\"id\":\"cs_x" + sub + "\",\"mode\":\"subscription\",\"subscription\":\"" + sub + "\",\"customer\":\"cus_1\","
                        + "\"client_reference_id\":\"" + usuarioId + "\",\"metadata\":{\"usuario_id\":\"" + usuarioId + "\"},"
                        + "\"amount_total\":1990,\"payment_status\":\"paid\"}").andExpect(status().isOk());
        webhook("evt_f" + sub, "invoice.paid", faturaNova("in_1" + sub, 1990, fim, "pi_1" + sub)).andExpect(status().isOk());
    }

    @Test
    void planosMostramAssinaturaMensalAtiva() throws Exception {
        mvc.perform(get("/planos"))
                .andExpect(jsonPath("$.recorrente.ativo").value(true))
                .andExpect(jsonPath("$.recorrente.precoCentavos").value(1990));
    }

    @Test
    void checkoutAbreAssinaturaECobraJaQuemNaoTemPro() throws Exception {
        mvc.perform(post("/pagamentos/checkout").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plano\":\"RECORRENTE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://checkout.stripe.test/sub"));
        assertThat(CHAMADAS).containsExactly("checkout:" + usuarioId);
        assertThat(ultimoInicioCobranca).isNull();
        assertThat(ultimoValor).isEqualTo(1990);
    }

    @Test
    void quemTemProPrePagoSoComecaAPagarQuandoEleAcabar() throws Exception {
        Usuario u = usuario();
        LocalDateTime proAte = LocalDateTime.now().plusDays(20).truncatedTo(ChronoUnit.SECONDS);
        u.setProAte(proAte);
        usuarioRepository.save(u);

        mvc.perform(post("/pagamentos/checkout").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"plano\":\"RECORRENTE\"}")).andExpect(status().isOk());
        assertThat(ultimoInicioCobranca).isEqualTo(epoch(proAte));
    }

    @Test
    void faturaPagaEstendeOProERegistraOPagamentoUmaVezSo() throws Exception {
        LocalDateTime fim = LocalDateTime.now().plusDays(30).truncatedTo(ChronoUnit.SECONDS);
        assinarEPagar(fim);

        assertThat(usuario().getProAte()).isCloseTo(fim.plusDays(1), within(1, ChronoUnit.SECONDS));
        Pagamento p = pagamentoRepository.findByStripeFaturaId("in_1" + sub).orElseThrow();
        assertThat(p.getPlano()).isEqualTo("RECORRENTE");
        assertThat(p.getStatus()).isEqualTo(Pagamento.PAGO);
        assertThat(p.getValorCentavos()).isEqualTo(1990);
        assertThat(p.getStripePagamentoId()).isEqualTo("pi_1" + sub);

        // A Stripe reenviou a mesma fatura em outro evento: nada muda.
        webhook("evt_f2" + sub, "invoice.paid", faturaNova("in_1" + sub, 1990, fim, "pi_1" + sub)).andExpect(status().isOk());
        assertThat(pagamentoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)).hasSize(1);

        mvc.perform(get("/pagamentos/assinatura").with(user(email)))
                .andExpect(jsonPath("$.ativa").value(true))
                .andExpect(jsonPath("$.status").value("active"))
                .andExpect(jsonPath("$.cancelarNoFim").value(false));
        mvc.perform(get("/perfil").with(user(email))).andExpect(jsonPath("$.pro").value(true));
    }

    @Test
    void renovacaoDoMesSeguinteEmpurraOFimDoPro() throws Exception {
        LocalDateTime fim1 = LocalDateTime.now().plusDays(30).truncatedTo(ChronoUnit.SECONDS);
        assinarEPagar(fim1);
        LocalDateTime fim2 = fim1.plusDays(30);
        webhook("evt_r" + sub, "invoice.paid", faturaNova("in_2" + sub, 1990, fim2, "pi_2" + sub)).andExpect(status().isOk());
        assertThat(usuario().getProAte()).isCloseTo(fim2.plusDays(1), within(1, ChronoUnit.SECONDS));
        assertThat(pagamentoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)).hasSize(2);
    }

    @Test
    void faturaNoFormatoAntigoDaApiTambemFunciona() throws Exception {
        LocalDateTime fim = LocalDateTime.now().plusDays(30).truncatedTo(ChronoUnit.SECONDS);
        String fatura = "{\"id\":\"in_v" + sub + "\",\"amount_paid\":1990,\"subscription\":\"" + sub + "\","
                + "\"payment_intent\":\"pi_v" + sub + "\",\"customer_email\":\"" + email + "\","
                + "\"subscription_details\":{\"metadata\":{\"usuario_id\":\"" + usuarioId + "\"}},"
                + "\"lines\":{\"data\":[{\"period\":{\"end\":" + epoch(fim) + "}}]}}";
        webhook("evt_v" + sub, "invoice.paid", fatura).andExpect(status().isOk());
        assertThat(usuario().getProAte()).isCloseTo(fim.plusDays(1), within(1, ChronoUnit.SECONDS));
        assertThat(pagamentoRepository.findFirstByStripePagamentoId("pi_v" + sub)).isPresent();
    }

    @Test
    void faturaZeradaDoPeriodoDeEsperaNaoGeraPagamento() throws Exception {
        LocalDateTime fim = LocalDateTime.now().plusDays(20).truncatedTo(ChronoUnit.SECONDS);
        webhook("evt_z" + sub, "invoice.paid", faturaNova("in_z" + sub, 0, fim, "pi_z" + sub)).andExpect(status().isOk());
        assertThat(pagamentoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)).isEmpty();
        assertThat(usuario().getProAte()).isNull();
        assertThat(assinaturaRepository.findByStripeAssinaturaId(sub).orElseThrow().getStatus()).isEqualTo("trialing");
    }

    @Test
    void naoDeixaAssinarDuasVezes() throws Exception {
        assinarEPagar(LocalDateTime.now().plusDays(30));
        mvc.perform(post("/pagamentos/checkout").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"plano\":\"RECORRENTE\"}")).andExpect(status().isConflict());
    }

    @Test
    void cancelarDesligaARenovacaoEMantemOAcessoAteOFim() throws Exception {
        LocalDateTime fim = LocalDateTime.now().plusDays(30).truncatedTo(ChronoUnit.SECONDS);
        assinarEPagar(fim);

        mvc.perform(post("/pagamentos/assinatura/cancelar").with(user(email)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cancelarNoFim").value(true));
        assertThat(CHAMADAS).contains("cancelar:" + sub);
        assertThat(usuario().getProAte()).isCloseTo(fim.plusDays(1), within(1, ChronoUnit.SECONDS));

        mvc.perform(post("/pagamentos/assinatura/reativar").with(user(email)))
                .andExpect(jsonPath("$.cancelarNoFim").value(false));
        assertThat(CHAMADAS).contains("reativar:" + sub);
    }

    @Test
    void cancelarSemAssinaturaDa404() throws Exception {
        mvc.perform(post("/pagamentos/assinatura/cancelar").with(user(email))).andExpect(status().isNotFound());
        mvc.perform(get("/pagamentos/assinatura").with(user(email))).andExpect(jsonPath("$.ativa").value(false));
    }

    @Test
    void falhaNaCobrancaMarcaPendenteEEncerramentoDesligaAAssinatura() throws Exception {
        LocalDateTime fim = LocalDateTime.now().plusDays(30).truncatedTo(ChronoUnit.SECONDS);
        assinarEPagar(fim);

        webhook("evt_pf" + sub, "invoice.payment_failed", faturaNova("in_f" + sub, 0, fim.plusDays(30), "pi_f" + sub));
        assertThat(assinaturaRepository.findByStripeAssinaturaId(sub).orElseThrow().getStatus()).isEqualTo("past_due");

        webhook("evt_del" + sub, "customer.subscription.deleted",
                "{\"id\":\"" + sub + "\",\"status\":\"canceled\",\"cancel_at_period_end\":false,\"metadata\":{\"usuario_id\":\"" + usuarioId + "\"}}")
                .andExpect(status().isOk());
        Assinatura a = assinaturaRepository.findByStripeAssinaturaId(sub).orElseThrow();
        assertThat(a.getStatus()).isEqualTo("canceled");
        assertThat(a.getEncerradaEm()).isNotNull();
        mvc.perform(get("/pagamentos/assinatura").with(user(email))).andExpect(jsonPath("$.ativa").value(false));
        // O que ja foi pago continua valendo.
        assertThat(usuario().getProAte()).isCloseTo(fim.plusDays(1), within(1, ChronoUnit.SECONDS));
    }

    @Test
    void atualizacaoDaAssinaturaEspelhaOCancelamentoFeitoNaStripe() throws Exception {
        assinarEPagar(LocalDateTime.now().plusDays(30));
        long fim = Instant.now().plusSeconds(30L * 86400).getEpochSecond();
        webhook("evt_up" + sub, "customer.subscription.updated",
                "{\"id\":\"" + sub + "\",\"status\":\"active\",\"cancel_at_period_end\":true,"
                        + "\"items\":{\"data\":[{\"current_period_end\":" + fim + "}]}}").andExpect(status().isOk());
        mvc.perform(get("/pagamentos/assinatura").with(user(email)))
                .andExpect(jsonPath("$.ativa").value(true))
                .andExpect(jsonPath("$.cancelarNoFim").value(true));
    }

    @Test
    void reembolsoDaCobrancaRecorrenteDevolveOsDias() throws Exception {
        LocalDateTime fim = LocalDateTime.now().plusDays(30).truncatedTo(ChronoUnit.SECONDS);
        assinarEPagar(fim);
        webhook("evt_rf" + sub, "charge.refunded", "{\"id\":\"ch_1\",\"payment_intent\":\"pi_1" + sub + "\"}")
                .andExpect(status().isOk());
        assertThat(pagamentoRepository.findByStripeFaturaId("in_1" + sub).orElseThrow().getStatus()).isEqualTo(Pagamento.REEMBOLSADO);
        assertThat(usuario().getProAte()).isBefore(LocalDateTime.now().plusDays(3));
        // Arrependimento: a assinatura e cancelada junto, sem nova cobranca.
        assertThat(CHAMADAS).contains("encerrar:" + sub);
        mvc.perform(get("/pagamentos/assinatura").with(user(email))).andExpect(jsonPath("$.ativa").value(false));
    }

    @Test
    void excluirAContaCancelaAAssinaturaNaStripe() throws Exception {
        assinarEPagar(LocalDateTime.now().plusDays(30));
        mvc.perform(delete("/admin/usuarios/" + usuarioId).with(user("admin@teste.com").roles("ADMIN")))
                .andExpect(status().is2xxSuccessful());

        assertThat(CHAMADAS).contains("encerrar:" + sub);
        assertThat(usuarioRepository.findById(usuarioId)).isEmpty();
        Assinatura a = assinaturaRepository.findByStripeAssinaturaId(sub).orElseThrow();
        assertThat(a.getStatus()).isEqualTo("canceled");
        assertThat(a.getUsuario()).isNull();
        // O pagamento fica (obrigacao fiscal), sem vinculo com a conta.
        assertThat(pagamentoRepository.findByStripeFaturaId("in_1" + sub)).isPresent();
    }
}

package com.gustavo.concursos.pro;

import com.fasterxml.jackson.databind.JsonNode;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Assinatura mensal com renovacao automatica (cartao), ao lado dos planos pre-pagos.
 *
 * O acesso Pro continua sendo controlado so por usuario.proAte: cada fatura paga
 * estende o Pro ate o fim do periodo cobrado (+1 dia de folga para a renovacao
 * seguinte ser processada). Cancelar desliga a renovacao; o acesso vai ate o fim
 * do periodo ja pago.
 *
 * Os eventos chegam no formato da versao da API configurada no webhook. Os campos
 * que mudaram de lugar nas versoes recentes (assinatura e metadados da fatura,
 * fim do periodo da assinatura) sao lidos nos dois formatos.
 */
@Service
public class AssinaturaService {

    private static final Logger log = LoggerFactory.getLogger(AssinaturaService.class);
    static final String PLANO = "RECORRENTE";
    /** A Stripe exige o fim do periodo de teste pelo menos 48 h no futuro. */
    private static final Duration ADIAMENTO_MINIMO = Duration.ofDays(2);
    private static final ZoneId FUSO = ZoneId.systemDefault();

    private final AssinaturaRepository assinaturaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final StripeAssinaturas stripe;
    private final boolean ativo;
    private final int precoCentavos;
    private final String urlBase;
    private final String nomeMarca;

    public AssinaturaService(
            AssinaturaRepository assinaturaRepository,
            PagamentoRepository pagamentoRepository,
            UsuarioRepository usuarioRepository,
            StripeAssinaturas stripe,
            @Value("${pro.recorrente.ativo:false}") boolean ativo,
            @Value("${pro.preco.recorrente:${pro.preco.mensal:2990}}") int precoCentavos,
            @Value("${app.url-base:http://localhost:8080}") String urlBase,
            @Value("${app.nome:Romano Concursos}") String nomeMarca
    ) {
        this.assinaturaRepository = assinaturaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.stripe = stripe;
        this.ativo = ativo;
        this.precoCentavos = precoCentavos;
        this.urlBase = urlBase.replaceAll("/+$", "");
        this.nomeMarca = nomeMarca;
    }

    public boolean ativo() {
        return ativo;
    }

    public int precoCentavos() {
        return precoCentavos;
    }

    public Optional<Assinatura> vigente(Long usuarioId) {
        return assinaturaRepository.findByUsuarioIdOrderByCriadaEmDesc(usuarioId).stream()
                .filter(Assinatura::vigente).findFirst();
    }

    // ------------------------------------------------------------------ pela pessoa

    @Transactional
    public String iniciarCheckout(Usuario usuario) {
        if (!ativo) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A assinatura mensal ainda não está disponível.");
        if (vigente(usuario.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já tem uma assinatura mensal ativa.");
        }
        // Quem ainda tem Pro pre-pago so comeca a pagar quando ele acabar.
        Long inicioCobranca = null;
        LocalDateTime proAte = usuario.getProAte();
        if (proAte != null && proAte.isAfter(LocalDateTime.now().plus(ADIAMENTO_MINIMO))) {
            inicioCobranca = proAte.atZone(FUSO).toEpochSecond();
        }
        return stripe.criarCheckoutAssinatura(usuario.getId(), usuario.getEmail(),
                nomeMarca + " — Pro Mensal (renovação automática)", precoCentavos, inicioCobranca,
                urlBase + "/?pagamento=sucesso", urlBase + "/?pagamento=cancelado").url();
    }

    @Transactional
    public Assinatura cancelarNoFim(Usuario usuario, boolean cancelar) {
        Assinatura a = vigente(usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Você não tem assinatura mensal ativa."));
        stripe.cancelarNoFimDoPeriodo(a.getStripeAssinaturaId(), cancelar);
        a.setCancelarNoFim(cancelar);
        return a;
    }

    /**
     * Na exclusao da conta: cancela na hora qualquer assinatura vigente, para a
     * pessoa nao continuar sendo cobrada. Se a Stripe falhar, a exclusao para.
     */
    @Transactional
    public void encerrarParaExclusao(Long usuarioId) {
        for (Assinatura a : assinaturaRepository.findByUsuarioIdOrderByCriadaEmDesc(usuarioId)) {
            if (a.vigente()) {
                stripe.cancelarAgora(a.getStripeAssinaturaId());
                a.setStatus("canceled");
                a.setEncerradaEm(LocalDateTime.now());
            }
            a.setUsuario(null);
        }
        // Grava ja: em seguida a exclusao apaga o usuario por SQL direto.
        assinaturaRepository.flush();
    }

    /**
     * Reembolso de uma cobranca recorrente (arrependimento): cancela a assinatura na
     * hora, para nao haver nova cobranca. Falha na Stripe so e registrada no log, para
     * nao travar o webhook; nesse caso, cancele pelo painel da Stripe.
     */
    public void encerrarPorReembolso(Usuario usuario) {
        if (usuario == null) return;
        vigente(usuario.getId()).ifPresent(a -> {
            try {
                stripe.cancelarAgora(a.getStripeAssinaturaId());
                a.setStatus("canceled");
                a.setEncerradaEm(LocalDateTime.now());
                assinaturaRepository.save(a);
            } catch (RuntimeException e) {
                log.error("Reembolso recebido, mas a assinatura {} nao foi cancelada na Stripe: {}",
                        a.getStripeAssinaturaId(), e.getMessage());
            }
        });
    }

    // ------------------------------------------------------------------ webhooks

    /** checkout.session.completed em modo assinatura: liga a assinatura a conta. */
    public void sessaoConcluida(JsonNode sessao) {
        String id = texto(sessao.path("subscription"));
        if (id == null) return;
        Assinatura a = buscarOuCriar(id, usuarioDe(sessao.path("metadata"), sessao.path("client_reference_id")),
                texto(sessao.path("customer_details").path("email")), texto(sessao.path("customer")));
        if (a.getValorCentavos() == 0) a.setValorCentavos(sessao.path("amount_total").asInt(precoCentavos));
        assinaturaRepository.save(a);
    }

    /** invoice.paid: cada fatura paga vira um pagamento e estende o Pro. */
    public void faturaPaga(JsonNode fatura) {
        String assinaturaId = assinaturaDaFatura(fatura);
        String faturaId = texto(fatura.path("id"));
        if (assinaturaId == null || faturaId == null) return;   // fatura avulsa: nao e da assinatura
        if (pagamentoRepository.findByStripeFaturaId(faturaId).isPresent()) return;

        Assinatura a = buscarOuCriar(assinaturaId, usuarioDe(metadadosDaFatura(fatura), null),
                texto(fatura.path("customer_email")), texto(fatura.path("customer")));
        LocalDateTime fim = fimDoPeriodo(fatura).orElse(LocalDateTime.now().plusDays(30));
        a.setPeriodoFim(fim);
        int valor = fatura.path("amount_paid").asInt(0);
        if (valor <= 0) {
            // Fatura zerada do periodo de espera (Pro pre-pago ainda valendo): so registra.
            if (a.getStatus().equals("incomplete")) a.setStatus("trialing");
            assinaturaRepository.save(a);
            return;
        }
        a.setStatus("active");
        a.setValorCentavos(valor);
        assinaturaRepository.save(a);

        Pagamento p = new Pagamento();
        p.setUsuario(a.getUsuario());
        p.setEmail(a.getEmail());
        p.setPlano(PLANO);
        p.setDias((int) Math.max(1, Duration.between(LocalDateTime.now(), fim).toDays()));
        p.setValorCentavos(valor);
        p.setStatus(Pagamento.PAGO);
        p.setPagoEm(LocalDateTime.now());
        p.setStripeFaturaId(faturaId);
        p.setStripePagamentoId(intencaoDaFatura(fatura));
        pagamentoRepository.save(p);

        Usuario u = a.getUsuario();
        if (u == null) return;
        LocalDateTime novoFim = fim.plusDays(1);
        if (u.getProAte() == null || u.getProAte().isBefore(novoFim)) {
            u.setProAte(novoFim);
            usuarioRepository.save(u);
        }
    }

    /** invoice.payment_failed: a Stripe tenta de novo; o Pro vai ate o fim do periodo ja pago. */
    public void faturaFalhou(JsonNode fatura) {
        String id = assinaturaDaFatura(fatura);
        if (id == null) return;
        assinaturaRepository.findByStripeAssinaturaId(id).ifPresent(a -> {
            a.setStatus("past_due");
            assinaturaRepository.save(a);
        });
    }

    /** customer.subscription.updated / deleted: espelha status, cancelamento e periodo. */
    public void assinaturaAtualizada(JsonNode assinatura, boolean apagada) {
        String id = texto(assinatura.path("id"));
        if (id == null) return;
        Assinatura a = buscarOuCriar(id, usuarioDe(assinatura.path("metadata"), null), null, texto(assinatura.path("customer")));
        a.setStatus(apagada ? "canceled" : assinatura.path("status").asText(a.getStatus()));
        a.setCancelarNoFim(assinatura.path("cancel_at_period_end").asBoolean(false));
        long fim = assinatura.path("current_period_end").asLong(0);
        if (fim == 0) fim = assinatura.path("items").path("data").path(0).path("current_period_end").asLong(0);
        if (fim > 0) a.setPeriodoFim(dataDe(fim));
        if (!Assinatura.VIGENTES.contains(a.getStatus()) && a.getEncerradaEm() == null) a.setEncerradaEm(LocalDateTime.now());
        assinaturaRepository.save(a);
    }

    // ------------------------------------------------------------------ auxiliares

    private Assinatura buscarOuCriar(String stripeId, Optional<Usuario> usuario, String email, String cliente) {
        Assinatura a = assinaturaRepository.findByStripeAssinaturaId(stripeId).orElseGet(() -> {
            Assinatura nova = new Assinatura();
            nova.setStripeAssinaturaId(stripeId);
            nova.setStatus("incomplete");
            nova.setValorCentavos(0);
            return nova;
        });
        if (a.getUsuario() == null) usuario.ifPresent(a::setUsuario);
        if (a.getEmail() == null || ("desconhecido".equals(a.getEmail()) && email != null)) {
            String e = email != null ? email : a.getUsuario() != null ? a.getUsuario().getEmail() : null;
            a.setEmail(e != null ? e : "desconhecido");
        }
        if (a.getStripeClienteId() == null) a.setStripeClienteId(cliente);
        if (a.getId() == null) log.info("Assinatura recorrente registrada: {}", stripeId);
        return a;
    }

    private Optional<Usuario> usuarioDe(JsonNode metadados, JsonNode referencia) {
        String id = texto(metadados.path("usuario_id"));
        if (id == null && referencia != null) id = texto(referencia);
        if (id == null) return Optional.empty();
        try {
            return usuarioRepository.findById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    // Versoes antigas: invoice.subscription. Recentes: invoice.parent.subscription_details.subscription.
    private static String assinaturaDaFatura(JsonNode fatura) {
        String id = texto(fatura.path("subscription"));
        return id != null ? id : texto(fatura.path("parent").path("subscription_details").path("subscription"));
    }

    private static JsonNode metadadosDaFatura(JsonNode fatura) {
        JsonNode antigos = fatura.path("subscription_details").path("metadata");
        return antigos.isMissingNode() ? fatura.path("parent").path("subscription_details").path("metadata") : antigos;
    }

    // Usado para casar o reembolso (charge.refunded traz o payment_intent).
    private static String intencaoDaFatura(JsonNode fatura) {
        String pi = texto(fatura.path("payment_intent"));
        if (pi != null) return pi;
        return texto(fatura.path("payments").path("data").path(0).path("payment").path("payment_intent"));
    }

    private static Optional<LocalDateTime> fimDoPeriodo(JsonNode fatura) {
        long fim = 0;
        for (JsonNode linha : fatura.path("lines").path("data")) fim = Math.max(fim, linha.path("period").path("end").asLong(0));
        if (fim == 0) fim = fatura.path("period_end").asLong(0);
        return fim > 0 ? Optional.of(dataDe(fim)) : Optional.empty();
    }

    private static LocalDateTime dataDe(long epoch) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), FUSO);
    }

    // Texto de um campo que pode vir como id ("sub_...") ou como objeto expandido.
    private static String texto(JsonNode no) {
        if (no == null || no.isMissingNode() || no.isNull()) return null;
        if (no.isObject()) return texto(no.path("id"));
        String s = no.asText("");
        return s.isBlank() ? null : s;
    }
}

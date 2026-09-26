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

import java.time.LocalDateTime;

@Service
public class PagamentoService {

    private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);

    private final PagamentoRepository pagamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final Planos planos;
    private final StripeCliente stripe;
    private final String urlBase;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            UsuarioRepository usuarioRepository,
            Planos planos,
            StripeCliente stripe,
            @Value("${app.url-base:http://localhost:8080}") String urlBase
    ) {
        this.pagamentoRepository = pagamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.planos = planos;
        this.stripe = stripe;
        this.urlBase = urlBase.replaceAll("/+$", "");
    }

    // Abre o checkout do Stripe. Os dias so sao liberados quando o webhook confirmar.
    @Transactional
    public String iniciarCheckout(Usuario usuario, String codigoPlano) {
        Planos.Plano plano = planos.buscar(codigoPlano)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plano inválido"));

        Pagamento p = new Pagamento();
        p.setUsuario(usuario);
        p.setEmail(usuario.getEmail());
        p.setPlano(plano.codigo());
        p.setDias(plano.dias());
        p.setValorCentavos(plano.precoCentavos());
        pagamentoRepository.save(p);

        StripeCliente.Sessao sessao = stripe.criarCheckout(p.getId(), usuario.getEmail(),
                "Concursos Platform — " + plano.nome() + " (" + plano.dias() + " dias)",
                plano.precoCentavos(),
                urlBase + "/?pagamento=sucesso", urlBase + "/?pagamento=cancelado");
        p.setStripeSessaoId(sessao.id());
        return sessao.url();
    }

    /**
     * Trata um evento do Stripe ja com assinatura verificada. Cada evento e
     * processado uma unica vez, mesmo que o Stripe o reenvie.
     */
    @Transactional
    public void processarEvento(JsonNode evento) {
        String id = evento.path("id").asText("");
        String tipo = evento.path("type").asText("");
        if (id.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento sem id");
        if (pagamentoRepository.contarEvento(id) > 0) return;
        pagamentoRepository.registrarEvento(id, tipo);

        JsonNode objeto = evento.path("data").path("object");
        switch (tipo) {
            // Cartao: ja vem pago. Boleto/Pix: vem "unpaid" e o pagamento chega depois.
            case "checkout.session.completed" -> sessao(objeto).ifPresent(p -> {
                p.setStripePagamentoId(objeto.path("payment_intent").asText(null));
                if ("paid".equals(objeto.path("payment_status").asText())) confirmar(p);
                else if (Pagamento.PENDENTE.equals(p.getStatus())) p.setStatus(Pagamento.AGUARDANDO);
            });
            case "checkout.session.async_payment_succeeded" -> sessao(objeto).ifPresent(p -> {
                if (p.getStripePagamentoId() == null) p.setStripePagamentoId(objeto.path("payment_intent").asText(null));
                confirmar(p);
            });
            case "checkout.session.async_payment_failed" -> sessao(objeto).ifPresent(p -> encerrar(p, Pagamento.FALHOU));
            case "checkout.session.expired" -> sessao(objeto).ifPresent(p -> encerrar(p, Pagamento.EXPIRADO));
            // Reembolso (ex.: direito de arrependimento em 7 dias): devolve os dias.
            case "charge.refunded" -> pagamentoRepository
                    .findFirstByStripePagamentoId(objeto.path("payment_intent").asText(""))
                    .ifPresent(this::reembolsar);
            default -> log.debug("Evento do Stripe ignorado: {}", tipo);
        }
    }

    private java.util.Optional<Pagamento> sessao(JsonNode objeto) {
        return pagamentoRepository.findByStripeSessaoId(objeto.path("id").asText(""));
    }

    // Soma os dias a partir do fim do Pro atual (ou de agora, se ja venceu).
    private void confirmar(Pagamento p) {
        if (Pagamento.PAGO.equals(p.getStatus()) || Pagamento.REEMBOLSADO.equals(p.getStatus())) return;
        p.setStatus(Pagamento.PAGO);
        p.setPagoEm(LocalDateTime.now());
        Usuario u = p.getUsuario();
        if (u == null) return;
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime base = u.getProAte() != null && u.getProAte().isAfter(agora) ? u.getProAte() : agora;
        u.setProAte(base.plusDays(p.getDias()));
        usuarioRepository.save(u);
    }

    private void encerrar(Pagamento p, String status) {
        if (!Pagamento.PAGO.equals(p.getStatus()) && !Pagamento.REEMBOLSADO.equals(p.getStatus())) p.setStatus(status);
    }

    private void reembolsar(Pagamento p) {
        if (!Pagamento.PAGO.equals(p.getStatus())) return;
        p.setStatus(Pagamento.REEMBOLSADO);
        p.setReembolsadoEm(LocalDateTime.now());
        Usuario u = p.getUsuario();
        if (u == null || u.getProAte() == null) return;
        LocalDateTime novo = u.getProAte().minusDays(p.getDias());
        u.setProAte(novo.isAfter(LocalDateTime.now()) ? novo : LocalDateTime.now());
        usuarioRepository.save(u);
    }
}

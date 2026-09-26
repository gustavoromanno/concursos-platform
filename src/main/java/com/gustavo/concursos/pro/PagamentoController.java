package com.gustavo.concursos.pro;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final PagamentoRepository pagamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final Planos planos;
    private final ObjectMapper json;
    private final String segredoWebhook;
    private final int simuladosGratis;

    public PagamentoController(
            PagamentoService pagamentoService,
            PagamentoRepository pagamentoRepository,
            UsuarioRepository usuarioRepository,
            Planos planos,
            ObjectMapper json,
            @Value("${stripe.segredo-webhook:}") String segredoWebhook,
            @Value("${pro.simulados-gratis-por-mes:3}") int simuladosGratis
    ) {
        this.pagamentoService = pagamentoService;
        this.pagamentoRepository = pagamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.planos = planos;
        this.json = json;
        this.segredoWebhook = segredoWebhook;
        this.simuladosGratis = simuladosGratis;
    }

    public record PlanoDTO(String codigo, String nome, int dias, int precoCentavos, int precoMensalCentavos) {
    }

    public record PlanosDTO(List<PlanoDTO> planos, int simuladosGratisPorMes) {
    }

    public record CheckoutDTO(@NotBlank String plano) {
    }

    public record PagamentoDTO(Long id, String plano, int dias, int valorCentavos, String status,
                               LocalDateTime criadoEm, LocalDateTime pagoEm) {
    }

    @GetMapping("/planos")
    public PlanosDTO planos() {
        return new PlanosDTO(planos.todos().stream()
                .map(p -> new PlanoDTO(p.codigo(), p.nome(), p.dias(), p.precoCentavos(), p.precoMensalEquivalente()))
                .toList(), simuladosGratis);
    }

    @PostMapping("/pagamentos/checkout")
    public Map<String, String> checkout(@Valid @RequestBody CheckoutDTO request, Authentication authentication) {
        return Map.of("url", pagamentoService.iniciarCheckout(usuarioLogado(authentication), request.plano()));
    }

    @Transactional(readOnly = true)
    @GetMapping("/pagamentos")
    public List<PagamentoDTO> meusPagamentos(Authentication authentication) {
        return pagamentoRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioLogado(authentication).getId()).stream()
                .map(p -> new PagamentoDTO(p.getId(), p.getPlano(), p.getDias(), p.getValorCentavos(),
                        p.getStatus(), p.getCriadoEm(), p.getPagoEm()))
                .toList();
    }

    // Chamado pelo Stripe (publico no SecurityConfig). So aceita com assinatura valida.
    @PostMapping("/pagamentos/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String corpo,
                                        @RequestHeader(value = "Stripe-Signature", required = false) String assinatura) {
        if (!AssinaturaWebhook.valida(corpo, assinatura, segredoWebhook, Instant.now().getEpochSecond())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assinatura inválida");
        }
        JsonNode evento;
        try {
            evento = json.readTree(corpo);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Evento inválido");
        }
        pagamentoService.processarEvento(evento);
        return ResponseEntity.ok().build();
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

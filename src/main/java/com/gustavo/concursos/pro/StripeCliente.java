package com.gustavo.concursos.pro;

// Cria a sessao de pagamento no Stripe. Interface para os testes usarem um
// Stripe falso (sem rede e sem custo).
public interface StripeCliente {

    record Sessao(String id, String url) {
    }

    Sessao criarCheckout(long pagamentoId, String email, String descricao, int valorCentavos,
                         String urlSucesso, String urlCancelamento);
}

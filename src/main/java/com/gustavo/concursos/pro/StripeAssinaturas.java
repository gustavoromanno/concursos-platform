package com.gustavo.concursos.pro;

// Operacoes de assinatura recorrente na Stripe. Interface separada do
// StripeCliente para os testes trocarem por uma Stripe falsa (sem rede).
public interface StripeAssinaturas {

    /**
     * Abre o Checkout em modo assinatura (cobranca mensal no cartao).
     * inicioCobrancaEpoch: quando a primeira cobranca acontece (fim do Pro pre-pago
     * atual), ou null para cobrar ja.
     */
    StripeCliente.Sessao criarCheckoutAssinatura(long usuarioId, String email, String descricao, int valorCentavos,
                                                 Long inicioCobrancaEpoch, String urlSucesso, String urlCancelamento);

    /** Liga ou desliga o cancelamento no fim do periodo ja pago. */
    void cancelarNoFimDoPeriodo(String assinaturaId, boolean cancelar);

    /** Cancela na hora, sem novas cobrancas (usado na exclusao da conta). */
    void cancelarAgora(String assinaturaId);
}

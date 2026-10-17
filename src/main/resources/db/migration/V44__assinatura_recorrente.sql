-- Assinatura mensal com renovacao automatica (cartao, Stripe Subscriptions).
-- Convive com os planos pre-pagos: cada fatura paga vira um registro em "pagamento"
-- (plano RECORRENTE) e estende o acesso Pro ate o fim do periodo cobrado.
CREATE TABLE assinatura (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES usuario(id),
    email VARCHAR(150) NOT NULL,
    stripe_assinatura_id VARCHAR(255) NOT NULL UNIQUE,
    stripe_cliente_id VARCHAR(255),
    status VARCHAR(30) NOT NULL,
    valor_centavos INT NOT NULL,
    periodo_fim TIMESTAMP,
    cancelar_no_fim BOOLEAN NOT NULL DEFAULT FALSE,
    criada_em TIMESTAMP NOT NULL DEFAULT now(),
    encerrada_em TIMESTAMP
);
CREATE INDEX idx_assinatura_usuario ON assinatura(usuario_id);

-- Fatura da Stripe que gerou o pagamento (so nas cobrancas recorrentes).
ALTER TABLE pagamento ADD COLUMN stripe_fatura_id VARCHAR(255);
CREATE UNIQUE INDEX idx_pagamento_fatura ON pagamento(stripe_fatura_id);

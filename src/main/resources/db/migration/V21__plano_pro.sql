-- Plano Pro pre-pago e perfil completo.
--
-- Plano Pro pre-pago: a pessoa compra 30, 90 ou 365 dias de acesso.
-- Nao ha cobranca recorrente; renovar e comprar de novo (os dias se somam).

ALTER TABLE usuario ADD COLUMN pro_ate TIMESTAMP;

CREATE TABLE pagamento (
    id BIGSERIAL PRIMARY KEY,
    -- SET NULL: o registro do pagamento (e da nota fiscal) sobrevive se a pessoa
    -- excluir a conta; o e-mail fica guardado para a contabilidade.
    usuario_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
    email VARCHAR(150) NOT NULL,
    plano VARCHAR(20) NOT NULL,          -- MENSAL, TRIMESTRAL, ANUAL
    dias INTEGER NOT NULL,
    valor_centavos INTEGER NOT NULL,
    -- PENDENTE (checkout aberto), AGUARDANDO (boleto/Pix gerado), PAGO,
    -- FALHOU, EXPIRADO, REEMBOLSADO
    status VARCHAR(20) NOT NULL,
    stripe_sessao_id VARCHAR(255) UNIQUE,
    stripe_pagamento_id VARCHAR(255),
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    pago_em TIMESTAMP,
    reembolsado_em TIMESTAMP
);

CREATE INDEX idx_pagamento_usuario ON pagamento(usuario_id, criado_em DESC);
CREATE INDEX idx_pagamento_stripe_pagamento ON pagamento(stripe_pagamento_id);

-- Eventos do Stripe ja processados: o Stripe pode reenviar o mesmo evento,
-- e o mesmo pagamento nao pode liberar dias duas vezes.
CREATE TABLE stripe_evento (
    id VARCHAR(255) PRIMARY KEY,
    tipo VARCHAR(100) NOT NULL,
    recebido_em TIMESTAMP NOT NULL DEFAULT now()
);

-- Perfil: dados opcionais que a pessoa preenche em "Minha conta".
ALTER TABLE usuario ADD COLUMN uf VARCHAR(2);
ALTER TABLE usuario ADD COLUMN carreira_alvo VARCHAR(60);
ALTER TABLE usuario ADD COLUMN bio VARCHAR(500);
-- Foto pequena (ate ~300 KB) como data URL. Simples e sem servico de arquivos.
ALTER TABLE usuario ADD COLUMN foto TEXT;

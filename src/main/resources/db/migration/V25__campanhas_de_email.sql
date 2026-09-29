-- E-mail promocional: campanhas enviadas pelo admin SO para quem aceitou.
-- Cada pessoa tem um codigo de descadastro (link "nao quero mais receber"),
-- que funciona sem login, como a LGPD e as boas praticas de e-mail exigem.
ALTER TABLE usuario ADD COLUMN token_descadastro VARCHAR(64) UNIQUE;

CREATE TABLE campanha_email (
    id BIGSERIAL PRIMARY KEY,
    assunto VARCHAR(150) NOT NULL,
    mensagem TEXT NOT NULL,
    destinatarios INTEGER NOT NULL DEFAULT 0,
    enviados INTEGER NOT NULL DEFAULT 0,
    -- ENVIANDO, CONCLUIDA
    status VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);

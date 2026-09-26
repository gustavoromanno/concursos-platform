-- "Esqueci minha senha": link de uso unico enviado por e-mail.
-- Guardamos so o HASH do token: quem ler o banco nao consegue usar o link.
CREATE TABLE token_redefinicao (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expira_em TIMESTAMP NOT NULL,
    usado_em TIMESTAMP,
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_token_redefinicao_usuario ON token_redefinicao(usuario_id, criado_em DESC);

-- Confirmacao de e-mail no cadastro.
-- Contas que ja existiam ficam confirmadas: ninguem perde o acesso.
ALTER TABLE usuario ADD COLUMN email_confirmado BOOLEAN NOT NULL DEFAULT FALSE;
UPDATE usuario SET email_confirmado = TRUE;

CREATE TABLE token_confirmacao (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expira_em TIMESTAMP NOT NULL,
    usado_em TIMESTAMP,
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_token_confirmacao_usuario ON token_confirmacao(usuario_id, criado_em DESC);

-- A conta de administrador passa a usar o e-mail real de contato (precisa
-- receber confirmacoes e redefinicao de senha). So troca se ainda nao existir
-- outra conta com esse e-mail.
UPDATE usuario SET email = 'contatoromanno@gmail.com'
WHERE email = 'gustavo@teste.com' AND papel = 'ADMIN'
  AND NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'contatoromanno@gmail.com');

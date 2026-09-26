-- Cadastro: e-mail padronizado e consentimento para comunicacoes promocionais.

-- 1) E-mails em minusculas e sem espacos. Antes, "Fulano@Gmail.com" e
--    "fulano@gmail.com" contavam como contas diferentes. So padroniza quando
--    isso nao colide com outra conta (se colidir, as duas continuam como estao).
UPDATE usuario u
SET email = lower(trim(u.email))
WHERE u.email <> lower(trim(u.email))
  AND NOT EXISTS (
      SELECT 1 FROM usuario o
      WHERE o.id <> u.id AND lower(trim(o.email)) = lower(trim(u.email))
  );

-- 2) Consentimento (LGPD): e-mail promocional so para quem marcou que aceita.
--    Comeca desmarcado para todos; cada pessoa decide no cadastro ou em "Minha conta".
ALTER TABLE usuario ADD COLUMN aceita_marketing BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE usuario ADD COLUMN marketing_atualizado_em TIMESTAMP;

-- 3) Data de cadastro. Contas ja existentes recebem a data desta migration.
ALTER TABLE usuario ADD COLUMN criado_em TIMESTAMP NOT NULL DEFAULT now();

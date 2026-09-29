-- Lembrete diario por e-mail das revisoes do dia (opt-in, recurso Pro).
-- Separado do consentimento de marketing: e aviso de estudo, nao promocao.
ALTER TABLE usuario ADD COLUMN lembrete_revisao BOOLEAN NOT NULL DEFAULT FALSE;

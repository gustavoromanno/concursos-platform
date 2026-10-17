-- Teoria por assunto: um resumo autoral por assunto, ligado as questoes do mesmo assunto.
-- "gratuito" libera o resumo para o plano gratuito; os demais sao do plano Pro.
CREATE TABLE resumo_assunto (
    id BIGSERIAL PRIMARY KEY,
    assunto_id BIGINT NOT NULL UNIQUE REFERENCES assunto(id) ON DELETE CASCADE,
    titulo VARCHAR(200) NOT NULL,
    conteudo TEXT NOT NULL,
    gratuito BOOLEAN NOT NULL DEFAULT FALSE,
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()
);

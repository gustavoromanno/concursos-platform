-- Revisao espacada: agenda QUANDO cada questao deve voltar.
--
-- O algoritmo (SM-2 simplificado) guarda tres coisas por questao:
--   intervalo_dias -> quantos dias ate a proxima revisao
--   facilidade     -> o quanto a questao e "facil" para este usuario (1.3 a 2.8)
--   repeticoes     -> acertos seguidos
-- Acertar aumenta o intervalo; errar zera e traz a questao de volta amanha.

CREATE TABLE revisao (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    questao_id BIGINT NOT NULL REFERENCES questao(id) ON DELETE CASCADE,
    proxima_revisao DATE NOT NULL,
    intervalo_dias INTEGER NOT NULL DEFAULT 1,
    facilidade NUMERIC(4,2) NOT NULL DEFAULT 2.5,
    repeticoes INTEGER NOT NULL DEFAULT 0,
    atualizado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_revisao UNIQUE (usuario_id, questao_id)
);

CREATE INDEX idx_revisao_agenda ON revisao(usuario_id, proxima_revisao);

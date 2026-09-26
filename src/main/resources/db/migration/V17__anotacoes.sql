-- Anotacoes pessoais: uma nota privada por usuario em cada questao.
-- So o autor ve a propria anotacao; nao aparece para a comunidade.

CREATE TABLE anotacao (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    questao_id BIGINT NOT NULL REFERENCES questao(id) ON DELETE CASCADE,
    texto TEXT NOT NULL,
    atualizado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uk_anotacao_usuario_questao UNIQUE (usuario_id, questao_id)
);

CREATE INDEX idx_anotacao_usuario ON anotacao(usuario_id);

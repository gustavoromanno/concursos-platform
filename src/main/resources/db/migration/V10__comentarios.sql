-- Comentarios dos usuarios nas questoes.
-- As estatisticas coletivas (quantos acertaram, qual alternativa mais marcada)
-- NAO viram tabela: sao derivadas da tabela resposta, que ja tem tudo.

CREATE TABLE comentario (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    questao_id BIGINT NOT NULL REFERENCES questao(id) ON DELETE CASCADE,
    texto TEXT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_comentario_questao ON comentario(questao_id, criado_em DESC);

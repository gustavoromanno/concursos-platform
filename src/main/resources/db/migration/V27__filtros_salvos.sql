-- Filtros de busca que o aluno guarda para reusar ("Bacen - Economia - CESPE").
CREATE TABLE filtro_salvo (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    nome VARCHAR(60) NOT NULL,
    -- query string da busca, ex.: disciplinaId=3&bancaId=1&tipo=CERTO_ERRADO
    parametros VARCHAR(2000) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_filtro_salvo_usuario ON filtro_salvo(usuario_id, criado_em);

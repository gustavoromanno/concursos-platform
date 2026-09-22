-- Sprint 5: simulado cronometrado.
-- Um simulado e um conjunto fixo de questoes sorteadas, com prazo para responder.

CREATE TABLE simulado (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    duracao_minutos INTEGER NOT NULL,
    finalizado_em TIMESTAMP
);

-- Guarda a ordem das questoes e, quando respondida, qual resposta foi dada.
-- resposta_id aponta para a tabela resposta, entao o que e respondido no
-- simulado alimenta as mesmas estatisticas do dashboard.
CREATE TABLE simulado_questao (
    id BIGSERIAL PRIMARY KEY,
    simulado_id BIGINT NOT NULL REFERENCES simulado(id) ON DELETE CASCADE,
    questao_id BIGINT NOT NULL REFERENCES questao(id),
    ordem INTEGER NOT NULL,
    resposta_id BIGINT REFERENCES resposta(id)
);

CREATE INDEX idx_simulado_usuario ON simulado(usuario_id);
CREATE INDEX idx_simulado_questao_simulado ON simulado_questao(simulado_id);

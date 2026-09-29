-- "Reportar erro": o aluno avisa sobre gabarito errado, enunciado confuso etc.
CREATE TABLE reporte_questao (
    id BIGSERIAL PRIMARY KEY,
    questao_id BIGINT NOT NULL REFERENCES questao(id) ON DELETE CASCADE,
    usuario_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
    -- GABARITO, ENUNCIADO, DESATUALIZADA, OUTRO
    motivo VARCHAR(20) NOT NULL,
    descricao VARCHAR(1000),
    -- NOVO, RESOLVIDO, DESCARTADO
    status VARCHAR(20) NOT NULL DEFAULT 'NOVO',
    resposta VARCHAR(500),
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_reporte_status ON reporte_questao(status, criado_em);
CREATE INDEX idx_reporte_questao ON reporte_questao(questao_id);

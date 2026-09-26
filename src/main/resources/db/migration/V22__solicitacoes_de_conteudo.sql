-- Pedidos de conteudo: "nao achei a materia/edital que eu estudo".
-- Viram fila para o admin priorizar novas importacoes e lotes de questoes.
CREATE TABLE solicitacao_conteudo (
    id BIGSERIAL PRIMARY KEY,
    -- SET NULL: se a pessoa excluir a conta, o pedido (anonimo) continua util.
    usuario_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
    concurso VARCHAR(150) NOT NULL,
    cargo VARCHAR(150),
    materia VARCHAR(150) NOT NULL,
    link_edital VARCHAR(500),
    detalhes VARCHAR(1000),
    -- NOVA, EM_ANALISE, ATENDIDA, RECUSADA
    status VARCHAR(20) NOT NULL DEFAULT 'NOVA',
    resposta VARCHAR(500),
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_solicitacao_usuario ON solicitacao_conteudo(usuario_id, criado_em DESC);
CREATE INDEX idx_solicitacao_status ON solicitacao_conteudo(status, criado_em);

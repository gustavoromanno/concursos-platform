-- Sprint 9: engajamento.
--
-- A meta diaria e o unico dado novo a persistir. Ofensiva (streak) e mapa de
-- estudo sao DERIVADOS da tabela resposta, que ja registra data e hora de cada
-- resposta. Guardar streak em coluna seria redundante e abriria espaco para o
-- numero divergir do historico real.

CREATE TABLE meta_estudo (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    questoes_por_dia INTEGER NOT NULL DEFAULT 10,
    atualizado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_meta_usuario UNIQUE (usuario_id)
);

-- Sprint 8: organizacao pessoal do usuario.
--
-- Caderno = lista tematica montada pelo usuario ("Revisar antes da prova").
-- Marcador = favorito simples, sem precisar escolher caderno.

CREATE TABLE caderno (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    nome VARCHAR(120) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE caderno_questao (
    id BIGSERIAL PRIMARY KEY,
    caderno_id BIGINT NOT NULL REFERENCES caderno(id) ON DELETE CASCADE,
    questao_id BIGINT NOT NULL REFERENCES questao(id) ON DELETE CASCADE,
    adicionado_em TIMESTAMP NOT NULL DEFAULT now(),
    -- A mesma questao nao entra duas vezes no mesmo caderno.
    CONSTRAINT uq_caderno_questao UNIQUE (caderno_id, questao_id)
);

CREATE TABLE marcador (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    questao_id BIGINT NOT NULL REFERENCES questao(id) ON DELETE CASCADE,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_marcador UNIQUE (usuario_id, questao_id)
);

CREATE INDEX idx_caderno_usuario ON caderno(usuario_id);
CREATE INDEX idx_caderno_questao_caderno ON caderno_questao(caderno_id);
CREATE INDEX idx_marcador_usuario ON marcador(usuario_id);

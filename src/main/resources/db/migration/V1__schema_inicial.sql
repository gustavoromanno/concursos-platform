CREATE TABLE disciplina (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE banca (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE questao (
    id BIGSERIAL PRIMARY KEY,
    enunciado TEXT NOT NULL,
    disciplina_id BIGINT NOT NULL REFERENCES disciplina(id),
    banca_id BIGINT NOT NULL REFERENCES banca(id),
    ano INTEGER NOT NULL,
    assunto VARCHAR(150),
    explicacao TEXT
);

CREATE TABLE alternativa (
    id BIGSERIAL PRIMARY KEY,
    questao_id BIGINT NOT NULL REFERENCES questao(id) ON DELETE CASCADE,
    texto TEXT NOT NULL,
    correta BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha_hash VARCHAR(255) NOT NULL
);

CREATE TABLE resposta (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    questao_id BIGINT NOT NULL REFERENCES questao(id),
    alternativa_escolhida_id BIGINT NOT NULL REFERENCES alternativa(id),
    correta BOOLEAN NOT NULL,
    respondida_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_questao_disciplina ON questao(disciplina_id);
CREATE INDEX idx_questao_banca ON questao(banca_id);
CREATE INDEX idx_resposta_usuario ON resposta(usuario_id);

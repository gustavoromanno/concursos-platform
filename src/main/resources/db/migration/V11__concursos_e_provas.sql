-- Catalogo de concursos e provas.
--
-- Concurso -> uma ou mais provas (uma por cargo) -> questoes daquela prova.
-- prova_questao guarda a NUMERACAO original, para a prova poder ser resolvida
-- na mesma ordem em que caiu.

CREATE TABLE concurso (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    orgao VARCHAR(150),
    banca_id BIGINT REFERENCES banca(id),
    ano INTEGER NOT NULL,
    -- PREVISTO, INSCRICOES_ABERTAS, EM_ANDAMENTO, ENCERRADO
    situacao VARCHAR(30) NOT NULL DEFAULT 'PREVISTO',
    vagas INTEGER,
    inscricoes_ate DATE,
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE prova (
    id BIGSERIAL PRIMARY KEY,
    concurso_id BIGINT NOT NULL REFERENCES concurso(id) ON DELETE CASCADE,
    cargo VARCHAR(150) NOT NULL,
    nivel VARCHAR(30),
    aplicada_em DATE
);

CREATE TABLE prova_questao (
    id BIGSERIAL PRIMARY KEY,
    prova_id BIGINT NOT NULL REFERENCES prova(id) ON DELETE CASCADE,
    questao_id BIGINT NOT NULL REFERENCES questao(id) ON DELETE CASCADE,
    numero INTEGER NOT NULL,
    CONSTRAINT uq_prova_questao UNIQUE (prova_id, questao_id)
);

CREATE INDEX idx_prova_concurso ON prova(concurso_id);
CREATE INDEX idx_prova_questao_prova ON prova_questao(prova_id, numero);

-- Uma prova de exemplo, montada com as questoes que ja existem no banco,
-- so para a tela nao nascer vazia. Os dados do concurso sao ficticios.
INSERT INTO concurso (nome, orgao, banca_id, ano, situacao, vagas)
SELECT 'Concurso de Exemplo — Analista Administrativo',
       'Orgao Modelo',
       b.id,
       2026,
       'PREVISTO',
       20
FROM banca b WHERE b.nome = 'CESPE/CEBRASPE' LIMIT 1;

INSERT INTO prova (concurso_id, cargo, nivel, aplicada_em)
SELECT c.id, 'Analista Administrativo', 'Superior', CURRENT_DATE
FROM concurso c WHERE c.nome = 'Concurso de Exemplo — Analista Administrativo';

-- Vincula ate 20 questoes, numeradas na ordem.
INSERT INTO prova_questao (prova_id, questao_id, numero)
SELECT p.id, q.id, ROW_NUMBER() OVER (ORDER BY q.id)
FROM prova p
CROSS JOIN (SELECT id FROM questao ORDER BY id LIMIT 20) q
WHERE p.cargo = 'Analista Administrativo';

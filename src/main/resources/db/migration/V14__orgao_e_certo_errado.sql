-- Duas lacunas do modelo:
--   1. Orgao era texto solto no concurso; vira entidade e passa a ligar-se
--      tambem a questao, permitindo filtrar e medir desempenho por orgao.
--   2. Questoes Certo/Errado, formato que o CESPE cobra bastante e que o
--      modelo so de multipla escolha nao atendia.

CREATE TABLE orgao (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL UNIQUE,
    sigla VARCHAR(30),
    esfera VARCHAR(20)   -- FEDERAL, ESTADUAL, MUNICIPAL
);

ALTER TABLE questao ADD COLUMN orgao_id BIGINT REFERENCES orgao(id);
ALTER TABLE concurso ADD COLUMN orgao_id BIGINT REFERENCES orgao(id);
CREATE INDEX idx_questao_orgao ON questao(orgao_id);

-- Migra os orgaos que ja estao escritos como texto nos concursos.
INSERT INTO orgao (nome)
SELECT DISTINCT TRIM(c.orgao)
FROM concurso c
WHERE c.orgao IS NOT NULL AND TRIM(c.orgao) <> '';

UPDATE concurso c
SET orgao_id = o.id
FROM orgao o
WHERE TRIM(c.orgao) = o.nome;

-- Tipo da questao. As existentes sao todas de multipla escolha.
ALTER TABLE questao ADD COLUMN tipo VARCHAR(20) NOT NULL DEFAULT 'MULTIPLA_ESCOLHA';

ALTER TABLE questao ADD CONSTRAINT ck_questao_tipo
    CHECK (tipo IN ('MULTIPLA_ESCOLHA', 'CERTO_ERRADO'));

-- Em Certo/Errado a questao continua tendo duas alternativas na tabela
-- alternativa ("Certo" e "Errado"), uma delas marcada como correta. Assim
-- todo o resto do sistema — respostas, estatisticas, simulados, revisao
-- espacada — continua funcionando sem nenhuma alteracao.

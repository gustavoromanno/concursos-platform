-- Sprint 7: assunto deixa de ser texto solto na questao e vira entidade propria,
-- com hierarquia (assunto pai -> subassunto). Isso e o que permite agrupar o
-- desempenho por topico e sugerir videoaula do assunto certo.

CREATE TABLE assunto (
    id BIGSERIAL PRIMARY KEY,
    disciplina_id BIGINT NOT NULL REFERENCES disciplina(id),
    nome VARCHAR(150) NOT NULL,
    -- Auto-relacionamento: nulo = assunto de primeiro nivel.
    pai_id BIGINT REFERENCES assunto(id)
);

CREATE INDEX idx_assunto_disciplina ON assunto(disciplina_id);
CREATE INDEX idx_assunto_pai ON assunto(pai_id);

-- A coluna de texto antiga continua existindo (nao quebra nada que ja funciona),
-- mas agora a questao tambem aponta para o assunto estruturado.
ALTER TABLE questao ADD COLUMN assunto_id BIGINT REFERENCES assunto(id);
CREATE INDEX idx_questao_assunto ON questao(assunto_id);

-- Migra o que ja existe: cria um assunto para cada texto distinto e religa as questoes.
INSERT INTO assunto (disciplina_id, nome)
SELECT DISTINCT q.disciplina_id, q.assunto
FROM questao q
WHERE q.assunto IS NOT NULL AND q.assunto <> '';

UPDATE questao q
SET assunto_id = a.id
FROM assunto a
WHERE a.nome = q.assunto AND a.disciplina_id = q.disciplina_id;

-- Videoaulas vinculadas a disciplina e, opcionalmente, a um assunto especifico.
-- Guardamos so o ID do YouTube (o trecho depois de "v="), nao a URL inteira:
-- e o que o player de embed precisa.
CREATE TABLE videoaula (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    youtube_id VARCHAR(20) NOT NULL,
    canal VARCHAR(120),
    duracao_minutos INTEGER,
    disciplina_id BIGINT NOT NULL REFERENCES disciplina(id),
    assunto_id BIGINT REFERENCES assunto(id),
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_videoaula_disciplina ON videoaula(disciplina_id);
CREATE INDEX idx_videoaula_assunto ON videoaula(assunto_id);

-- Detalhamento do concurso + objetivo de estudo do usuario.
--
-- Progresso e os contadores (dominado/atencao/revisar/vai cair) NAO viram
-- coluna: sao derivados do historico de respostas cruzado com o conteudo
-- programatico do cargo. Guardar esses numeros abriria espaco para divergirem
-- do que realmente aconteceu.

ALTER TABLE concurso ADD COLUMN inscricoes_de DATE;
ALTER TABLE concurso ADD COLUMN taxa NUMERIC(10,2);
ALTER TABLE concurso ADD COLUMN edital_url VARCHAR(400);
ALTER TABLE concurso ADD COLUMN observacoes TEXT;

-- Cronograma: inscricoes, prova objetiva, resultado, etc.
CREATE TABLE concurso_etapa (
    id BIGSERIAL PRIMARY KEY,
    concurso_id BIGINT NOT NULL REFERENCES concurso(id) ON DELETE CASCADE,
    nome VARCHAR(120) NOT NULL,
    data_prevista DATE,
    -- CONCLUIDO, EM_ANDAMENTO, PREVISTO
    status VARCHAR(20) NOT NULL DEFAULT 'PREVISTO',
    ordem INTEGER NOT NULL DEFAULT 1
);

-- Cargos oferecidos, com vagas e remuneracao.
CREATE TABLE concurso_cargo (
    id BIGSERIAL PRIMARY KEY,
    concurso_id BIGINT NOT NULL REFERENCES concurso(id) ON DELETE CASCADE,
    nome VARCHAR(150) NOT NULL,
    nivel VARCHAR(40),
    vagas INTEGER,
    cadastro_reserva INTEGER,
    salario NUMERIC(12,2),
    ordem INTEGER NOT NULL DEFAULT 1
);

-- Conteudo programatico: quais disciplinas caem para cada cargo.
CREATE TABLE cargo_disciplina (
    id BIGSERIAL PRIMARY KEY,
    cargo_id BIGINT NOT NULL REFERENCES concurso_cargo(id) ON DELETE CASCADE,
    disciplina_id BIGINT NOT NULL REFERENCES disciplina(id),
    total_topicos INTEGER,
    ordem INTEGER NOT NULL DEFAULT 1,
    CONSTRAINT uq_cargo_disciplina UNIQUE (cargo_id, disciplina_id)
);

-- O concurso/cargo que o usuario escolheu como alvo. Um por usuario.
CREATE TABLE objetivo_usuario (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    concurso_id BIGINT NOT NULL REFERENCES concurso(id) ON DELETE CASCADE,
    cargo_id BIGINT REFERENCES concurso_cargo(id) ON DELETE SET NULL,
    definido_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uq_objetivo_usuario UNIQUE (usuario_id)
);

CREATE INDEX idx_etapa_concurso ON concurso_etapa(concurso_id, ordem);
CREATE INDEX idx_cargo_concurso ON concurso_cargo(concurso_id, ordem);

-- Cronograma e cargo de exemplo para o concurso que ja existe.
INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Edital publicado', CURRENT_DATE - 30, 'CONCLUIDO', 1),
    ('Inscrições', CURRENT_DATE - 10, 'EM_ANDAMENTO', 2),
    ('Prova objetiva', CURRENT_DATE + 45, 'PREVISTO', 3),
    ('Resultado preliminar', CURRENT_DATE + 70, 'PREVISTO', 4),
    ('Resultado final', CURRENT_DATE + 95, 'PREVISTO', 5)
) AS v(nome, data, status, ordem)
WHERE c.nome = 'Concurso de Exemplo — Analista Administrativo';

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, 'Analista Administrativo', 'Superior', 20, 100, 6500.00, 1
FROM concurso c WHERE c.nome = 'Concurso de Exemplo — Analista Administrativo';

-- Conteudo programatico do cargo: todas as disciplinas que ja temos.
INSERT INTO cargo_disciplina (cargo_id, disciplina_id, total_topicos, ordem)
SELECT cc.id, d.id, (SELECT COUNT(*) FROM assunto a WHERE a.disciplina_id = d.id),
       ROW_NUMBER() OVER (ORDER BY d.nome)
FROM concurso_cargo cc
CROSS JOIN disciplina d
WHERE cc.nome = 'Analista Administrativo';

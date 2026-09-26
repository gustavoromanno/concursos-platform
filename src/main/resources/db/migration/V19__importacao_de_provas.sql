-- Importacao de provas antigas como BASE para gerar questoes ineditas.
--
-- As questoes originais extraidas das provas ficam em questao_base e NUNCA
-- sao publicadas: servem so de referencia de assunto e estilo da banca (o
-- texto das provas e protegido por direito autoral). O que vai para o site
-- sao as questoes ineditas, primeiro como rascunho, depois de aprovadas.

-- Questoes publicadas passam a poder apontar para o concurso/cargo de origem.
ALTER TABLE questao ADD COLUMN concurso_id BIGINT REFERENCES concurso(id) ON DELETE SET NULL;
ALTER TABLE questao ADD COLUMN cargo_id BIGINT REFERENCES concurso_cargo(id) ON DELETE SET NULL;
-- AUTORAL (escritas a mao / lotes do script) ou IA (geradas a partir de provas).
ALTER TABLE questao ADD COLUMN origem VARCHAR(20) NOT NULL DEFAULT 'AUTORAL';
CREATE INDEX idx_questao_concurso_cargo ON questao(concurso_id, cargo_id);

CREATE TABLE importacao_prova (
    id BIGSERIAL PRIMARY KEY,
    -- AGUARDANDO, EXTRAINDO, GERANDO, CONCLUIDA, ERRO
    status VARCHAR(20) NOT NULL,
    etapa VARCHAR(200),
    progresso INTEGER NOT NULL DEFAULT 0,
    concurso_id BIGINT REFERENCES concurso(id) ON DELETE SET NULL,
    cargo_id BIGINT REFERENCES concurso_cargo(id) ON DELETE SET NULL,
    cargo_informado VARCHAR(150),
    ineditas_por_questao INTEGER NOT NULL DEFAULT 1,
    nome_arquivo_prova VARCHAR(255),
    nome_arquivo_gabarito VARCHAR(255),
    tokens_entrada BIGINT NOT NULL DEFAULT 0,
    tokens_saida BIGINT NOT NULL DEFAULT 0,
    mensagem_erro TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP NOT NULL DEFAULT now()
);

-- Os PDFs ficam em tabela propria (listar importacoes nao carrega megabytes)
-- e so ate o processamento terminar com sucesso; ate la permitem reprocessar.
CREATE TABLE importacao_arquivo (
    importacao_id BIGINT PRIMARY KEY REFERENCES importacao_prova(id) ON DELETE CASCADE,
    pdf_prova BYTEA NOT NULL,
    pdf_gabarito BYTEA NOT NULL
);

-- Questoes originais da prova. PRIVADAS: nenhum endpoint publico le esta tabela.
CREATE TABLE questao_base (
    id BIGSERIAL PRIMARY KEY,
    importacao_id BIGINT NOT NULL REFERENCES importacao_prova(id) ON DELETE CASCADE,
    numero INTEGER NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    disciplina VARCHAR(100),
    assunto VARCHAR(150),
    enunciado TEXT NOT NULL,
    -- JSON com a lista de textos das alternativas; vazio em Certo/Errado.
    alternativas TEXT,
    -- Letra (A..E), C/E, ou nulo quando anulada.
    gabarito VARCHAR(10),
    anulada BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_questao_base_importacao ON questao_base(importacao_id, numero);

-- Questoes ineditas geradas pela IA, aguardando revisao.
CREATE TABLE questao_rascunho (
    id BIGSERIAL PRIMARY KEY,
    importacao_id BIGINT NOT NULL REFERENCES importacao_prova(id) ON DELETE CASCADE,
    questao_base_id BIGINT REFERENCES questao_base(id) ON DELETE SET NULL,
    tipo VARCHAR(20) NOT NULL,
    disciplina VARCHAR(100) NOT NULL,
    assunto VARCHAR(150),
    enunciado TEXT NOT NULL,
    -- JSON: [{"texto": "...", "correta": true}, ...]
    alternativas TEXT NOT NULL,
    explicacao TEXT,
    -- PENDENTE, APROVADA, DESCARTADA
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    questao_id BIGINT REFERENCES questao(id) ON DELETE SET NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_rascunho_importacao ON questao_rascunho(importacao_id, status);

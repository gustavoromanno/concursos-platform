-- Catalogo de concursos, lote 2: Bacen (2013 e 2024) e cinco concursos estaduais de 2026.
--
-- Fonte de cada numero: o edital de abertura (link em concurso.edital_url) e, quando
-- houve, as retificacoes. O que veio de outra fonte esta marcado nas observacoes como
-- "nao conferido no edital" e nao vira coluna nem etapa.
--
-- Decisoes (as da V31 continuam valendo):
--   * Nome no padrao "Sigla Ano", o mesmo que a importacao de provas procura.
--   * Bacen 2013 e Bacen 2024 podem ja existir em producao, criados pela importacao
--     (sem edital_url, orgao_id nem observacoes). Por isso:
--       - o INSERT do concurso continua com WHERE NOT EXISTS;
--       - um UPDATE preenche so as colunas que estao NULL (COALESCE), sem sobrescrever nada;
--       - cargos e etapas entram com NOT EXISTS por NOME, nao "se o concurso nao tem cargos",
--         para conviver com os cargos que a importacao ja criou.
--   * Concurso localizado por LOWER(nome) + ano, igual a importacao (IgnoreCase).
--   * cargo_disciplina so recebe disciplinas de verdade e so nos cargos criados aqui
--     (filtro por nome do cargo, nunca "todos os cargos do concurso").
--   * Bacen 2013, Tecnico: as disciplinas ficam fora de cargo_disciplina porque o item 22
--     do edital oficial (objetos de avaliacao) nao pode ser lido; ver observacoes.
--   * situacao e status das etapas sao a foto de 07/10/2026 e envelhecem.
--   * Bacen 2026 nao entra: ate 07/10/2026 so ha a autorizacao (Portaria MGI 5.508/2026),
--     sem edital.

-- ---------------------------------------------------------------- bancas
INSERT INTO banca (nome) SELECT 'FCC'
WHERE NOT EXISTS (SELECT 1 FROM banca WHERE LOWER(nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'));

-- ---------------------------------------------------------------- orgaos
INSERT INTO orgao (nome, sigla, esfera) VALUES
    ('Banco Central do Brasil', 'Bacen', 'FEDERAL'),
    ('Secretaria de Estado da Fazenda de Santa Catarina', 'Sefaz SC', 'ESTADUAL'),
    ('Tribunal de Justiça do Estado do Rio Grande do Sul', 'TJ RS', 'ESTADUAL'),
    ('Tribunal de Contas do Estado de Goiás', 'TCE GO', 'ESTADUAL'),
    ('Secretaria de Estado da Fazenda de Alagoas', 'Sefaz AL', 'ESTADUAL'),
    ('Secretaria de Estado de Planejamento e Gestão do Rio de Janeiro', 'Seplag RJ', 'ESTADUAL')
ON CONFLICT (nome) DO NOTHING;

-- ---------------------------------------------------------------- disciplinas novas
INSERT INTO disciplina (nome)
SELECT v.nome FROM (VALUES
    ('Economia'), ('Ciência de Dados'), ('Segurança da Informação'), ('Engenharia de Software'),
    ('Banco de Dados'), ('Direito Processual Civil'), ('Direito do Consumidor'),
    ('Direito da Criança e do Adolescente'), ('Direito Eleitoral'), ('Contabilidade Pública'),
    ('Finanças Públicas'), ('Auditoria'), ('Administração Financeira e Orçamentária'),
    ('Licitações e Contratos'), ('Controle Externo'), ('Ciência Política'), ('Atualidades'),
    ('Direito Público'), ('Administração Geral'), ('Inteligência Artificial')
) AS v(nome)
WHERE NOT EXISTS (SELECT 1 FROM disciplina d WHERE d.nome = v.nome);

-- ================================================================ 1. Bacen 2013
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'Bacen 2013', 'Banco Central do Brasil',
       (SELECT id FROM orgao WHERE nome = 'Banco Central do Brasil'),
       (SELECT id FROM banca WHERE nome = 'CESPE/CEBRASPE' ORDER BY id LIMIT 1),
       2013, 'ENCERRADO', NULL, NULL, NULL, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'bacen 2013' AND ano = 2013);

UPDATE concurso SET
    orgao        = COALESCE(orgao, 'Banco Central do Brasil'),
    orgao_id     = COALESCE(orgao_id, (SELECT id FROM orgao WHERE nome = 'Banco Central do Brasil')),
    banca_id     = COALESCE(banca_id, (SELECT id FROM banca WHERE nome = 'CESPE/CEBRASPE' ORDER BY id LIMIT 1)),
    vagas        = COALESCE(vagas, 500),
    inscricoes_de  = COALESCE(inscricoes_de, DATE '2013-08-22'),
    inscricoes_ate = COALESCE(inscricoes_ate, DATE '2013-09-09'),
    edital_url   = COALESCE(edital_url,
        'https://www.bcb.gov.br/content/acessoinformacao/analistatecnico2013/CONANALISTA20130815103656-Ed_1_2013_BACEN_ABT_AT.pdf'),
    observacoes  = COALESCE(observacoes,
        'Edital nº 1/2013 – BCB/DEPES, de 15/08/2013 (CESPE/UnB). 400 vagas de Analista e 100 de Técnico, '
     || 'distribuídas por área e praça (Belém, Brasília, Porto Alegre, Salvador, São Paulo). '
     || 'Subsídio: Analista R$ 13.595,85 até 31/12/2013 e R$ 14.289,24 a partir de 2014; '
     || 'Técnico R$ 5.158,23 até 31/12/2013 e R$ 5.421,30 a partir de 2014. Taxa: R$ 120,00 (Analista) e R$ 70,00 (Técnico). '
     || 'Provas Certo/Errado (cada item errado anula um certo) em 20/10/2013. '
     || 'Analista: P1 Conhecimentos Básicos 50 itens, P2 Específicos 70 itens, discursiva de 50 pontos; avaliação de títulos. '
     || 'Técnico: P1 60 itens, P2 60 itens e redação de 50 pontos (até 30 linhas), em 4h30 à tarde. '
     || 'Segunda etapa: Programa de Capacitação em Brasília. Validade de 9 meses. '
     || 'Básicos do Analista: Língua Portuguesa, Língua Inglesa, Raciocínio Lógico, Dir. Constitucional, '
     || 'Dir. Administrativo (exceto Área 6), Sistema Financeiro Nacional e SPB, Economia (exceto Área 3). '
     || 'Específicos do Analista: áreas temáticas de cada área, só no edital. '
     || 'Técnico: as disciplinas (item 22 do edital) não puderam ser conferidas no edital oficial e ficaram fora do conteúdo programático.')
WHERE LOWER(nome) = 'bacen 2013' AND ano = 2013;

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Inscrições (até)',          DATE '2013-09-09', 'CONCLUIDO', 1),
    ('Pagamento da taxa (até)',   DATE '2013-09-20', 'CONCLUIDO', 2),
    ('Provas objetivas e discursivas', DATE '2013-10-20', 'CONCLUIDO', 3),
    ('Gabarito preliminar',       DATE '2013-10-22', 'CONCLUIDO', 4)
) AS v(nome, data, status, ordem)
WHERE LOWER(c.nome) = 'bacen 2013' AND c.ano = 2013
  AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id AND LOWER(e.nome) = LOWER(v.nome));

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, v.nome, v.nivel, v.vagas, NULL, v.salario,
       v.ordem + (SELECT COUNT(*) FROM concurso_cargo x WHERE x.concurso_id = c.id
                  AND LOWER(x.nome) NOT LIKE 'analista – área %' AND LOWER(x.nome) NOT LIKE 'técnico – área %')
FROM concurso c
CROSS JOIN (VALUES
    ('Analista – Área 1: Análise e Desenvolvimento de Sistemas', 'Superior', 15,  13595.85, 1),
    ('Analista – Área 2: Suporte à Infraestrutura de TI',        'Superior', 12,  13595.85, 2),
    ('Analista – Área 3: Política Econômica e Monetária',        'Superior', 50,  13595.85, 3),
    ('Analista – Área 4: Contabilidade e Finanças',              'Superior', 117, 13595.85, 4),
    ('Analista – Área 5: Infraestrutura e Logística',            'Superior', 93,  13595.85, 5),
    ('Analista – Área 6: Gestão e Análise Processual',           'Superior', 113, 13595.85, 6),
    ('Técnico – Área 1: Suporte Técnico-Administrativo',         'Médio',    78,  5158.23,  7),
    ('Técnico – Área 2: Segurança Institucional',                'Médio',    22,  5158.23,  8)
) AS v(nome, nivel, vagas, salario, ordem)
WHERE LOWER(c.nome) = 'bacen 2013' AND c.ano = 2013
  AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id AND LOWER(cc.nome) = LOWER(v.nome));

-- ================================================================ 2. Bacen 2024
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'Bacen 2024', 'Banco Central do Brasil',
       (SELECT id FROM orgao WHERE nome = 'Banco Central do Brasil'),
       (SELECT id FROM banca WHERE nome = 'CESPE/CEBRASPE' ORDER BY id LIMIT 1),
       2024, 'ENCERRADO', NULL, NULL, NULL, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'bacen 2024' AND ano = 2024);

UPDATE concurso SET
    orgao      = COALESCE(orgao, 'Banco Central do Brasil'),
    orgao_id   = COALESCE(orgao_id, (SELECT id FROM orgao WHERE nome = 'Banco Central do Brasil')),
    banca_id   = COALESCE(banca_id, (SELECT id FROM banca WHERE nome = 'CESPE/CEBRASPE' ORDER BY id LIMIT 1)),
    vagas      = COALESCE(vagas, 100),
    taxa       = COALESCE(taxa, 150.00),
    edital_url = COALESCE(edital_url,
        'https://www.bcb.gov.br/content/acessoinformacao/analista_2024/Edital-n-1-de-Abertura-do-concurso.pdf'),
    observacoes = COALESCE(observacoes,
        'Edital nº 1 – BCB, de 15/01/2024 (autorizado pela Portaria MGI nº 3.620/2023). Só o cargo de Analista, '
     || 'com exercício em Brasília. Por área: 50 vagas imediatas (37 AC, 3 PcD, 10 PP) e limite de 150 aprovados '
     || '(112 AC, 8 PcD, 30 PP). Provas Certo/Errado. P1 Conhecimentos Gerais, 50 itens: Língua Portuguesa 25, '
     || 'Noções de lógica e estatística 10, Direito administrativo 5, Fundamentos de microeconomia e macroeconomia 10. '
     || 'P2 Específicos, 70 itens. Economia e Finanças: Macroeconomia 18, Microeconomia 10, Finanças 18, '
     || 'Estatística e Econometria 12, Contabilidade de instituições financeiras (COSIF) 12. '
     || 'TI: Ciência de dados 14, Segurança da Informação 7, Engenharia de Software 24, Infraestrutura em TI 17, '
     || 'Bancos de Dados 4, Gestão em TI 4. Discursivas: P3 dissertação sobre atualidades (30 pontos, 40 linhas) e '
     || 'P4 situação-problema (50 pontos, 80 linhas). Títulos até 5 pontos. Segunda etapa: Procap. '
     || 'Validade de 6 meses, prorrogável. '
     || 'Não conferido no edital (fontes jornalísticas): inscrições de 22/01 a 20/02/2024; provas marcadas para '
     || '19/05/2024 e adiadas para 04/08/2024 (retificação no DOU de 29/05/2024); validade prorrogada até março de 2026.')
WHERE LOWER(nome) = 'bacen 2024' AND ano = 2024;

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, v.nome, 'Superior', 50, NULL, 20924.80,
       v.ordem + (SELECT COUNT(*) FROM concurso_cargo x WHERE x.concurso_id = c.id
                  AND LOWER(x.nome) NOT IN ('analista – economia e finanças', 'analista – tecnologia da informação'))
FROM concurso c
CROSS JOIN (VALUES
    ('Analista – Economia e Finanças',      1),
    ('Analista – Tecnologia da Informação', 2)
) AS v(nome, ordem)
WHERE LOWER(c.nome) = 'bacen 2024' AND c.ano = 2024
  AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id AND LOWER(cc.nome) = LOWER(v.nome));

-- ================================================================ 3. Sefaz SC 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'Sefaz SC 2026', 'Secretaria de Estado da Fazenda de Santa Catarina',
       (SELECT id FROM orgao WHERE nome = 'Secretaria de Estado da Fazenda de Santa Catarina'),
       (SELECT id FROM banca WHERE LOWER(nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas') ORDER BY id LIMIT 1),
       2026, 'EM_ANDAMENTO', 50, DATE '2026-09-04', DATE '2026-10-05', 200.00,
       'https://www.concursosfcc.com.br/concursos/sefsc126/index.html',
       'Edital nº 01/2026, de 31/08/2026. Auditor Estadual de Finanças Públicas, 40 h, lotação em Florianópolis: '
    || '50 vagas (5 PcD) + cadastro de reserva. Taxa paga por DARE. '
    || 'Provas em 22/11/2026, em Florianópolis: P1 Conhecimentos Gerais, 80 questões, peso 1, 4h (manhã); '
    || 'P2 Conhecimentos Específicos, 100 questões, peso 2, 5h (tarde). Notas padronizadas (média 50, desvio 10); '
    || 'habilitado com soma de pelo menos 150. Validade de 2 anos. '
    || 'Conhecimentos Gerais: Língua Portuguesa; Matemática Financeira, Estatística e Raciocínio Lógico; '
    || 'Noções de Dir. Constitucional e de Dir. Administrativo (não cobradas na área de Direito); '
    || 'Ciência e Análise de Dados; Ética, Integridade e Prevenção ao Assédio e Discriminação; Conhecimentos Regionais de SC. '
    || 'Específicos – Administração e Engenharias: Ciclo de Planejamento e Gestão Orçamentária; Gestão de Riscos, '
    || 'Governança e Avaliação por Resultados; Custos no Setor Público; Administração Geral; Administração Estratégica. '
    || 'Ciências Contábeis: CASP e NBC TSP; Contabilidade Geral; Responsabilidade Fiscal; Tecnologia Aplicada à '
    || 'Contabilidade; Custos no Setor Público. Ciências da Computação: Finanças Públicas e tópicos de TI. '
    || 'Específicos de Ciências Econômicas e de Direito: ver o edital.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'sefaz sc 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Resultado da isenção',              DATE '2026-09-15', 'CONCLUIDO', 1),
    ('Inscrições (até)',                  DATE '2026-10-05', 'CONCLUIDO', 2),
    ('Pagamento da taxa (até)',           DATE '2026-10-06', 'CONCLUIDO', 3),
    ('Lista de candidatos PcD',           DATE '2026-10-16', 'PREVISTO', 4),
    ('Provas objetivas',                  DATE '2026-11-22', 'PREVISTO', 5)
) AS v(nome, data, status, ordem)
WHERE LOWER(c.nome) = 'sefaz sc 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id AND LOWER(e.nome) = LOWER(v.nome));

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, v.nome, 'Superior', v.vagas, NULL, 25337.61, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Auditor Estadual de Finanças Públicas – Administração e Engenharias', 6,  1),
    ('Auditor Estadual de Finanças Públicas – Ciências da Computação',      12, 2),
    ('Auditor Estadual de Finanças Públicas – Ciências Contábeis',          20, 3),
    ('Auditor Estadual de Finanças Públicas – Ciências Econômicas',         4,  4),
    ('Auditor Estadual de Finanças Públicas – Direito',                     8,  5)
) AS v(nome, vagas, ordem)
WHERE LOWER(c.nome) = 'sefaz sc 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id AND LOWER(cc.nome) = LOWER(v.nome));

-- ================================================================ 4. TJ RS 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'TJ RS 2026', 'Tribunal de Justiça do Estado do Rio Grande do Sul',
       (SELECT id FROM orgao WHERE nome = 'Tribunal de Justiça do Estado do Rio Grande do Sul'),
       (SELECT id FROM banca WHERE nome = 'FGV' ORDER BY id LIMIT 1),
       2026, 'INSCRICOES_ABERTAS', 30, DATE '2026-09-15', DATE '2026-10-14', 305.00,
       'https://conhecimento.fgv.br/sites/default/files/concursos/sei_9998416_edital_consolidado.pdf',
       'Edital nº 0031/2026-DMAG (DJE de 27/08/2026), consolidado com a 1ª retificação (0032/2026, de 10/09/2026). '
    || 'Juiz de Direito Substituto: 30 vagas (19 AC, 1 PcD, 8 negros, 1 indígena, 1 quilombola). '
    || 'Requisitos: 3 anos de atividade jurídica e aprovação no ENAM. '
    || 'Prova objetiva seletiva em 13/12/2026, das 13h às 18h, em Porto Alegre: 100 questões em 3 blocos; '
    || 'mínimo de 30% em cada bloco e 60% no total. Bloco 1 (40): Língua Portuguesa, Dir. Civil, Dir. Processual Civil, '
    || 'Dir. do Consumidor, Dir. da Criança e do Adolescente. Bloco 2 (30): Dir. Penal, Dir. Processual Penal, '
    || 'Dir. Constitucional, Dir. Eleitoral. Bloco 3 (30): Dir. Empresarial, Dir. Tributário, Dir. Ambiental, '
    || 'Dir. Administrativo, Noções gerais de Direito e formação humanística, Direitos Humanos. '
    || 'Segunda etapa: discursiva e sentença cível em 14/03/2027 e sentença criminal em 15/03/2027. '
    || 'Depois: sindicância da vida pregressa e exames, prova oral e avaliação de títulos.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'tj rs 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Resultado preliminar da isenção',  DATE '2026-09-29', 'CONCLUIDO', 1),
    ('Inscrições (até)',                 DATE '2026-10-14', 'EM_ANDAMENTO', 2),
    ('Pagamento da taxa (até)',          DATE '2026-10-15', 'PREVISTO', 3),
    ('Prova objetiva seletiva',          DATE '2026-12-13', 'PREVISTO', 4),
    ('Discursiva e sentença cível',      DATE '2027-03-14', 'PREVISTO', 5),
    ('Sentença criminal',                DATE '2027-03-15', 'PREVISTO', 6)
) AS v(nome, data, status, ordem)
WHERE LOWER(c.nome) = 'tj rs 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id AND LOWER(e.nome) = LOWER(v.nome));

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, 'Juiz de Direito Substituto', 'Superior', 30, NULL, 30505.36, 1
FROM concurso c
WHERE LOWER(c.nome) = 'tj rs 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id AND LOWER(cc.nome) = 'juiz de direito substituto');

-- ================================================================ 5. TCE GO 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'TCE GO 2026', 'Tribunal de Contas do Estado de Goiás',
       (SELECT id FROM orgao WHERE nome = 'Tribunal de Contas do Estado de Goiás'),
       (SELECT id FROM banca WHERE LOWER(nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas') ORDER BY id LIMIT 1),
       2026, 'INSCRICOES_ABERTAS', 16, DATE '2026-10-05', DATE '2026-11-06', 100.00,
       'https://www.concursosfcc.com.br/concursos/tcego125/index.html',
       'Edital nº 01/2026 (DOE-GO de 25/08/2026). Técnico de Controle Externo, nível médio: '
    || 'A01 Técnico Administrativo, 6 vagas (1 para negros); B02 Tecnologia da Informação, 10 vagas (1 PcD, 2 negros). '
    || 'Provas previstas para 17/01/2027, em Goiânia, em 4h30: Conhecimentos Gerais 25 questões (peso 1), '
    || 'Conhecimentos Específicos 45 questões (peso 2) e discursiva (redação no A01, estudo de caso no B02). '
    || 'Habilitado com soma das notas padronizadas de pelo menos 150. Validade de 2 anos. '
    || 'Conhecimentos Gerais: Língua Portuguesa; Matemática e Raciocínio Lógico; Legislação Institucional. '
    || 'Específicos do A01: Noções de Dir. Administrativo; Licitações e Contratos Administrativos; Noções de Dir. '
    || 'Constitucional; Noções de Controle Externo; Noções de Administração Financeira e Orçamentária; demais itens no edital. '
    || 'Específicos do B02: ver o edital.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'tce go 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Resultado da isenção',                    DATE '2026-09-21', 'CONCLUIDO', 1),
    ('Inscrições (até)',                        DATE '2026-11-06', 'EM_ANDAMENTO', 2),
    ('Pagamento do boleto (até)',               DATE '2026-11-09', 'PREVISTO', 3),
    ('Relação de inscrições e condições especiais', DATE '2026-11-23', 'PREVISTO', 4),
    ('Provas objetivas e discursiva',           DATE '2027-01-17', 'PREVISTO', 5)
) AS v(nome, data, status, ordem)
WHERE LOWER(c.nome) = 'tce go 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id AND LOWER(e.nome) = LOWER(v.nome));

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, v.nome, 'Médio', v.vagas, NULL, 11862.19, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Técnico de Controle Externo – Técnico Administrativo (A01)',     6,  1),
    ('Técnico de Controle Externo – Tecnologia da Informação (B02)',   10, 2)
) AS v(nome, vagas, ordem)
WHERE LOWER(c.nome) = 'tce go 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id AND LOWER(cc.nome) = LOWER(v.nome));

-- ================================================================ 6. Sefaz AL 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'Sefaz AL 2026', 'Secretaria de Estado da Fazenda de Alagoas',
       (SELECT id FROM orgao WHERE nome = 'Secretaria de Estado da Fazenda de Alagoas'),
       (SELECT id FROM banca WHERE nome = 'CESPE/CEBRASPE' ORDER BY id LIMIT 1),
       2026, 'INSCRICOES_ABERTAS', 40, DATE '2026-09-17', DATE '2026-10-21', 300.00,
       'http://www.cebraspe.org.br/concursos/sefaz_al_26',
       'Edital nº 1 – SEFAZ/AL, de 24/08/2026 (DOE-AL de 25/08/2026). Auditor Fiscal da Administração Tributária '
    || 'Estadual (AFTE), nível superior em qualquer área, 40 h. 40 vagas imediatas (30 AC, 2 PcD, 8 PPIQ) e '
    || '60 de cadastro de reserva (45 AC, 3 PcD, 12 PPIQ). Provas Certo/Errado. '
    || 'P1 Conhecimentos Básicos, 60 itens: Matemática Financeira 5, Dir. Constitucional 5, Dir. Administrativo 5, '
    || 'Contabilidade Geral 5, Dir. Tributário 10, Estatística e Probabilidade 10, Contabilidade Pública 10, Economia 10. '
    || 'P2 Conhecimentos Específicos, 100 itens: Finanças Públicas 10, Legislação Tributária Estadual 10, '
    || 'Inteligência Artificial 10, Desenvolvimento de Sistemas 10, Infraestrutura de TIC e Segurança da Informação 10, '
    || 'Reforma Tributária 15, Auditoria Fiscal 15, Ciência de Dados 20. '
    || 'Discursiva: 4 questões de 10 pontos (ciência de dados, auditoria fiscal, finanças públicas e reforma tributária); '
    || 'aprovado com pelo menos 20 pontos. Corrigidas as discursivas de 375 AC, 25 PcD e 100 PPIQ. '
    || 'Não conferido no edital (cronograma do Anexo I não lido): pagamento até 23/10/2026, objetiva em 20/12/2026 '
    || 'e discursiva em 10/01/2027.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'sefaz al 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, 'Inscrições (até)', DATE '2026-10-21', 'EM_ANDAMENTO', 1
FROM concurso c
WHERE LOWER(c.nome) = 'sefaz al 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id AND LOWER(e.nome) = 'inscrições (até)');

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, 'Auditor Fiscal da Administração Tributária Estadual', 'Superior', 40, 60, 25270.68, 1
FROM concurso c
WHERE LOWER(c.nome) = 'sefaz al 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id
                  AND LOWER(cc.nome) = 'auditor fiscal da administração tributária estadual');

-- ================================================================ 7. Seplag RJ 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'Seplag RJ 2026', 'Secretaria de Estado de Planejamento e Gestão do Rio de Janeiro',
       (SELECT id FROM orgao WHERE nome = 'Secretaria de Estado de Planejamento e Gestão do Rio de Janeiro'),
       (SELECT id FROM banca WHERE nome = 'FGV' ORDER BY id LIMIT 1),
       2026, 'INSCRICOES_ABERTAS', 60, DATE '2026-09-10', DATE '2026-10-08', 125.00,
       'https://conhecimento.fgv.br/sites/default/files/concursos/edital-concurso-seplag-09.09.2026.pdf',
       'Edital de 20/08/2026, consolidado com as retificações de 01/09, 04/09 e 09/09/2026. 40 h. '
    || 'Remuneração de até R$ 14.387,75 = vencimento-base R$ 7.572,50 + gratificação de desempenho até R$ 3.786,25 '
    || '+ adicional de qualificação até R$ 3.029,00. Vagas (AC/PcD/negros e indígenas/hipossuficientes): '
    || 'EPPGG Gestão e Governança Pública 15 (10/1/2/2); EPPGG Políticas Públicas 15 (10/1/2/2); '
    || 'APO Planejamento e Orçamento 25 (15/2/5/3); APO Tecnologia da Informação 5 (2/1/1/1). '
    || 'Provas em 29/11/2026: específicos das 8h às 12h; gerais e discursiva das 14h30 às 19h. '
    || 'Conhecimentos Gerais (30): Língua Portuguesa 12, Língua Inglesa 6, Raciocínio Lógico-matemático 6, Atualidades 6. '
    || 'Específicos (70): Direito Público 20 (10 em TI), Estado, Governo e Administração Pública 10, '
    || 'Administração Financeira e Orçamentária 10, e mais: Gestão e Governança: Gestão Governamental 15 e Governança '
    || 'Pública 15; Políticas Públicas: Ciência Política 15 e Políticas Públicas 15; Planejamento e Orçamento: '
    || 'Economia 15 e Finanças Públicas e Planejamento Governamental 15; TI: Governança e Gestão de TIC 20 e '
    || 'Dados e Inteligência Analítica na Administração Pública 20. Discursiva de 40 pontos (estudo de caso + 2 questões). '
    || 'Títulos até 10 pontos. Validade de 2 anos.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'seplag rj 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Inscrições (até)',                 DATE '2026-10-08', 'EM_ANDAMENTO', 1),
    ('Pagamento do boleto (até)',        DATE '2026-10-09', 'PREVISTO', 2),
    ('Provas objetivas e discursiva',    DATE '2026-11-29', 'PREVISTO', 3)
) AS v(nome, data, status, ordem)
WHERE LOWER(c.nome) = 'seplag rj 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id AND LOWER(e.nome) = LOWER(v.nome));

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, v.nome, 'Superior', v.vagas, NULL, 14387.75, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Especialista em Políticas Públicas e Gestão Governamental – Gestão e Governança Pública', 15, 1),
    ('Especialista em Políticas Públicas e Gestão Governamental – Políticas Públicas',          15, 2),
    ('Analista de Planejamento e Orçamento – Planejamento e Orçamento',                         25, 3),
    ('Analista de Planejamento e Orçamento – Tecnologia da Informação',                         5,  4)
) AS v(nome, vagas, ordem)
WHERE LOWER(c.nome) = 'seplag rj 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id AND LOWER(cc.nome) = LOWER(v.nome));

-- ================================================================ conteudo programatico
-- v.cargo e um padrao LIKE sobre o nome do cargo (sempre um dos cargos criados acima).
INSERT INTO cargo_disciplina (cargo_id, disciplina_id, total_topicos, ordem)
SELECT cc.id, d.id, NULL, v.ordem
FROM (VALUES
    -- Bacen 2013: basicos do Analista (Tecnico fora: item 22 nao conferido)
    ('bacen 2013', 'Analista – Área %',   'Língua Portuguesa', 1),
    ('bacen 2013', 'Analista – Área %',   'Língua Inglesa', 2),
    ('bacen 2013', 'Analista – Área %',   'Raciocínio Lógico', 3),
    ('bacen 2013', 'Analista – Área %',   'Direito Constitucional', 4),
    ('bacen 2013', 'Analista – Área 1:%', 'Direito Administrativo', 5),
    ('bacen 2013', 'Analista – Área 2:%', 'Direito Administrativo', 5),
    ('bacen 2013', 'Analista – Área 3:%', 'Direito Administrativo', 5),
    ('bacen 2013', 'Analista – Área 4:%', 'Direito Administrativo', 5),
    ('bacen 2013', 'Analista – Área 5:%', 'Direito Administrativo', 5),
    ('bacen 2013', 'Analista – Área %',   'Conhecimentos Bancários', 6),
    ('bacen 2013', 'Analista – Área 1:%', 'Economia', 7),
    ('bacen 2013', 'Analista – Área 2:%', 'Economia', 7),
    ('bacen 2013', 'Analista – Área 4:%', 'Economia', 7),
    ('bacen 2013', 'Analista – Área 5:%', 'Economia', 7),
    ('bacen 2013', 'Analista – Área 6:%', 'Economia', 7),
    -- Bacen 2024: P1 (todas as areas)
    ('bacen 2024', 'Analista – %', 'Língua Portuguesa', 1),
    ('bacen 2024', 'Analista – %', 'Raciocínio Lógico', 2),
    ('bacen 2024', 'Analista – %', 'Estatística', 3),
    ('bacen 2024', 'Analista – %', 'Direito Administrativo', 4),
    ('bacen 2024', 'Analista – %', 'Economia', 5),
    -- Bacen 2024: P2 ("Finanças", "Infraestrutura em TI" e "Gestão em TI" so nas observacoes)
    ('bacen 2024', 'Analista – Economia e Finanças',      'Contabilidade', 6),
    ('bacen 2024', 'Analista – Tecnologia da Informação', 'Ciência de Dados', 6),
    ('bacen 2024', 'Analista – Tecnologia da Informação', 'Segurança da Informação', 7),
    ('bacen 2024', 'Analista – Tecnologia da Informação', 'Engenharia de Software', 8),
    ('bacen 2024', 'Analista – Tecnologia da Informação', 'Banco de Dados', 9),
    -- Sefaz SC: gerais (Etica/Integridade e Conhecimentos Regionais so nas observacoes)
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – %', 'Língua Portuguesa', 1),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – %', 'Matemática Financeira', 2),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – %', 'Estatística', 3),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – %', 'Raciocínio Lógico', 4),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – %', 'Ciência de Dados', 5),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – Administração%', 'Direito Constitucional', 6),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – Ciências%',      'Direito Constitucional', 6),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – Administração%', 'Direito Administrativo', 7),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – Ciências%',      'Direito Administrativo', 7),
    -- Sefaz SC: especificos lidos
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – Administração e Engenharias', 'Administração Geral', 8),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – Ciências Contábeis', 'Contabilidade Pública', 8),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – Ciências Contábeis', 'Contabilidade', 9),
    ('sefaz sc 2026', 'Auditor Estadual de Finanças Públicas – Ciências da Computação', 'Finanças Públicas', 8),
    -- TJ RS ("Noções gerais de Direito e formação humanística" so nas observacoes)
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Língua Portuguesa', 1),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito Civil', 2),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito Processual Civil', 3),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito do Consumidor', 4),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito da Criança e do Adolescente', 5),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito Penal', 6),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito Processual Penal', 7),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito Constitucional', 8),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito Eleitoral', 9),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito Empresarial', 10),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito Tributário', 11),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito Ambiental', 12),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direito Administrativo', 13),
    ('tj rs 2026', 'Juiz de Direito Substituto', 'Direitos Humanos', 14),
    -- TCE GO: gerais (os dois cargos)
    ('tce go 2026', 'Técnico de Controle Externo – %', 'Língua Portuguesa', 1),
    ('tce go 2026', 'Técnico de Controle Externo – %', 'Matemática', 2),
    ('tce go 2026', 'Técnico de Controle Externo – %', 'Raciocínio Lógico', 3),
    ('tce go 2026', 'Técnico de Controle Externo – %', 'Legislação Institucional', 4),
    -- TCE GO: especificos do A01 (os do B02 nao foram lidos)
    ('tce go 2026', 'Técnico de Controle Externo – Técnico Administrativo (A01)', 'Direito Administrativo', 5),
    ('tce go 2026', 'Técnico de Controle Externo – Técnico Administrativo (A01)', 'Licitações e Contratos', 6),
    ('tce go 2026', 'Técnico de Controle Externo – Técnico Administrativo (A01)', 'Direito Constitucional', 7),
    ('tce go 2026', 'Técnico de Controle Externo – Técnico Administrativo (A01)', 'Controle Externo', 8),
    ('tce go 2026', 'Técnico de Controle Externo – Técnico Administrativo (A01)', 'Administração Financeira e Orçamentária', 9),
    -- Sefaz AL (Legislação Tributária Estadual, Desenvolvimento de Sistemas, Infraestrutura de TIC e
    -- Reforma Tributária so nas observacoes)
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Matemática Financeira', 1),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Direito Constitucional', 2),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Direito Administrativo', 3),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Contabilidade', 4),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Direito Tributário', 5),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Estatística', 6),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Contabilidade Pública', 7),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Economia', 8),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Finanças Públicas', 9),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Inteligência Artificial', 10),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Auditoria', 11),
    ('sefaz al 2026', 'Auditor Fiscal da Administração Tributária Estadual', 'Ciência de Dados', 12),
    -- Seplag RJ: gerais e especificos comuns
    ('seplag rj 2026', '%', 'Língua Portuguesa', 1),
    ('seplag rj 2026', '%', 'Língua Inglesa', 2),
    ('seplag rj 2026', '%', 'Raciocínio Lógico', 3),
    ('seplag rj 2026', '%', 'Atualidades', 4),
    ('seplag rj 2026', '%', 'Direito Público', 5),
    ('seplag rj 2026', '%', 'Administração Pública', 6),
    ('seplag rj 2026', '%', 'Administração Financeira e Orçamentária', 7),
    -- Seplag RJ: especificos por especialidade (gestao e TI so nas observacoes)
    ('seplag rj 2026', '% – Políticas Públicas', 'Ciência Política', 8),
    ('seplag rj 2026', '% – Políticas Públicas', 'Políticas Públicas', 9),
    ('seplag rj 2026', '% – Planejamento e Orçamento', 'Economia', 8),
    ('seplag rj 2026', '% – Planejamento e Orçamento', 'Finanças Públicas', 9)
) AS v(concurso, cargo, disciplina, ordem)
JOIN concurso c ON LOWER(c.nome) = v.concurso
JOIN concurso_cargo cc ON cc.concurso_id = c.id AND cc.nome LIKE v.cargo
JOIN disciplina d ON d.id = (SELECT MIN(id) FROM disciplina WHERE nome = v.disciplina)
WHERE NOT EXISTS (SELECT 1 FROM cargo_disciplina x WHERE x.cargo_id = cc.id AND x.disciplina_id = d.id);

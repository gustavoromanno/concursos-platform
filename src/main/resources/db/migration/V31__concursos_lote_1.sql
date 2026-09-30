-- Catalogo de concursos, lote 1: cinco concursos com inscricoes abertas em 30/09/2026.
--
-- Fonte de cada numero: o edital de abertura (link em concurso.edital_url) e, quando
-- houve, as retificacoes. Nada aqui vem dos prints do outro site.
--
-- Decisoes:
--   * Nome no padrao "Sigla Ano", o mesmo que a importacao de provas procura; assim a
--     prova importada depois cai no concurso certo em vez de criar um duplicado.
--   * Tudo com WHERE NOT EXISTS: se o concurso/banca/disciplina ja existir em producao
--     (criado a mao ou pela importacao), nada e duplicado.
--   * cargo_disciplina so recebe disciplinas de verdade. Blocos do edital que nao sao
--     uma disciplina ("Conhecimentos Especificos", "Area de Atuacao", "Legislacao")
--     ficam so nas observacoes, com o numero de questoes, ate a divisao por assunto.
--   * total_topicos fica NULL: o numero de topicos depende de ler o Anexo de conteudo.
--   * situacao e status das etapas sao a foto de 30/09/2026 e envelhecem.

-- ---------------------------------------------------------------- bancas
INSERT INTO banca (nome) SELECT 'CESGRANRIO'
WHERE NOT EXISTS (SELECT 1 FROM banca WHERE LOWER(nome) IN ('cesgranrio', 'fundação cesgranrio'));
INSERT INTO banca (nome) SELECT 'IBAM'
WHERE NOT EXISTS (SELECT 1 FROM banca WHERE LOWER(nome) = 'ibam');
INSERT INTO banca (nome) SELECT 'Instituto Avalia'
WHERE NOT EXISTS (SELECT 1 FROM banca WHERE LOWER(nome) = 'instituto avalia');

-- ---------------------------------------------------------------- orgaos
INSERT INTO orgao (nome, sigla, esfera) VALUES
    ('Polícia Civil do Estado do Amapá', 'PC AP', 'ESTADUAL'),
    ('Guarda Civil Municipal de Salvador', 'GCM Salvador', 'MUNICIPAL'),
    ('Corpo de Bombeiros Militar de Alagoas', 'CBM AL', 'ESTADUAL'),
    ('Guarda Civil Municipal de Guarulhos', 'GCM Guarulhos', 'MUNICIPAL'),
    ('Departamento Estadual de Trânsito de São Paulo', 'Detran SP', 'ESTADUAL')
ON CONFLICT (nome) DO NOTHING;

-- ---------------------------------------------------------------- disciplinas novas
INSERT INTO disciplina (nome)
SELECT v.nome FROM (VALUES
    ('Direito Penal'), ('Direito Processual Penal'), ('Direito Civil'), ('Direito Empresarial'),
    ('Direito Tributário'), ('Direito Ambiental'), ('Direitos Humanos'), ('Medicina Legal'),
    ('Criminologia'), ('História do Amapá'), ('Geografia do Amapá'), ('Legislação Institucional'),
    ('Estatística'), ('Contabilidade'), ('Matemática'), ('Língua Inglesa'), ('Física'),
    ('Química'), ('Biologia'), ('Mecânica'), ('Administração Pública'), ('Políticas Públicas'),
    ('Legislação de Trânsito')
) AS v(nome)
WHERE NOT EXISTS (SELECT 1 FROM disciplina d WHERE d.nome = v.nome);

-- ================================================================ 1. PC AP 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'PC AP 2026', 'Polícia Civil do Estado do Amapá',
       (SELECT id FROM orgao WHERE nome = 'Polícia Civil do Estado do Amapá'),
       (SELECT id FROM banca WHERE LOWER(nome) IN ('cesgranrio', 'fundação cesgranrio') ORDER BY id LIMIT 1),
       2026, 'INSCRICOES_ABERTAS', NULL, DATE '2026-09-22', DATE '2026-10-19', NULL,
       'https://editor.amapa.gov.br/arquivos_portais/publicacoes/SEAD_74da0ce312e6a3d54cb2c43b0aabc5c2.pdf',
       'Edital nº 001/2026 (SEAD/AP). Somente cadastro de reserva: 102 Delegado e 294 Oficial Investigador (5% PcD). '
    || 'Taxa: R$ 200,00 (Delegado) e R$ 150,00 (Oficial Investigador). Prova objetiva de 80 questões (5 alternativas) '
    || 'e prova escrita no mesmo turno, 5h30, em Macapá; mínimo de 60% e nenhuma disciplina zerada. '
    || 'Delegado: Dir. Administrativo 10, Dir. Constitucional 10, Dir. Penal 17, Dir. Processual Penal 17, '
    || 'Dir. Civil/Empresarial 5, Dir. Tributário 3, Dir. Ambiental 4, Medicina Legal 4, Criminologia 4, '
    || 'História do Amapá 2, Geografia do Amapá 2, Legislação Institucional 2; dissertativa de caso concreto. '
    || 'Oficial Investigador: Língua Portuguesa 8, Raciocínio Lógico-Matemático 8, Estatística e Análise de Dados 8, '
    || 'Contabilidade e Análise Financeira 8, Informática 12, Dir. Penal 8, Dir. Processual Penal 8, '
    || 'Dir. Constitucional 5, Dir. Administrativo 5, Direitos Humanos 4, História do Amapá 2, '
    || 'Geografia do Amapá 2, Legislação Institucional 2; redação de 25 a 30 linhas.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'pc ap 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Inscrições (até)',                   DATE '2026-10-19', 'EM_ANDAMENTO', 1),
    ('Pagamento da taxa (até)',            DATE '2026-11-03', 'PREVISTO', 2),
    ('Prova objetiva e prova escrita',     DATE '2026-12-06', 'PREVISTO', 3),
    ('Gabarito preliminar e questões',     DATE '2026-12-07', 'PREVISTO', 4),
    ('Resultado definitivo da objetiva',   DATE '2027-01-29', 'PREVISTO', 5)
) AS v(nome, data, status, ordem)
WHERE c.nome = 'PC AP 2026' AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id);

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, v.nome, 'Superior', NULL, v.cr, v.salario, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Delegado de Polícia Civil',           102, 31439.06, 1),
    ('Oficial Investigador de Polícia Civil', 294, 7327.04, 2)
) AS v(nome, cr, salario, ordem)
WHERE c.nome = 'PC AP 2026' AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id);

-- ================================================================ 2. GCM Salvador 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'GCM Salvador 2026', 'Guarda Civil Municipal de Salvador',
       (SELECT id FROM orgao WHERE nome = 'Guarda Civil Municipal de Salvador'),
       (SELECT id FROM banca WHERE nome = 'FGV' ORDER BY id LIMIT 1),
       2026, 'INSCRICOES_ABERTAS', 50, DATE '2026-09-21', DATE '2026-10-30', 98.00,
       'https://semge.salvador.ba.gov.br/prefeitura-de-salvador-abre-concurso-para-guarda-civil-municipal-com-50-vagas-imediatas/',
       'Edital nº 02/2026. 50 vagas imediatas + cadastro de reserva (30% PPP, 5% PcD, 20% sexo feminino). '
    || 'Remuneração de R$ 3.546,42 = vencimento de R$ 1.773,21 + gratificações do edital. '
    || 'Prova objetiva de 70 questões (5 alternativas). Módulo I (28): Língua Portuguesa 10, '
    || 'Raciocínio Lógico-Matemático 10, Informática 8. Módulo II (42): Dir. Constitucional e Dir. Civil 10, '
    || 'Dir. Penal e Dir. Processual Penal 7, Administração e Políticas Públicas 5, '
    || 'Conhecimentos na Área de Atuação 10, Legislação 10. Mínimo: 14 no Módulo I, 21 no Módulo II e 35 no total. '
    || 'Depois: TAF, avaliação psicológica e curso de formação.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'gcm salvador 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Inscrições (até)',                    DATE '2026-10-30', 'EM_ANDAMENTO', 1),
    ('Pagamento da taxa (até)',             DATE '2026-11-06', 'PREVISTO', 2),
    ('Relação definitiva das inscrições',   DATE '2026-12-01', 'PREVISTO', 3),
    ('Prova objetiva',                      DATE '2027-01-17', 'PREVISTO', 4)
) AS v(nome, data, status, ordem)
WHERE c.nome = 'GCM Salvador 2026' AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id);

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, 'Guarda Civil Municipal – 3ª Classe', 'Médio', 50, NULL, 3546.42, 1
FROM concurso c
WHERE c.nome = 'GCM Salvador 2026' AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id);

-- ================================================================ 3. CBM AL 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'CBM AL 2026', 'Corpo de Bombeiros Militar de Alagoas',
       (SELECT id FROM orgao WHERE nome = 'Corpo de Bombeiros Militar de Alagoas'),
       (SELECT id FROM banca WHERE nome = 'CESPE/CEBRASPE' ORDER BY id LIMIT 1),
       2026, 'INSCRICOES_ABERTAS', 172, DATE '2026-09-18', DATE '2026-10-26', 150.00,
       'https://www.cebraspe.org.br/concursos/cbm_al_26/',
       'Edital nº 1 – CBM/AL (22/05/2026), retomado pelo Edital nº 7 (04/09/2026) com reabertura das inscrições; '
    || 'inscrições feitas no período original continuam válidas e a prova saiu de 11/10/2026 para 24/01/2027. '
    || '172 vagas imediatas + 172 de cadastro de reserva. Salários são os de após a formação '
    || '(durante o curso: R$ 3.874,43 cadete e R$ 2.354,67 aluno-soldado). '
    || 'Prova objetiva de 120 itens Certo/Errado: 50 de conhecimentos básicos (Língua Portuguesa, Língua Inglesa, '
    || 'Informática, Matemática, Raciocínio Lógico e Analítico, Legislação do CBM/AL, Cidadania e Direitos Humanos) '
    || 'e 70 de específicos (Oficial: Física, Química, Biologia, Dir. Administrativo e Dir. Constitucional; '
    || 'Soldado QPBM/1 e QPBM/2: Física, Química, Biologia e Mecânica Geral). Mais prova discursiva. '
    || 'O edital não divide os itens por disciplina. Provas em Maceió e Arapiraca.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'cbm al 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Inscrições reabertas (até)',          DATE '2026-10-26', 'EM_ANDAMENTO', 1),
    ('Pagamento da taxa (até)',             DATE '2026-10-28', 'PREVISTO', 2),
    ('Consulta aos locais de prova',        DATE '2027-01-08', 'PREVISTO', 3),
    ('Provas objetiva e discursiva',        DATE '2027-01-24', 'PREVISTO', 4),
    ('Resultado final da objetiva',         DATE '2027-03-04', 'PREVISTO', 5)
) AS v(nome, data, status, ordem)
WHERE c.nome = 'CBM AL 2026' AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id);

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, v.nome, 'Médio', v.vagas, v.cr, v.salario, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Oficial de Estado-Maior',                       22,  22, 11563.77, 1),
    ('Soldado – QPBM/1 (Combatente)',                105, 105,  6067.53, 2),
    ('Soldado – QPBM/2 (Motomecanização)',            45,  45,  6067.53, 3)
) AS v(nome, vagas, cr, salario, ordem)
WHERE c.nome = 'CBM AL 2026' AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id);

-- ================================================================ 4. GCM Guarulhos 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'GCM Guarulhos 2026', 'Guarda Civil Municipal de Guarulhos',
       (SELECT id FROM orgao WHERE nome = 'Guarda Civil Municipal de Guarulhos'),
       (SELECT id FROM banca WHERE LOWER(nome) = 'ibam' ORDER BY id LIMIT 1),
       2026, 'INSCRICOES_ABERTAS', 200, DATE '2026-09-10', DATE '2026-10-15', 89.00,
       'https://www.ibamsp-concursos.org.br',
       'Edital nº 09/2026-SGE01. 200 vagas: 150 ampla concorrência, 40 sexo feminino, 10 PcD. '
    || 'Salário-base de R$ 2.445,06 mais auxílio-alimentação, auxílio-transporte e cesta básica. '
    || 'Exige ensino médio e CNH B. Prova objetiva de 40 questões (4 alternativas), 3h30, 100 pontos, mínimo 50: '
    || 'Língua Portuguesa 10 (peso 2), Matemática 5 (peso 2), Informática 5 (peso 2), '
    || 'Conhecimentos Específicos 20 (peso 3). Depois: investigação social, exame antropométrico, TAF, '
    || 'avaliação psicológica, exames médicos e toxicológico.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'gcm guarulhos 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Inscrições (até)',                    DATE '2026-10-15', 'EM_ANDAMENTO', 1),
    ('Edital de convocação para a prova',   DATE '2026-11-17', 'PREVISTO', 2),
    ('Prova objetiva',                      DATE '2026-11-29', 'PREVISTO', 3)
) AS v(nome, data, status, ordem)
WHERE c.nome = 'GCM Guarulhos 2026' AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id);

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, 'Guarda Civil Municipal – Grau A, Categoria 1, 3ª Classe', 'Médio', 200, NULL, 2445.06, 1
FROM concurso c
WHERE c.nome = 'GCM Guarulhos 2026' AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id);

-- ================================================================ 5. Detran SP 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'Detran SP 2026', 'Departamento Estadual de Trânsito de São Paulo',
       (SELECT id FROM orgao WHERE nome = 'Departamento Estadual de Trânsito de São Paulo'),
       (SELECT id FROM banca WHERE LOWER(nome) = 'instituto avalia' ORDER BY id LIMIT 1),
       2026, 'INSCRICOES_ABERTAS', 145, DATE '2026-09-09', DATE '2026-10-07', 98.00,
       'https://arquivos.qconcursos.com/f/concurso-detra-sp-2026-edital.pdf',
       'Edital de Concurso Público DETRAN-SP nº 1, de 8/09/2026. Emprego público (CLT), 40 h. '
    || '145 vagas + cadastro de reserva (138 ampla concorrência, 7 PcD). Nível superior em qualquer área e CNH B. '
    || 'Prova objetiva de 60 questões (5 alternativas), valendo 10 pontos, mínimo 6 e nenhuma área zerada. '
    || 'Gerais (0,10 cada): Língua Portuguesa 10, Matemática e Raciocínio Lógico-Matemático 5, Informática 5, '
    || 'Dir. Constitucional 5, Dir. Administrativo 5. Específicos (0,20 cada): Código de Trânsito Brasileiro 17, '
    || 'Resoluções do CONTRAN 8, Legislação estadual 5. Redação no mesmo dia, à tarde.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'detran sp 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Inscrições (até)',                    DATE '2026-10-07', 'EM_ANDAMENTO', 1),
    ('Pagamento da taxa (até)',             DATE '2026-10-08', 'PREVISTO', 2),
    ('Divulgação dos locais de prova',      DATE '2026-10-26', 'PREVISTO', 3),
    ('Provas objetiva e de redação',        DATE '2026-11-01', 'PREVISTO', 4)
) AS v(nome, data, status, ordem)
WHERE c.nome = 'Detran SP 2026' AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id);

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, 'Agente Estadual de Trânsito', 'Superior', 145, NULL, 5702.18, 1
FROM concurso c
WHERE c.nome = 'Detran SP 2026' AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id);

-- ================================================================ conteudo programatico
-- (concurso, cargo, disciplina, ordem). Cargo NULL = todos os cargos do concurso.
INSERT INTO cargo_disciplina (cargo_id, disciplina_id, total_topicos, ordem)
SELECT cc.id, d.id, NULL, v.ordem
FROM (VALUES
    -- PC AP: Delegado
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Direito Administrativo', 1),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Direito Constitucional', 2),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Direito Penal', 3),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Direito Processual Penal', 4),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Direito Civil', 5),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Direito Empresarial', 6),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Direito Tributário', 7),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Direito Ambiental', 8),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Medicina Legal', 9),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Criminologia', 10),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'História do Amapá', 11),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Geografia do Amapá', 12),
    ('PC AP 2026', 'Delegado de Polícia Civil', 'Legislação Institucional', 13),
    -- PC AP: Oficial Investigador
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Língua Portuguesa', 1),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Raciocínio Lógico', 2),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Estatística', 3),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Contabilidade', 4),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Noções de Informática', 5),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Direito Penal', 6),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Direito Processual Penal', 7),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Direito Constitucional', 8),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Direito Administrativo', 9),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Direitos Humanos', 10),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'História do Amapá', 11),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Geografia do Amapá', 12),
    ('PC AP 2026', 'Oficial Investigador de Polícia Civil', 'Legislação Institucional', 13),
    -- GCM Salvador ("Área de Atuação" e "Legislação" so nas observacoes)
    ('GCM Salvador 2026', NULL, 'Língua Portuguesa', 1),
    ('GCM Salvador 2026', NULL, 'Raciocínio Lógico', 2),
    ('GCM Salvador 2026', NULL, 'Noções de Informática', 3),
    ('GCM Salvador 2026', NULL, 'Direito Constitucional', 4),
    ('GCM Salvador 2026', NULL, 'Direito Civil', 5),
    ('GCM Salvador 2026', NULL, 'Direito Penal', 6),
    ('GCM Salvador 2026', NULL, 'Direito Processual Penal', 7),
    ('GCM Salvador 2026', NULL, 'Administração Pública', 8),
    ('GCM Salvador 2026', NULL, 'Políticas Públicas', 9),
    -- CBM AL: basicos (todos os cargos)
    ('CBM AL 2026', NULL, 'Língua Portuguesa', 1),
    ('CBM AL 2026', NULL, 'Língua Inglesa', 2),
    ('CBM AL 2026', NULL, 'Noções de Informática', 3),
    ('CBM AL 2026', NULL, 'Matemática', 4),
    ('CBM AL 2026', NULL, 'Raciocínio Lógico', 5),
    ('CBM AL 2026', NULL, 'Legislação Institucional', 6),
    ('CBM AL 2026', NULL, 'Direitos Humanos', 7),
    ('CBM AL 2026', NULL, 'Física', 8),
    ('CBM AL 2026', NULL, 'Química', 9),
    ('CBM AL 2026', NULL, 'Biologia', 10),
    -- CBM AL: especificos por cargo
    ('CBM AL 2026', 'Oficial de Estado-Maior', 'Direito Administrativo', 11),
    ('CBM AL 2026', 'Oficial de Estado-Maior', 'Direito Constitucional', 12),
    ('CBM AL 2026', 'Soldado – QPBM/1 (Combatente)', 'Mecânica', 11),
    ('CBM AL 2026', 'Soldado – QPBM/2 (Motomecanização)', 'Mecânica', 11),
    -- GCM Guarulhos ("Conhecimentos Específicos" so nas observacoes)
    ('GCM Guarulhos 2026', NULL, 'Língua Portuguesa', 1),
    ('GCM Guarulhos 2026', NULL, 'Matemática', 2),
    ('GCM Guarulhos 2026', NULL, 'Noções de Informática', 3),
    -- Detran SP ("Legislação estadual" so nas observacoes)
    ('Detran SP 2026', NULL, 'Língua Portuguesa', 1),
    ('Detran SP 2026', NULL, 'Raciocínio Lógico', 2),
    ('Detran SP 2026', NULL, 'Noções de Informática', 3),
    ('Detran SP 2026', NULL, 'Direito Constitucional', 4),
    ('Detran SP 2026', NULL, 'Direito Administrativo', 5),
    ('Detran SP 2026', NULL, 'Legislação de Trânsito', 6)
) AS v(concurso, cargo, disciplina, ordem)
JOIN concurso c ON c.nome = v.concurso
JOIN concurso_cargo cc ON cc.concurso_id = c.id AND (v.cargo IS NULL OR cc.nome = v.cargo)
JOIN disciplina d ON d.id = (SELECT MIN(id) FROM disciplina WHERE nome = v.disciplina)
WHERE NOT EXISTS (SELECT 1 FROM cargo_disciplina x WHERE x.cargo_id = cc.id AND x.disciplina_id = d.id);

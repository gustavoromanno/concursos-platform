-- Catalogo de concursos, lote 3: AgSUS 2026 (processo seletivo simplificado, Psicologo).
--
-- Fonte: Edital de Processo Seletivo Simplificado nº 98/2026, de 17/08/2026, lido na
-- integra (inclusive o Anexo I, cronograma). Mesmas regras da V34: WHERE NOT EXISTS,
-- concurso por LOWER(nome) + ano, cargos e etapas por nome, cargo_disciplina so com
-- disciplinas de verdade. situacao e status sao calculados pela data na aplicacao
-- (SituacaoCalculadora); os valores gravados sao so a base.

-- ---------------------------------------------------------------- orgao
INSERT INTO orgao (nome, sigla, esfera) VALUES
    ('Agência Brasileira de Apoio à Gestão do SUS', 'AgSUS', 'FEDERAL')
ON CONFLICT (nome) DO NOTHING;

-- ---------------------------------------------------------------- disciplina nova
INSERT INTO disciplina (nome)
SELECT v.nome FROM (VALUES ('Sistema Único de Saúde (SUS)')) AS v(nome)
WHERE NOT EXISTS (SELECT 1 FROM disciplina d WHERE d.nome = v.nome);

-- ================================================================ AgSUS 2026
INSERT INTO concurso (nome, orgao, orgao_id, banca_id, ano, situacao, vagas,
                      inscricoes_de, inscricoes_ate, taxa, edital_url, observacoes)
SELECT 'AgSUS 2026', 'Agência Brasileira de Apoio à Gestão do SUS',
       (SELECT id FROM orgao WHERE nome = 'Agência Brasileira de Apoio à Gestão do SUS'),
       (SELECT id FROM banca WHERE nome = 'CESPE/CEBRASPE' ORDER BY id LIMIT 1),
       2026, 'EM_ANDAMENTO', NULL, DATE '2026-09-04', DATE '2026-09-18', 0.00,
       'http://www.cebraspe.org.br/concursos/agsus_26_acolhedora',
       'Edital de Processo Seletivo Simplificado nº 98/2026, de 17/08/2026 (Programa AgSUS Acolhedora). '
    || 'Psicólogo, contratação pela CLT, 40 h, lotação na sede da AgSUS (Brasília/DF). Somente cadastro de reserva '
    || '(ampla concorrência, PcD 5%, pretos ou pardos, indígenas e quilombolas). Remuneração de R$ 8.800,00 + benefícios '
    || '(auxílio-alimentação/refeição, vale-transporte, auxílio-saúde e bem-estar). Inscrição gratuita. '
    || 'Requisitos: graduação em Psicologia e registro ativo no CRP. '
    || 'Prova objetiva on-line, em 1º/11/2026, de manhã, em dois blocos de 40 min: 40 itens Certo/Errado (1 ponto '
    || 'cada, sem desconto): Normativos da AgSUS 10, Sistema Único de Saúde (SUS) 10 e Conhecimentos Específicos 20; '
    || 'eliminado com menos de 20 pontos. Avaliação de títulos até 8 pontos. Validade de 1 ano. '
    || 'Conhecimentos Específicos: Código de Ética Profissional do Psicólogo; saúde mental e atenção psicossocial; '
    || 'escuta qualificada e acolhimento; intervenção em crise; políticas públicas de saúde mental; RAPS; '
    || 'promoção e prevenção em saúde mental; trabalho interdisciplinar; diversidade cultural e interculturalidade; '
    || 'bem viver indígena.'
WHERE NOT EXISTS (SELECT 1 FROM concurso WHERE LOWER(nome) = 'agsus 2026' AND ano = 2026);

INSERT INTO concurso_etapa (concurso_id, nome, data_prevista, status, ordem)
SELECT c.id, v.nome, v.data, v.status, v.ordem
FROM concurso c
CROSS JOIN (VALUES
    ('Inscrições (até)',                         DATE '2026-09-18', 'CONCLUIDO', 1),
    ('Prova objetiva on-line',                   DATE '2026-11-01', 'PREVISTO',  2),
    ('Gabarito oficial preliminar',              DATE '2026-11-06', 'PREVISTO',  3),
    ('Resultado final na prova objetiva',        DATE '2026-11-27', 'PREVISTO',  4),
    ('Envio dos títulos (até)',                  DATE '2026-12-31', 'PREVISTO',  5),
    ('Resultado final do processo seletivo',     DATE '2027-01-26', 'PREVISTO',  6)
) AS v(nome, data, status, ordem)
WHERE LOWER(c.nome) = 'agsus 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_etapa e WHERE e.concurso_id = c.id AND LOWER(e.nome) = LOWER(v.nome));

INSERT INTO concurso_cargo (concurso_id, nome, nivel, vagas, cadastro_reserva, salario, ordem)
SELECT c.id, 'Psicólogo', 'Superior', NULL, NULL, 8800.00, 1
FROM concurso c
WHERE LOWER(c.nome) = 'agsus 2026' AND c.ano = 2026
  AND NOT EXISTS (SELECT 1 FROM concurso_cargo cc WHERE cc.concurso_id = c.id AND LOWER(cc.nome) = 'psicólogo');

-- ---------------------------------------------------------------- conteudo programatico
-- "Normativos da AgSUS" -> Legislação Institucional. Os conhecimentos especificos de
-- Psicologia ficam so nas observacoes (nao ha disciplina equivalente no banco).
INSERT INTO cargo_disciplina (cargo_id, disciplina_id, total_topicos, ordem)
SELECT cc.id, d.id, NULL, v.ordem
FROM (VALUES
    ('agsus 2026', 'Psicólogo', 'Legislação Institucional', 1),
    ('agsus 2026', 'Psicólogo', 'Sistema Único de Saúde (SUS)', 2)
) AS v(concurso, cargo, disciplina, ordem)
JOIN concurso c ON LOWER(c.nome) = v.concurso
JOIN concurso_cargo cc ON cc.concurso_id = c.id AND cc.nome = v.cargo
JOIN disciplina d ON d.id = (SELECT MIN(id) FROM disciplina WHERE nome = v.disciplina)
WHERE NOT EXISTS (SELECT 1 FROM cargo_disciplina x WHERE x.cargo_id = cc.id AND x.disciplina_id = d.id);

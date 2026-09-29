-- Lote 4 de questoes AUTORAIS, gerado por scripts/gerar_questoes.py.
-- Nao edite a mao: altere o script e gere uma nova versao (V16, V17...).
--
-- Questoes escritas no estilo das bancas, nao copiadas de provas (o texto
-- das provas e protegido por direito autoral das bancas).

-- Disciplinas novas
INSERT INTO disciplina (nome) SELECT 'Conhecimentos Bancários'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Conhecimentos Bancários');
INSERT INTO disciplina (nome) SELECT 'Matemática Financeira'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Matemática Financeira');

-- Assuntos (so cria os que ainda nao existem)
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Sistema Financeiro Nacional' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Sistema Financeiro Nacional') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Garantias do sistema financeiro' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Garantias do sistema financeiro') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Política monetária' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Política monetária') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Mercado de capitais' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Mercado de capitais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Prevenção à lavagem de dinheiro' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Prevenção à lavagem de dinheiro') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Produtos e serviços bancários' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Produtos e serviços bancários') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Juros compostos' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Juros compostos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Juros simples' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Juros simples') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Taxas equivalentes' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Taxas equivalentes') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Descontos' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Descontos') AND a.disciplina_id = d.id);

-- Questoes
WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No Sistema Financeiro Nacional, o órgão normativo máximo, responsável por fixar as diretrizes das políticas monetária, creditícia e cambial, é o',
           d.id, b.id, 2024, 'Sistema Financeiro Nacional', a.id,
           'O CMN é o órgão normativo máximo do SFN (Lei 4.595/1964). O Banco Central executa e fiscaliza as normas do CMN; a CVM regula o mercado de valores mobiliários; a Susep, o de seguros.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistema Financeiro Nacional')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Conselho Monetário Nacional.', TRUE, 1),
        ('Comissão de Valores Mobiliários.', FALSE, 2),
        ('Tesouro Nacional.', FALSE, 3),
        ('Banco Central do Brasil.', FALSE, 4),
        ('Superintendência de Seguros Privados.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Integra o Conselho Monetário Nacional:',
           d.id, b.id, 2023, 'Sistema Financeiro Nacional', a.id,
           'O CMN é composto pelo Ministro da Fazenda, que o preside, pelo Ministro do Planejamento e Orçamento e pelo Presidente do Banco Central do Brasil. Dirigentes de bancos, da CVM ou de associações do setor não integram o Conselho.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistema Financeiro Nacional')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('o Presidente da Comissão de Valores Mobiliários.', FALSE, 1),
        ('o Presidente do Banco Central do Brasil.', TRUE, 2),
        ('o Presidente da Federação Brasileira de Bancos.', FALSE, 3),
        ('o Presidente do Banco do Brasil.', FALSE, 4),
        ('o Ministro-Chefe da Casa Civil.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O Fundo Garantidor de Créditos (FGC) garante os depósitos e investimentos cobertos até o limite, por CPF ou CNPJ e por instituição (ou conglomerado), de',
           d.id, b.id, 2025, 'Garantias do sistema financeiro', a.id,
           'O limite é de R$ 250 mil por pessoa e por instituição ou conglomerado. Há ainda um teto global de R$ 1 milhão por pessoa, a cada período de quatro anos, somando todas as instituições.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Garantias do sistema financeiro')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 70 mil.', FALSE, 1),
        ('R$ 100 mil.', FALSE, 2),
        ('R$ 250 mil.', TRUE, 3),
        ('R$ 1 milhão.', FALSE, 4),
        ('R$ 500 mil.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O órgão do Banco Central responsável por definir a meta da taxa Selic é o',
           d.id, b.id, 2024, 'Política monetária', a.id,
           'O Copom, formado pela diretoria do Banco Central, fixa a meta da Selic em reuniões periódicas. O CMN define a meta de inflação que o Copom persegue.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Política monetária')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Conselho Monetário Nacional (CMN).', FALSE, 1),
        ('Conselho de Controle de Atividades Financeiras (Coaf).', FALSE, 2),
        ('Tesouro Nacional.', FALSE, 3),
        ('Comitê de Política Monetária (Copom).', TRUE, 4),
        ('Conselho de Recursos do SFN.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A autarquia responsável por regular e fiscalizar o mercado de valores mobiliários, como ações e debêntures, é a',
           d.id, b.id, 2023, 'Mercado de capitais', a.id,
           'A CVM (Lei 6.385/1976) disciplina e fiscaliza o mercado de valores mobiliários. Susep e Previc cuidam de seguros e de previdência complementar fechada.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Mercado de capitais')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Caixa Econômica Federal.', FALSE, 1),
        ('Superintendência de Seguros Privados.', FALSE, 2),
        ('Superintendência Nacional de Previdência Complementar.', FALSE, 3),
        ('Receita Federal.', FALSE, 4),
        ('Comissão de Valores Mobiliários.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'As três fases clássicas do processo de lavagem de dinheiro são',
           d.id, b.id, 2025, 'Prevenção à lavagem de dinheiro', a.id,
           'Na colocação, o dinheiro ilícito entra no sistema; na ocultação (ou estratificação), movimentações dificultam rastrear a origem; na integração, os recursos voltam à economia com aparência lícita.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Prevenção à lavagem de dinheiro')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('colocação, ocultação e integração.', TRUE, 1),
        ('captação, aplicação e resgate.', FALSE, 2),
        ('emissão, circulação e liquidação.', FALSE, 3),
        ('ocultação, fracionamento e saque.', FALSE, 4),
        ('depósito, transferência e consumo.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O instrumento de política monetária pelo qual o Banco Central exige que os bancos mantenham parte dos depósitos recolhida junto a ele é o',
           d.id, b.id, 2024, 'Política monetária', a.id,
           'O compulsório retira parte dos depósitos da capacidade de empréstimo dos bancos. Redesconto é o empréstimo do BC aos bancos; open market é a compra e venda de títulos públicos.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Política monetária')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('câmbio flutuante.', FALSE, 1),
        ('recolhimento compulsório.', TRUE, 2),
        ('redesconto.', FALSE, 3),
        ('open market.', FALSE, 4),
        ('superávit primário.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O Banco Central do Brasil é autarquia de natureza especial, e sua autonomia está prevista em lei complementar.',
           d.id, b.id, 2024, 'Sistema Financeiro Nacional', a.id,
           'A Lei Complementar 179/2021 definiu o BC como autarquia de natureza especial, sem vinculação a ministério e com mandatos fixos para presidente e diretores.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistema Financeiro Nacional')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O Conselho Monetário Nacional é presidido pelo presidente do Banco Central do Brasil.',
           d.id, b.id, 2023, 'Sistema Financeiro Nacional', a.id,
           'O CMN é presidido pelo Ministro da Fazenda. O presidente do Banco Central é um dos seus integrantes.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistema Financeiro Nacional')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', FALSE, 1),
        ('Errado', TRUE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O Fundo Garantidor de Créditos é entidade pública mantida com recursos do Tesouro Nacional.',
           d.id, b.id, 2025, 'Garantias do sistema financeiro', a.id,
           'O FGC é entidade privada, sem fins lucrativos, mantida por contribuições das instituições financeiras associadas.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Garantias do sistema financeiro')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', FALSE, 1),
        ('Errado', TRUE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O aumento da alíquota do recolhimento compulsório tende a reduzir a quantidade de recursos que os bancos têm disponível para conceder empréstimos.',
           d.id, b.id, 2024, 'Política monetária', a.id,
           'Com mais recursos retidos no Banco Central, sobra menos para emprestar: é uma medida contracionista.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Política monetária')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Os depósitos em caderneta de poupança estão entre os créditos cobertos pela garantia do FGC, observados os limites.',
           d.id, b.id, 2023, 'Garantias do sistema financeiro', a.id,
           'Poupança, depósitos à vista e a prazo (como CDB), letras de câmbio e LCI/LCA estão entre os créditos garantidos pelo FGC.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Garantias do sistema financeiro')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O Pix é um arranjo de pagamentos instituído pelo Banco Central do Brasil.',
           d.id, b.id, 2025, 'Produtos e serviços bancários', a.id,
           'O Pix foi criado e é gerido pelo Banco Central, que define suas regras e opera a infraestrutura de liquidação.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Produtos e serviços bancários')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Um capital de R$ 1.000,00 foi aplicado a juros compostos de 10% ao mês. Após 2 meses, o montante é',
           d.id, b.id, 2024, 'Juros compostos', a.id,
           'M = C × (1 + i)^n = 1.000 × 1,1² = 1.000 × 1,21 = R$ 1.210,00. Os R$ 1.200,00 seriam o montante em juros simples.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Juros compostos')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 1.020,00.', FALSE, 1),
        ('R$ 1.100,00.', FALSE, 2),
        ('R$ 1.210,00.', TRUE, 3),
        ('R$ 1.221,00.', FALSE, 4),
        ('R$ 1.200,00.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Aplicado a juros simples de 5% ao mês por 4 meses, um capital rendeu R$ 400,00 de juros. Esse capital era de',
           d.id, b.id, 2023, 'Juros simples', a.id,
           'J = C × i × t → 400 = C × 0,05 × 4 → C = 400 / 0,20 = R$ 2.000,00.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Juros simples')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 8.000,00.', FALSE, 1),
        ('R$ 2.500,00.', FALSE, 2),
        ('R$ 1.600,00.', FALSE, 3),
        ('R$ 2.000,00.', TRUE, 4),
        ('R$ 400,00.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No regime de juros compostos, a taxa anual equivalente a 1% ao mês é de, aproximadamente,',
           d.id, b.id, 2025, 'Taxas equivalentes', a.id,
           '(1,01)^12 ≈ 1,1268, ou seja, cerca de 12,68% ao ano. Os 12% seriam a taxa proporcional, que só vale em juros simples.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Taxas equivalentes')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('12%.', FALSE, 1),
        ('11,36%.', FALSE, 2),
        ('13,2%.', FALSE, 3),
        ('12,5%.', FALSE, 4),
        ('12,68%.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Uma taxa nominal de 12% ao ano, com capitalização mensal, corresponde a uma taxa efetiva mensal de',
           d.id, b.id, 2024, 'Taxas equivalentes', a.id,
           'Na taxa nominal, a taxa do período de capitalização é a proporcional: 12% / 12 = 1% ao mês.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Taxas equivalentes')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('1%.', TRUE, 1),
        ('0,5%.', FALSE, 2),
        ('0,95%.', FALSE, 3),
        ('1,2%.', FALSE, 4),
        ('12%.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Um título de R$ 5.000,00 foi descontado 2 meses antes do vencimento, com desconto comercial simples à taxa de 3% ao mês. O valor do desconto é',
           d.id, b.id, 2023, 'Descontos', a.id,
           'No desconto comercial (por fora), D = N × i × t = 5.000 × 0,03 × 2 = R$ 300,00. O valor recebido é R$ 4.700,00.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Descontos')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 600,00.', FALSE, 1),
        ('R$ 300,00.', TRUE, 2),
        ('R$ 283,02.', FALSE, 3),
        ('R$ 150,00.', FALSE, 4),
        ('R$ 4.700,00.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O montante de R$ 2.000,00 aplicados a juros simples de 2% ao mês, durante 6 meses, é',
           d.id, b.id, 2025, 'Juros simples', a.id,
           'J = 2.000 × 0,02 × 6 = R$ 240,00; M = 2.000 + 240 = R$ 2.240,00. O valor de R$ 2.252,32 seria em juros compostos.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Juros simples')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 2.400,00.', FALSE, 1),
        ('R$ 2.252,32.', FALSE, 2),
        ('R$ 2.240,00.', TRUE, 3),
        ('R$ 2.024,00.', FALSE, 4),
        ('R$ 2.120,00.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No regime de juros simples, os juros de cada período são calculados sempre sobre o capital inicial.',
           d.id, b.id, 2024, 'Juros simples', a.id,
           'Essa é a característica dos juros simples: não há juros sobre juros, e o crescimento do montante é linear.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Juros simples')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Para prazos superiores a um período, a mesma taxa rende menos juros no regime composto do que no regime simples.',
           d.id, b.id, 2023, 'Juros compostos', a.id,
           'É o contrário: acima de um período, os juros compostos rendem mais, porque incidem também sobre os juros já acumulados.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Juros compostos')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', FALSE, 1),
        ('Errado', TRUE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Em juros simples, a taxa de 2% ao mês é proporcional à taxa de 24% ao ano.',
           d.id, b.id, 2025, 'Taxas equivalentes', a.id,
           'Em juros simples, taxas proporcionais são equivalentes: 2% × 12 = 24%.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Taxas equivalentes')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Em juros compostos, uma taxa de 21% em dois meses corresponde a 10% ao mês.',
           d.id, b.id, 2024, 'Juros compostos', a.id,
           '1,10 × 1,10 = 1,21, ou seja, 21% no bimestre.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Juros compostos')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

-- O cargo do concurso de exemplo passa a cobrar tambem as disciplinas novas,
-- para que o objetivo de estudo reflita o conteudo disponivel.
INSERT INTO cargo_disciplina (cargo_id, disciplina_id, total_topicos, ordem)
SELECT cc.id, d.id, 0,
       (SELECT COALESCE(MAX(x.ordem), 0) FROM cargo_disciplina x WHERE x.cargo_id = cc.id)
         + ROW_NUMBER() OVER (PARTITION BY cc.id ORDER BY d.nome)
FROM concurso_cargo cc
CROSS JOIN disciplina d
WHERE cc.nome = 'Analista Administrativo'
  AND NOT EXISTS (SELECT 1 FROM cargo_disciplina x WHERE x.cargo_id = cc.id AND x.disciplina_id = d.id);

-- Recalcula o total de topicos de cada disciplina do conteudo programatico.
UPDATE cargo_disciplina cd
SET total_topicos = (SELECT COUNT(*) FROM assunto a WHERE a.disciplina_id = cd.disciplina_id);

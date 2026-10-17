-- Lote 8 de questoes AUTORAIS, gerado por scripts/gerar_questoes.py.
-- Nao edite a mao: altere o script e gere uma nova versao (V16, V17...).
--
-- Questoes escritas no estilo das bancas, nao copiadas de provas (o texto
-- das provas e protegido por direito autoral das bancas).

-- Disciplinas novas

-- Assuntos (so cria os que ainda nao existem)
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Juros compostos' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Juros compostos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Taxas equivalentes' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Taxas equivalentes') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Taxa real e inflação' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Taxa real e inflação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Juros simples' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Juros simples') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Descontos' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Descontos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Sistemas de amortização' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Sistemas de amortização') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Análise de investimentos' FROM disciplina d WHERE d.nome = 'Matemática Financeira'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Análise de investimentos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Sistema Financeiro Nacional' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Sistema Financeiro Nacional') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Política monetária' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Política monetária') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Garantias do sistema financeiro' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Garantias do sistema financeiro') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Produtos e serviços bancários' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Produtos e serviços bancários') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Títulos públicos' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Títulos públicos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Mercado de capitais' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Mercado de capitais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Sistema de Pagamentos Brasileiro' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Sistema de Pagamentos Brasileiro') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Open Finance' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Open Finance') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Prevenção à lavagem de dinheiro' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Prevenção à lavagem de dinheiro') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Mercado de câmbio' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Mercado de câmbio') AND a.disciplina_id = d.id);

-- Questoes
WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Um cliente aplicou R$ 10.000,00 a juros compostos de 2% ao mês. Sem novos depósitos nem retiradas, o montante ao final de 3 meses será de',
           d.id, b.id, 2026, 'Juros compostos', a.id,
           'M = C × (1 + i)^n = 10.000 × 1,02³ = 10.000 × 1,061208 = R$ 10.612,08. R$ 10.600,00 seria o resultado com juros simples (3 × 2% = 6%).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Juros compostos')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 10.612,08', TRUE, 1),
        ('R$ 10.600,00', FALSE, 2),
        ('R$ 10.604,00', FALSE, 3),
        ('R$ 10.200,00', FALSE, 4),
        ('R$ 10.824,32', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Uma taxa nominal de 12% ao ano, com capitalização mensal, corresponde a uma taxa efetiva anual de, aproximadamente,',
           d.id, b.id, 2026, 'Taxas equivalentes', a.id,
           'Taxa nominal de 12% a.a. capitalizada mensalmente = 1% ao mês. Efetiva anual: (1,01)¹² − 1 ≈ 0,1268, ou seja, 12,68% ao ano.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Taxas equivalentes')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('13,00%', FALSE, 1),
        ('12,68%', TRUE, 2),
        ('12,36%', FALSE, 3),
        ('11,39%', FALSE, 4),
        ('12,00%', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No regime de juros compostos, a taxa anual equivalente a 10% ao semestre é de',
           d.id, b.id, 2025, 'Taxas equivalentes', a.id,
           '(1 + 0,10)² − 1 = 1,21 − 1 = 0,21 = 21% ao ano. Somar as taxas (20%) só vale no regime de juros simples.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Taxas equivalentes')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('20%', FALSE, 1),
        ('22%', FALSE, 2),
        ('21%', TRUE, 3),
        ('19%', FALSE, 4),
        ('10%', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Uma aplicação rendeu 10,25% em um ano no qual a inflação foi de 5%. A taxa real de rendimento no período foi de',
           d.id, b.id, 2025, 'Taxa real e inflação', a.id,
           'Fórmula de Fisher: (1 + nominal) = (1 + real) × (1 + inflação). 1,1025 / 1,05 = 1,05, logo a taxa real é de 5%. Subtrair as taxas (5,25%) é só uma aproximação.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Taxa real e inflação')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('15,25%', FALSE, 1),
        ('5,25%', FALSE, 2),
        ('2,05%', FALSE, 3),
        ('5,00%', TRUE, 4),
        ('4,75%', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Qual capital, aplicado a juros simples de 1,5% ao mês, rende R$ 900,00 de juros em 8 meses?',
           d.id, b.id, 2026, 'Juros simples', a.id,
           'J = C × i × t → 900 = C × 0,015 × 8 = C × 0,12 → C = 900 / 0,12 = R$ 7.500,00.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Juros simples')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 6.000,00', FALSE, 1),
        ('R$ 13.500,00', FALSE, 2),
        ('R$ 9.000,00', FALSE, 3),
        ('R$ 1.200,00', FALSE, 4),
        ('R$ 7.500,00', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Um título de R$ 5.000,00 foi descontado 3 meses antes do vencimento, com desconto comercial (por fora) simples à taxa de 2% ao mês. O valor recebido pelo cliente foi de',
           d.id, b.id, 2025, 'Descontos', a.id,
           'Desconto comercial simples: D = N × i × n = 5.000 × 0,02 × 3 = R$ 300,00. Valor atual = 5.000 − 300 = R$ 4.700,00. R$ 4.716,98 seria o desconto racional (por dentro).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Descontos')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 4.700,00', TRUE, 1),
        ('R$ 4.716,98', FALSE, 2),
        ('R$ 4.600,00', FALSE, 3),
        ('R$ 4.705,96', FALSE, 4),
        ('R$ 4.900,00', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Um título de R$ 5.000,00 foi resgatado 3 meses antes do vencimento, com desconto racional (por dentro) simples à taxa de 2% ao mês. O valor atual do título é de, aproximadamente,',
           d.id, b.id, 2026, 'Descontos', a.id,
           'No desconto racional simples, A = N / (1 + i × n) = 5.000 / 1,06 ≈ R$ 4.716,98. O desconto (R$ 283,02) incide sobre o valor atual, e não sobre o nominal.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Descontos')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 4.700,00', FALSE, 1),
        ('R$ 4.716,98', TRUE, 2),
        ('R$ 4.850,00', FALSE, 3),
        ('R$ 4.283,02', FALSE, 4),
        ('R$ 4.711,61', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Um financiamento de R$ 120.000,00 será pago em 24 prestações mensais pelo Sistema de Amortização Constante (SAC), à taxa de 1% ao mês. O valor da segunda prestação será de',
           d.id, b.id, 2026, 'Sistemas de amortização', a.id,
           'Amortização constante: 120.000 / 24 = R$ 5.000,00. Após a 1ª parcela, o saldo é R$ 115.000,00; os juros da 2ª são 1% × 115.000 = R$ 1.150,00. Prestação: 5.000 + 1.150 = R$ 6.150,00 (a 1ª foi R$ 6.200,00).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistemas de amortização')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 5.000,00', FALSE, 1),
        ('R$ 6.100,00', FALSE, 2),
        ('R$ 6.150,00', TRUE, 3),
        ('R$ 6.250,00', FALSE, 4),
        ('R$ 6.200,00', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Um empréstimo de R$ 10.000,00 será quitado em 2 prestações mensais iguais pelo Sistema Francês (Price), à taxa de 2% ao mês, com a primeira paga um mês após a contratação. A amortização contida na primeira prestação é de, aproximadamente,',
           d.id, b.id, 2025, 'Sistemas de amortização', a.id,
           'Prestação: PMT = 10.000 × 0,02 / (1 − 1,02⁻²) ≈ R$ 5.150,50. Juros do 1º mês: 2% × 10.000 = R$ 200,00. Amortização = 5.150,50 − 200,00 = R$ 4.950,50.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistemas de amortização')
    WHERE d.nome = 'Matemática Financeira'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 5.050,50', FALSE, 1),
        ('R$ 200,00', FALSE, 2),
        ('R$ 5.150,50', FALSE, 3),
        ('R$ 4.950,50', TRUE, 4),
        ('R$ 5.000,00', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No Sistema de Amortização Constante (SAC), as prestações são decrescentes, pois a parcela de juros diminui a cada período, enquanto no Sistema Francês (Price) as prestações são constantes e as amortizações, crescentes.',
           d.id, b.id, 2026, 'Sistemas de amortização', a.id,
           'No SAC a amortização é fixa e os juros caem com o saldo devedor, então a prestação diminui. Na Price a prestação é fixa: como os juros caem, a parte que amortiza a dívida cresce.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistemas de amortização')
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
    SELECT 'Um projeto exige investimento de R$ 1.000,00 hoje e retorna R$ 1.210,00 daqui a dois anos. Considerando taxa mínima de atratividade de 10% ao ano, o valor presente líquido do projeto é nulo, e sua taxa interna de retorno é de 10% ao ano.',
           d.id, b.id, 2025, 'Análise de investimentos', a.id,
           'VPL = 1.210 / 1,1² − 1.000 = 1.210 / 1,21 − 1.000 = 0. A TIR é a taxa que zera o VPL; como ele é zero a 10%, a TIR é 10% ao ano.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Análise de investimentos')
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
    SELECT 'O Conselho Monetário Nacional (CMN), órgão normativo máximo do Sistema Financeiro Nacional, é composto pelo',
           d.id, b.id, 2026, 'Sistema Financeiro Nacional', a.id,
           'O CMN tem três membros: o Ministro da Fazenda (que o preside), o Ministro do Planejamento e Orçamento e o Presidente do Banco Central. A Secretaria-Executiva do CMN é exercida pelo Banco Central.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistema Financeiro Nacional')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Presidente do Banco Central do Brasil e pelos diretores do Banco Central.', FALSE, 1),
        ('Ministro da Fazenda, pelo Presidente do Banco do Brasil e pelo Presidente da Caixa Econômica Federal.', FALSE, 2),
        ('Presidente da CVM, pelo Superintendente da Susep e pelo Diretor-Superintendente da Previc.', FALSE, 3),
        ('Presidente da República, pelo Ministro da Fazenda e pelo Presidente da CVM.', FALSE, 4),
        ('Ministro da Fazenda, pelo Ministro do Planejamento e Orçamento e pelo Presidente do Banco Central do Brasil.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No Sistema Financeiro Nacional, a entidade responsável por regular e fiscalizar o mercado de valores mobiliários, como o de ações e debêntures, é a',
           d.id, b.id, 2025, 'Sistema Financeiro Nacional', a.id,
           'A CVM é a autarquia que regula e fiscaliza o mercado de valores mobiliários. A Susep cuida de seguros, capitalização e previdência aberta; a Previc, dos fundos de pensão (previdência fechada).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistema Financeiro Nacional')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Comissão de Valores Mobiliários (CVM).', TRUE, 1),
        ('Secretaria do Tesouro Nacional.', FALSE, 2),
        ('Superintendência Nacional de Previdência Complementar (Previc).', FALSE, 3),
        ('Caixa Econômica Federal.', FALSE, 4),
        ('Superintendência de Seguros Privados (Susep).', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O Comitê de Política Monetária (Copom), do Banco Central do Brasil, define a meta para a taxa Selic, enquanto a meta para a inflação é fixada pelo Conselho Monetário Nacional.',
           d.id, b.id, 2026, 'Política monetária', a.id,
           'O CMN fixa a meta de inflação; o Copom, órgão do Banco Central, define a meta da Selic, principal instrumento para levar a inflação à meta.',
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
    SELECT 'Uma medida de política monetária que tende a REDUZIR a liquidez da economia é',
           d.id, b.id, 2026, 'Política monetária', a.id,
           'Com compulsório maior, os bancos precisam deixar mais recursos parados no Banco Central e têm menos para emprestar. As demais medidas aumentam a liquidez (expansionistas).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Política monetária')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('a compra de títulos públicos pelo Banco Central no mercado aberto.', FALSE, 1),
        ('o aumento da alíquota do recolhimento compulsório sobre depósitos.', TRUE, 2),
        ('a ampliação dos prazos das operações de redesconto.', FALSE, 3),
        ('a redução da meta para a taxa Selic.', FALSE, 4),
        ('a redução da taxa cobrada nas operações de redesconto.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Entre os produtos a seguir, aquele que conta com a garantia do Fundo Garantidor de Créditos (FGC) é',
           d.id, b.id, 2026, 'Garantias do sistema financeiro', a.id,
           'O FGC cobre, até o limite por pessoa e por instituição, depósitos à vista, poupança, CDB, LCI, LCA e outros. Fundos de investimento, debêntures e ações não têm essa garantia; títulos públicos têm o risco do Tesouro Nacional, não do FGC.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Garantias do sistema financeiro')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('a ação negociada em bolsa.', FALSE, 1),
        ('o título do Tesouro Direto.', FALSE, 2),
        ('o Certificado de Depósito Bancário (CDB).', TRUE, 3),
        ('a debênture emitida por uma sociedade anônima.', FALSE, 4),
        ('a cota de fundo de investimento.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A garantia ordinária do Fundo Garantidor de Créditos é limitada a R$ 250 mil por titular em cada instituição ou conglomerado financeiro.',
           d.id, b.id, 2025, 'Garantias do sistema financeiro', a.id,
           'O limite ordinário do FGC é de R$ 250 mil por CPF ou CNPJ, por instituição ou conglomerado. Há também um teto global de R$ 1 milhão por titular, válido por períodos de quatro anos.',
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
    SELECT 'Pela regra em vigor para os depósitos de poupança feitos desde maio de 2012, quando a meta da taxa Selic é superior a 8,5% ao ano, a remuneração da poupança corresponde a',
           d.id, b.id, 2025, 'Produtos e serviços bancários', a.id,
           'Com Selic acima de 8,5% a.a., a poupança rende 0,5% ao mês + TR. Com Selic igual ou abaixo de 8,5% a.a., rende 70% da Selic + TR.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Produtos e serviços bancários')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('a variação do IPCA mais 6% ao ano.', FALSE, 1),
        ('100% do CDI.', FALSE, 2),
        ('70% da meta da Selic mais a TR.', FALSE, 3),
        ('0,5% ao mês mais a Taxa Referencial (TR).', TRUE, 4),
        ('a meta da Selic, sem acréscimo.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No Tesouro Direto, o título cuja rentabilidade acompanha a taxa Selic, sendo indicado para reserva de emergência por sua baixa oscilação de preço, é o',
           d.id, b.id, 2026, 'Títulos públicos', a.id,
           'O Tesouro Selic (LFT) é pós-fixado à Selic. Os prefixados e os atrelados ao IPCA têm preço que oscila mais com as mudanças de juros (marcação a mercado).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Títulos públicos')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Tesouro IPCA+ com Juros Semestrais (NTN-B).', FALSE, 1),
        ('Tesouro Prefixado (LTN).', FALSE, 2),
        ('Tesouro IPCA+ (NTN-B Principal).', FALSE, 3),
        ('Tesouro Prefixado com Juros Semestrais (NTN-F).', FALSE, 4),
        ('Tesouro Selic (LFT).', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Nas sociedades anônimas, as ações ordinárias conferem ao acionista direito a voto nas assembleias, e as ações preferenciais costumam ter prioridade na distribuição de dividendos, podendo ter o direito a voto restringido ou suprimido pelo estatuto.',
           d.id, b.id, 2026, 'Mercado de capitais', a.id,
           'Ações ordinárias (ON) dão direito a voto. As preferenciais (PN) têm vantagens como prioridade nos dividendos e podem ter o voto restringido ou suprimido, nos termos da Lei 6.404/1976.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Mercado de capitais')
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
    SELECT 'O Pix é o arranjo de pagamentos instantâneos instituído pelo Banco Central do Brasil, que permite transferências e pagamentos em poucos segundos, inclusive em fins de semana e feriados.',
           d.id, b.id, 2025, 'Sistema de Pagamentos Brasileiro', a.id,
           'O Pix foi criado e é regulamentado pelo Banco Central e funciona 24 horas por dia, 7 dias por semana, com liquidação em tempo real.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistema de Pagamentos Brasileiro')
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
    SELECT 'No Open Finance, uma instituição pode acessar os dados de conta de um cliente mantidos em outra instituição independentemente de autorização, bastando que ambas participem do ecossistema.',
           d.id, b.id, 2026, 'Open Finance', a.id,
           'O compartilhamento de dados no Open Finance depende do consentimento prévio do cliente, que define quais dados, com quem e por quanto tempo, e pode revogá-lo a qualquer momento.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Open Finance')
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
    SELECT 'A lavagem de dinheiro costuma ser descrita em três etapas. A etapa em que se realizam várias operações para dificultar o rastreamento da origem ilícita dos recursos é chamada de',
           d.id, b.id, 2025, 'Prevenção à lavagem de dinheiro', a.id,
           'Etapas clássicas: colocação (o dinheiro entra no sistema), ocultação ou estratificação (operações sucessivas para apagar a trilha) e integração (o recurso volta com aparência lícita).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESGRANRIO'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Prevenção à lavagem de dinheiro')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('ocultação (estratificação).', TRUE, 1),
        ('integração.', FALSE, 2),
        ('compensação.', FALSE, 3),
        ('custódia.', FALSE, 4),
        ('colocação.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O Conselho de Controle de Atividades Financeiras (Coaf), unidade de inteligência financeira do Brasil, está vinculado administrativamente ao Banco Central do Brasil.',
           d.id, b.id, 2026, 'Prevenção à lavagem de dinheiro', a.id,
           'Desde a Lei 13.974/2020, o Coaf é vinculado administrativamente ao Banco Central, com autonomia técnica e operacional. Recebe e analisa as comunicações de operações suspeitas.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Prevenção à lavagem de dinheiro')
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
    SELECT 'A valorização do real frente ao dólar tende a baratear as importações e a reduzir a competitividade dos produtos brasileiros exportados.',
           d.id, b.id, 2025, 'Mercado de câmbio', a.id,
           'Com o real mais forte, são necessários menos reais para comprar o mesmo bem importado; já o exportador recebe menos reais pelo mesmo valor em dólar, perdendo competitividade.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Mercado de câmbio')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

-- O cargo do concurso de exemplo passa a cobrar as disciplinas deste lote.
INSERT INTO cargo_disciplina (cargo_id, disciplina_id, total_topicos, ordem)
SELECT cc.id, d.id, 0,
       (SELECT COALESCE(MAX(x.ordem), 0) FROM cargo_disciplina x WHERE x.cargo_id = cc.id)
         + ROW_NUMBER() OVER (PARTITION BY cc.id ORDER BY d.nome)
FROM concurso_cargo cc
CROSS JOIN disciplina d
WHERE cc.nome = 'Analista Administrativo'
  AND d.nome IN ('Conhecimentos Bancários', 'Matemática Financeira')
  AND NOT EXISTS (SELECT 1 FROM cargo_disciplina x WHERE x.cargo_id = cc.id AND x.disciplina_id = d.id);

-- Recalcula o total de topicos so onde ele ja era contado (NULL = catalogo, fica NULL).
UPDATE cargo_disciplina cd
SET total_topicos = (SELECT COUNT(*) FROM assunto a WHERE a.disciplina_id = cd.disciplina_id)
WHERE cd.total_topicos IS NOT NULL;

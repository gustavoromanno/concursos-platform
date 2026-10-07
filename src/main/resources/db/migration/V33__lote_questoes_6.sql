-- Lote 6 de questoes AUTORAIS, gerado por scripts/gerar_questoes.py.
-- Nao edite a mao: altere o script e gere uma nova versao (V16, V17...).
--
-- Questoes escritas no estilo das bancas, nao copiadas de provas (o texto
-- das provas e protegido por direito autoral das bancas).

-- Disciplinas novas
INSERT INTO disciplina (nome) SELECT 'Economia'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Economia');
INSERT INTO disciplina (nome) SELECT 'Estatística'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Estatística');

-- Assuntos (so cria os que ainda nao existem)
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Elasticidade' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Elasticidade') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Estruturas de mercado' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Estruturas de mercado') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Falhas de mercado' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Falhas de mercado') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Contas nacionais' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Contas nacionais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Modelo IS-LM' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Modelo IS-LM') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Política monetária' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Política monetária') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Balanço de pagamentos' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Balanço de pagamentos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Política monetária' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Política monetária') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Sistema de Pagamentos Brasileiro' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Sistema de Pagamentos Brasileiro') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Sistema Financeiro Nacional' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Sistema Financeiro Nacional') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Garantias do sistema financeiro' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Garantias do sistema financeiro') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Prevenção à lavagem de dinheiro' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Prevenção à lavagem de dinheiro') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Anulação e revogação' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Anulação e revogação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Poderes administrativos' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Poderes administrativos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Licitações' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Licitações') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Responsabilidade civil do Estado' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Responsabilidade civil do Estado') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Servidores públicos' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Servidores públicos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Medidas de tendência central' FROM disciplina d WHERE d.nome = 'Estatística'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Medidas de tendência central') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Medidas de dispersão' FROM disciplina d WHERE d.nome = 'Estatística'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Medidas de dispersão') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Probabilidade' FROM disciplina d WHERE d.nome = 'Estatística'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Probabilidade') AND a.disciplina_id = d.id);

-- Questoes
WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Se a demanda por determinado bem é elástica em relação ao preço, um aumento do preço reduz a receita total do vendedor.',
           d.id, b.id, 2025, 'Elasticidade', a.id,
           'Com demanda elástica (|E| > 1), a quantidade cai proporcionalmente mais do que o preço sobe, e a receita total (preço × quantidade) diminui. Com demanda inelástica ocorre o contrário.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Elasticidade')
    WHERE d.nome = 'Economia'
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
    SELECT 'Bens que possuem muitos substitutos próximos tendem a apresentar demanda inelástica em relação ao preço.',
           d.id, b.id, 2026, 'Elasticidade', a.id,
           'É o contrário: quanto mais substitutos próximos, mais fácil trocar de bem quando o preço sobe, e mais elástica é a demanda.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Elasticidade')
    WHERE d.nome = 'Economia'
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
    SELECT 'Em concorrência perfeita, no equilíbrio de longo prazo, a firma representativa',
           d.id, b.id, 2025, 'Estruturas de mercado', a.id,
           'Sem barreiras à entrada, lucros extraordinários atraem novas firmas até que desapareçam. A firma é tomadora de preço e produz onde P = CMg = CMe mínimo.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Estruturas de mercado')
    WHERE d.nome = 'Economia'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('obtém lucro econômico nulo, produzindo onde o preço é igual ao custo marginal e ao custo médio mínimo.', TRUE, 1),
        ('fixa o preço de mercado, por ser a maior ofertante.', FALSE, 2),
        ('obtém lucro econômico positivo, garantido pelas barreiras à entrada.', FALSE, 3),
        ('cobra preço acima do custo marginal, por ter poder de mercado.', FALSE, 4),
        ('diferencia seu produto para fidelizar consumidores.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O monopolista maximiza o lucro produzindo a quantidade em que a receita marginal se iguala ao custo marginal e cobra, por essa quantidade, preço superior ao custo marginal.',
           d.id, b.id, 2024, 'Estruturas de mercado', a.id,
           'A condição RMg = CMg vale para qualquer firma. No monopólio, a demanda é negativamente inclinada, a receita marginal fica abaixo do preço e, por isso, P > CMg no ponto ótimo.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Estruturas de mercado')
    WHERE d.nome = 'Economia'
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
    SELECT 'Bens públicos puros caracterizam-se pela rivalidade no consumo e pela possibilidade de exclusão de quem não paga.',
           d.id, b.id, 2025, 'Falhas de mercado', a.id,
           'Bens públicos puros são não rivais (o consumo de um não reduz o do outro) e não excludentes (não é possível impedir o uso de quem não paga), o que gera o problema do carona.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Falhas de mercado')
    WHERE d.nome = 'Economia'
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
    SELECT 'Pela ótica da despesa, o produto interno bruto corresponde à soma de',
           d.id, b.id, 2024, 'Contas nacionais', a.id,
           'PIB = C + I + G + (X − M). A soma de salários, lucros, juros e aluguéis é a ótica da renda; o valor da produção menos o consumo intermediário é a ótica do produto.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Contas nacionais')
    WHERE d.nome = 'Economia'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('salários, lucros, juros e aluguéis pagos na economia.', FALSE, 1),
        ('consumo das famílias, investimento, gastos do governo e exportações, menos as importações.', TRUE, 2),
        ('consumo das famílias, investimento e importações, menos as exportações.', FALSE, 3),
        ('renda nacional bruta e renda enviada ao exterior.', FALSE, 4),
        ('valor bruto da produção de todos os setores, sem descontar o consumo intermediário.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No modelo IS-LM, uma política fiscal expansionista, com a política monetária inalterada, tende a',
           d.id, b.id, 2026, 'Modelo IS-LM', a.id,
           'O aumento de gastos ou a redução de tributos desloca a IS para a direita. Com a LM parada, o novo equilíbrio tem renda e juros maiores; a alta dos juros reduz parte do investimento privado (efeito deslocamento).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Modelo IS-LM')
    WHERE d.nome = 'Economia'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('deslocar a curva LM para a direita, reduzindo a taxa de juros.', FALSE, 1),
        ('deslocar a curva IS para a direita, elevando a renda e reduzindo a taxa de juros.', FALSE, 2),
        ('deslocar a curva IS para a direita, elevando a renda e a taxa de juros.', TRUE, 3),
        ('deslocar a curva IS para a esquerda, reduzindo a renda.', FALSE, 4),
        ('não alterar a renda nem a taxa de juros.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No regime de metas para a inflação adotado no Brasil, o índice de preços de referência é o IGP-M.',
           d.id, b.id, 2025, 'Política monetária', a.id,
           'O índice de referência é o IPCA, calculado pelo IBGE. O IGP-M, da FGV, é usado em contratos como os de aluguel, mas não no regime de metas.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Política monetária')
    WHERE d.nome = 'Economia'
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
    SELECT 'No sistema de meta contínua para a inflação, a meta é considerada descumprida quando a inflação acumulada em doze meses permanece fora do intervalo de tolerância por seis meses consecutivos.',
           d.id, b.id, 2026, 'Política monetária', a.id,
           'Regra do Decreto 12.079/2024, em vigor desde 2025: a aferição deixou de ser só no ano-calendário e passou a ser mensal, sobre o IPCA acumulado em doze meses.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Política monetária')
    WHERE d.nome = 'Economia'
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
    SELECT 'As remessas de lucros e dividendos de empresas estrangeiras instaladas no país para suas matrizes são registradas na conta de renda primária do balanço de pagamentos.',
           d.id, b.id, 2024, 'Balanço de pagamentos', a.id,
           'Lucros, dividendos e juros são rendimentos de fatores de produção e ficam na renda primária, dentro das transações correntes. A entrada do investimento em si fica na conta financeira.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Balanço de pagamentos')
    WHERE d.nome = 'Economia'
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
    SELECT 'A venda de títulos públicos pelo Banco Central no mercado aberto reduz a liquidez da economia.',
           d.id, b.id, 2025, 'Política monetária', a.id,
           'Ao vender títulos, o Banco Central recebe reservas dos bancos e retira moeda de circulação. A compra de títulos tem o efeito oposto e injeta liquidez.',
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
    SELECT 'O Comitê de Política Monetária (Copom) reúne-se ordinariamente oito vezes por ano para definir a meta da taxa Selic.',
           d.id, b.id, 2024, 'Política monetária', a.id,
           'Desde 2017 o calendário do Copom prevê oito reuniões ordinárias por ano, de dois dias cada. Podem ser convocadas reuniões extraordinárias.',
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
    SELECT 'O Pix é um arranjo de pagamentos instituído pelo Banco Central do Brasil que liquida as transações em tempo real, inclusive em fins de semana e feriados.',
           d.id, b.id, 2025, 'Sistema de Pagamentos Brasileiro', a.id,
           'O Pix funciona 24 horas por dia, todos os dias, com liquidação em tempo real. O Banco Central é o instituidor do arranjo e opera a infraestrutura de liquidação.',
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
    SELECT 'O sistema operado pelo Banco Central que realiza a liquidação bruta em tempo real das transferências de fundos entre as instituições financeiras é o',
           d.id, b.id, 2026, 'Sistema de Pagamentos Brasileiro', a.id,
           'O STR é o núcleo do SPB: liquida, uma a uma e em tempo real, as transferências entre contas de reservas dos bancos no Banco Central. As demais opções não são sistemas de liquidação.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistema de Pagamentos Brasileiro')
    WHERE d.nome = 'Conhecimentos Bancários'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Sistema Financeiro da Habitação (SFH).', FALSE, 1),
        ('Fundo Garantidor de Créditos (FGC).', FALSE, 2),
        ('Conselho de Controle de Atividades Financeiras (Coaf).', FALSE, 3),
        ('Sistema de Transferência de Reservas (STR).', TRUE, 4),
        ('Cadastro de Clientes do Sistema Financeiro (CCS).', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Pela Lei Complementar 179/2021, o presidente e os diretores do Banco Central têm mandatos fixos de quatro anos, não coincidentes com o mandato do Presidente da República.',
           d.id, b.id, 2025, 'Sistema Financeiro Nacional', a.id,
           'A lei da autonomia do Banco Central fixou mandatos de quatro anos, escalonados; o do presidente começa no terceiro ano do mandato do Presidente da República.',
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
    SELECT 'Depósitos em caderneta de poupança e aplicações em CDB são cobertos pelo Fundo Garantidor de Créditos, ao passo que as cotas de fundos de investimento não contam com essa garantia.',
           d.id, b.id, 2024, 'Garantias do sistema financeiro', a.id,
           'O FGC cobre depósitos e títulos de emissão das instituições associadas (poupança, CDB, LCI, LCA, entre outros). Fundos de investimento não são obrigação do banco e ficam fora da cobertura.',
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
    SELECT 'O Conselho de Controle de Atividades Financeiras (Coaf), unidade de inteligência financeira do Brasil, está vinculado administrativamente ao Banco Central do Brasil.',
           d.id, b.id, 2026, 'Prevenção à lavagem de dinheiro', a.id,
           'A Lei 13.974/2020 vinculou o Coaf administrativamente ao Banco Central, com autonomia técnica e operacional.',
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
    SELECT 'A revogação de ato administrativo produz efeitos retroativos (ex tunc) e pode ser feita pelo Poder Judiciário no exercício da função jurisdicional.',
           d.id, b.id, 2025, 'Anulação e revogação', a.id,
           'A revogação retira ato válido por conveniência e oportunidade, com efeitos ex nunc, e é privativa da Administração que o editou. O Judiciário, ao julgar, só anula atos ilegais.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Anulação e revogação')
    WHERE d.nome = 'Direito Administrativo'
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
    SELECT 'Por ser atividade típica de Estado, o poder de polícia não pode ter nenhuma de suas fases delegada a pessoa jurídica de direito privado.',
           d.id, b.id, 2026, 'Poderes administrativos', a.id,
           'O STF (Tema 532) admitiu a delegação a estatais de direito privado, de capital majoritariamente público, que prestem serviço público em regime não concorrencial. Só a fase de legislação (ordem de polícia) é indelegável.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Poderes administrativos')
    WHERE d.nome = 'Direito Administrativo'
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
    SELECT 'São modalidades de licitação previstas na Lei 14.133/2021:',
           d.id, b.id, 2025, 'Licitações', a.id,
           'Art. 28 da Lei 14.133/2021. Convite e tomada de preços deixaram de existir; credenciamento é procedimento auxiliar; dispensa e inexigibilidade são contratação direta, não modalidades.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Licitações')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('convite, tomada de preços, concorrência, concurso e leilão.', FALSE, 1),
        ('pregão, convite, concorrência, leilão e credenciamento.', FALSE, 2),
        ('concorrência, tomada de preços, pregão e dispensa.', FALSE, 3),
        ('concurso, leilão, inexigibilidade e diálogo competitivo.', FALSE, 4),
        ('pregão, concorrência, concurso, leilão e diálogo competitivo.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'As pessoas jurídicas de direito público respondem objetivamente pelos danos que seus agentes, nessa qualidade, causarem a terceiros, assegurado o direito de regresso contra o responsável nos casos de dolo ou culpa.',
           d.id, b.id, 2024, 'Responsabilidade civil do Estado', a.id,
           'Art. 37, § 6º, da CF. A vítima não precisa provar culpa do Estado; a culpa ou o dolo do agente só importam na ação de regresso.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Responsabilidade civil do Estado')
    WHERE d.nome = 'Direito Administrativo'
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
    SELECT 'São estáveis após três anos de efetivo exercício os servidores nomeados para cargo de provimento efetivo em virtude de concurso público.',
           d.id, b.id, 2025, 'Servidores públicos', a.id,
           'Art. 41 da CF, com a redação da EC 19/1998. A aquisição da estabilidade depende também de avaliação especial de desempenho por comissão.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Servidores públicos')
    WHERE d.nome = 'Direito Administrativo'
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
    SELECT 'Para o conjunto de dados {2, 4, 4, 5, 10}, a média, a mediana e a moda são, respectivamente,',
           d.id, b.id, 2025, 'Medidas de tendência central', a.id,
           'Média = (2 + 4 + 4 + 5 + 10) / 5 = 25 / 5 = 5. Mediana: valor central dos dados ordenados, 4. Moda: valor mais frequente, 4.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Medidas de tendência central')
    WHERE d.nome = 'Estatística'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('5, 4 e 4.', TRUE, 1),
        ('4, 4 e 5.', FALSE, 2),
        ('4, 5 e 4.', FALSE, 3),
        ('5, 5 e 4.', FALSE, 4),
        ('5, 4 e 10.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Somar uma mesma constante a todos os valores de um conjunto de dados altera a média, mas não altera a variância.',
           d.id, b.id, 2026, 'Medidas de dispersão', a.id,
           'A média é deslocada pela constante, mas os desvios em relação à nova média continuam os mesmos, e a variância (média dos quadrados dos desvios) não muda.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Medidas de dispersão')
    WHERE d.nome = 'Estatística'
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
    SELECT 'No lançamento de duas moedas honestas, a probabilidade de se obter pelo menos uma cara é igual a 3/4.',
           d.id, b.id, 2024, 'Probabilidade', a.id,
           'O complemento é sair coroa nas duas: 1/2 × 1/2 = 1/4. Logo, P(pelo menos uma cara) = 1 − 1/4 = 3/4.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Probabilidade')
    WHERE d.nome = 'Estatística'
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
  AND d.nome IN ('Conhecimentos Bancários', 'Direito Administrativo', 'Economia', 'Estatística')
  AND NOT EXISTS (SELECT 1 FROM cargo_disciplina x WHERE x.cargo_id = cc.id AND x.disciplina_id = d.id);

-- Recalcula o total de topicos so onde ele ja era contado (NULL = catalogo, fica NULL).
UPDATE cargo_disciplina cd
SET total_topicos = (SELECT COUNT(*) FROM assunto a WHERE a.disciplina_id = cd.disciplina_id)
WHERE cd.total_topicos IS NOT NULL;

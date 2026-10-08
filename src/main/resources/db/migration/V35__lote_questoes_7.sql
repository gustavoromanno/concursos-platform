-- Lote 7 de questoes AUTORAIS, gerado por scripts/gerar_questoes.py.
-- Nao edite a mao: altere o script e gere uma nova versao (V16, V17...).
--
-- Questoes escritas no estilo das bancas, nao copiadas de provas (o texto
-- das provas e protegido por direito autoral das bancas).

-- Disciplinas novas

-- Assuntos (so cria os que ainda nao existem)
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Vozes verbais' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Vozes verbais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Orações subordinadas' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Orações subordinadas') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Coesão referencial' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Coesão referencial') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Classes de palavras' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Classes de palavras') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Tipologia textual' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Tipologia textual') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Reescrita de frases' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Reescrita de frases') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Semântica' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Semântica') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Lógica de argumentação' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Lógica de argumentação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Proposições e conectivos' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Proposições e conectivos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Conjuntos' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Conjuntos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Probabilidade' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Probabilidade') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Análise combinatória' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Análise combinatória') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Equivalências lógicas' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Equivalências lógicas') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Mercado de câmbio' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Mercado de câmbio') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Produtos e serviços bancários' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Produtos e serviços bancários') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Sistema Financeiro Nacional' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Sistema Financeiro Nacional') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Prevenção à lavagem de dinheiro' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Prevenção à lavagem de dinheiro') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Política monetária' FROM disciplina d WHERE d.nome = 'Conhecimentos Bancários'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Política monetária') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Inflação' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Inflação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Política fiscal' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Política fiscal') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Desemprego' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Desemprego') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Moeda' FROM disciplina d WHERE d.nome = 'Economia'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Moeda') AND a.disciplina_id = d.id);

-- Questoes
WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Na frase "O edital foi publicado pelo órgão ontem", a forma verbal está na voz passiva analítica, e sua transposição para a voz ativa resulta em "O órgão publicou o edital ontem".',
           d.id, b.id, 2025, 'Vozes verbais', a.id,
           'Voz passiva analítica: verbo ser + particípio, com agente da passiva ("pelo órgão"). Na ativa, o agente vira sujeito e o sujeito paciente vira objeto direto, mantendo o tempo verbal (pretérito perfeito).',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Vozes verbais')
    WHERE d.nome = 'Língua Portuguesa'
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
    SELECT 'Em "Vendem-se apostilas usadas", o verbo deveria estar no singular, pois o sujeito da oração é indeterminado.',
           d.id, b.id, 2026, 'Vozes verbais', a.id,
           'É voz passiva sintética: "se" é pronome apassivador e "apostilas usadas" é o sujeito, com o qual o verbo concorda. Equivale a "Apostilas usadas são vendidas".',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Vozes verbais')
    WHERE d.nome = 'Língua Portuguesa'
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
    SELECT 'Em "Embora estivesse cansado, o candidato terminou a prova", a oração introduzida por "embora" expressa ideia de concessão.',
           d.id, b.id, 2025, 'Orações subordinadas', a.id,
           '"Embora" introduz oração subordinada adverbial concessiva: o fato (o cansaço) poderia impedir a ação principal, mas não impede.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Orações subordinadas')
    WHERE d.nome = 'Língua Portuguesa'
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
    SELECT 'Na frase "Os candidatos que estudaram foram aprovados", se a oração "que estudaram" fosse isolada por vírgulas, o período passaria a indicar que todos os candidatos estudaram.',
           d.id, b.id, 2024, 'Orações subordinadas', a.id,
           'Sem vírgulas, a oração adjetiva é restritiva: só os candidatos que estudaram foram aprovados. Entre vírgulas, vira explicativa e atribui a característica a todos os candidatos.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Orações subordinadas')
    WHERE d.nome = 'Língua Portuguesa'
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
    SELECT 'No período "O servidor apresentou o relatório ao diretor, que o aprovou sem ressalvas", o pronome "o" em "o aprovou" retoma',
           d.id, b.id, 2025, 'Coesão referencial', a.id,
           '"Que" retoma "o diretor" (sujeito de "aprovou"), e o pronome oblíquo "o" é o objeto direto, retomando "o relatório", aquilo que foi aprovado.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Coesão referencial')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('o relatório.', TRUE, 1),
        ('a apresentação.', FALSE, 2),
        ('as ressalvas.', FALSE, 3),
        ('o diretor.', FALSE, 4),
        ('o servidor.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Em "Ela chegou meio cansada", a palavra "meio" é advérbio e, por isso, permanece invariável; seria incorreto escrever "Ela chegou meia cansada".',
           d.id, b.id, 2026, 'Classes de palavras', a.id,
           'Como advérbio (equivale a "um pouco"), "meio" não varia. Varia apenas como numeral ou adjetivo: "meia hora", "meia porção".',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Classes de palavras')
    WHERE d.nome = 'Língua Portuguesa'
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
    SELECT 'Em "Este é o motivo por que desisti", a grafia separada de "por que" está correta, pois a expressão equivale a "pelo qual".',
           d.id, b.id, 2025, 'Classes de palavras', a.id,
           '"Por que" separado é preposição + pronome relativo ("pelo qual") ou aparece em perguntas. "Porque" junto é conjunção explicativa ou causal.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Classes de palavras')
    WHERE d.nome = 'Língua Portuguesa'
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
    SELECT 'O texto predominantemente dissertativo-argumentativo caracteriza-se por',
           d.id, b.id, 2024, 'Tipologia textual', a.id,
           'A argumentação tem tese e argumentos. Relato com personagens é narração; caracterização é descrição; instruções passo a passo formam o texto injuntivo.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Tipologia textual')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('orientar o leitor, passo a passo, a executar um procedimento.', FALSE, 1),
        ('defender um ponto de vista com argumentos, para convencer o leitor.', TRUE, 2),
        ('caracterizar seres, objetos e ambientes por meio de detalhes sensoriais.', FALSE, 3),
        ('relatar fatos em sequência temporal, com personagens e enredo.', FALSE, 4),
        ('reproduzir diálogos entre personagens, sem a voz do autor.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A reescrita de "Caso o candidato se atrase, não poderá entrar na sala" como "Se o candidato se atrasar, não poderá entrar na sala" preserva o sentido e a correção gramatical do período.',
           d.id, b.id, 2025, 'Reescrita de frases', a.id,
           '"Caso" e "se" são conjunções condicionais. A troca exige ajustar o verbo: "caso" pede presente do subjuntivo (atrase); "se", futuro do subjuntivo (atrasar).',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Reescrita de frases')
    WHERE d.nome = 'Língua Portuguesa'
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
    SELECT 'No período "O resultado foi ratificado pela banca", a substituição de "ratificado" por "retificado" manteria o sentido original.',
           d.id, b.id, 2026, 'Semântica', a.id,
           'São parônimos: ratificar é confirmar; retificar é corrigir. A troca altera o sentido: o resultado deixaria de ser confirmado e passaria a ser corrigido.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Semântica')
    WHERE d.nome = 'Língua Portuguesa'
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
    SELECT 'O argumento "Todo servidor é concursado. Paulo é concursado. Logo, Paulo é servidor" é válido.',
           d.id, b.id, 2025, 'Lógica de argumentação', a.id,
           'É a falácia da afirmação do consequente: ser concursado é condição necessária para ser servidor, não suficiente. Paulo pode ser concursado sem ser servidor.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Lógica de argumentação')
    WHERE d.nome = 'Raciocínio Lógico'
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
    SELECT 'A proposição condicional "p → q" é falsa somente quando p é verdadeira e q é falsa.',
           d.id, b.id, 2024, 'Proposições e conectivos', a.id,
           'Na tabela-verdade da condicional, há um único caso falso: antecedente verdadeiro e consequente falso (V → F). Nos outros três, a condicional é verdadeira.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Proposições e conectivos')
    WHERE d.nome = 'Raciocínio Lógico'
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
    SELECT 'Em uma turma de 40 alunos, 25 estudam Português, 20 estudam Matemática e 8 estudam as duas disciplinas. O número de alunos que não estudam nenhuma das duas é',
           d.id, b.id, 2025, 'Conjuntos', a.id,
           'União = 25 + 20 − 8 = 37 alunos estudam ao menos uma disciplina. Logo, 40 − 37 = 3 não estudam nenhuma.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Conjuntos')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('8.', FALSE, 1),
        ('5.', FALSE, 2),
        ('3.', TRUE, 3),
        ('11.', FALSE, 4),
        ('0.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'De uma urna com 3 bolas brancas e 2 pretas, retiram-se duas bolas, sem reposição. A probabilidade de ambas serem brancas é igual a 3/10.',
           d.id, b.id, 2026, 'Probabilidade', a.id,
           'P = 3/5 × 2/4 = 6/20 = 3/10. Sem reposição, a segunda retirada tem uma bola branca e uma bola a menos na urna.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Probabilidade')
    WHERE d.nome = 'Raciocínio Lógico'
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
    SELECT 'O número de maneiras distintas de 4 pessoas se sentarem em torno de uma mesa circular é',
           d.id, b.id, 2024, 'Análise combinatória', a.id,
           'Permutação circular: (n − 1)! = 3! = 6. Rotações da mesma disposição não contam como arranjos diferentes.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Análise combinatória')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('16.', FALSE, 1),
        ('12.', FALSE, 2),
        ('4.', FALSE, 3),
        ('6.', TRUE, 4),
        ('24.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A proposição "Se não estudo, então não passo" é logicamente equivalente a "Se passo, então estudo".',
           d.id, b.id, 2025, 'Equivalências lógicas', a.id,
           'É a contrapositiva: ~E → ~P equivale a P → E (inverte-se a ordem e negam-se as duas proposições).',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Equivalências lógicas')
    WHERE d.nome = 'Raciocínio Lógico'
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
    SELECT 'No regime de câmbio flutuante adotado pelo Brasil, o Banco Central pode intervir no mercado, por meio de leilões de moeda estrangeira ou de swaps cambiais, para reduzir a volatilidade da taxa de câmbio.',
           d.id, b.id, 2025, 'Mercado de câmbio', a.id,
           'O câmbio é flutuante, mas não livre de intervenção: o Banco Central atua para suavizar oscilações excessivas, sem fixar um patamar para a taxa.',
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

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O certificado de depósito bancário (CDB) é título de renda fixa emitido por bancos para captar recursos e pode ter remuneração prefixada ou pós-fixada.',
           d.id, b.id, 2026, 'Produtos e serviços bancários', a.id,
           'No CDB o investidor empresta ao banco. A remuneração pode ser prefixada, pós-fixada (atrelada ao CDI, por exemplo) ou híbrida, com parte prefixada e parte indexada.',
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
    SELECT 'A autarquia responsável por fiscalizar e supervisionar as entidades fechadas de previdência complementar (fundos de pensão) é a',
           d.id, b.id, 2024, 'Sistema Financeiro Nacional', a.id,
           'A Previc supervisiona os fundos de pensão. A Susep cuida de seguros, capitalização e previdência aberta; a CVM, do mercado de valores mobiliários.',
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
        ('Agência Nacional de Saúde Suplementar (ANS).', FALSE, 1),
        ('Secretaria do Tesouro Nacional.', FALSE, 2),
        ('Superintendência de Seguros Privados (Susep).', FALSE, 3),
        ('Comissão de Valores Mobiliários (CVM).', FALSE, 4),
        ('Superintendência Nacional de Previdência Complementar (Previc).', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'As instituições financeiras devem comunicar ao Coaf as operações com indícios de lavagem de dinheiro, sem dar ciência dessa comunicação ao cliente envolvido.',
           d.id, b.id, 2025, 'Prevenção à lavagem de dinheiro', a.id,
           'Lei 9.613/1998, art. 11: a comunicação é obrigatória e sigilosa; avisar o cliente frustraria a apuração.',
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
    SELECT 'O redesconto é a operação pela qual o Banco Central concede assistência financeira de liquidez às instituições financeiras.',
           d.id, b.id, 2026, 'Política monetária', a.id,
           'O Banco Central atua como emprestador de última instância. Encarecer o redesconto desestimula esses empréstimos e reduz a liquidez; baratear tem o efeito oposto.',
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
    SELECT 'A inflação de custos ocorre quando o aumento generalizado dos preços decorre do excesso de demanda agregada em relação à capacidade produtiva da economia.',
           d.id, b.id, 2025, 'Inflação', a.id,
           'Esse é o conceito de inflação de demanda. A inflação de custos vem do lado da oferta: alta de salários, matérias-primas, energia ou câmbio que as empresas repassam aos preços.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Inflação')
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
    SELECT 'Há superávit primário quando as receitas do governo superam as despesas, excluídos os juros da dívida pública.',
           d.id, b.id, 2024, 'Política fiscal', a.id,
           'O resultado primário desconsidera os juros. Incluídos os juros, tem-se o resultado nominal, que no Brasil costuma ser deficitário mesmo com superávit primário.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Política fiscal')
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
    SELECT 'O trabalhador que perde o emprego porque sua função foi extinta pela adoção de uma nova tecnologia no setor em que atuava está em situação de desemprego',
           d.id, b.id, 2026, 'Desemprego', a.id,
           'O desemprego estrutural decorre de mudanças na estrutura produtiva que tornam certas qualificações obsoletas. O friccional é a transição entre empregos; o cíclico acompanha as recessões; o sazonal, épocas do ano.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Desemprego')
    WHERE d.nome = 'Economia'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('estrutural.', TRUE, 1),
        ('voluntário.', FALSE, 2),
        ('cíclico.', FALSE, 3),
        ('friccional.', FALSE, 4),
        ('sazonal.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A moeda desempenha as funções de meio de troca, unidade de conta e reserva de valor.',
           d.id, b.id, 2025, 'Moeda', a.id,
           'São as três funções clássicas: facilita as trocas, serve de medida comum de valor e permite transferir poder de compra no tempo (função prejudicada por inflação alta).',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Moeda')
    WHERE d.nome = 'Economia'
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
  AND d.nome IN ('Conhecimentos Bancários', 'Economia', 'Língua Portuguesa', 'Raciocínio Lógico')
  AND NOT EXISTS (SELECT 1 FROM cargo_disciplina x WHERE x.cargo_id = cc.id AND x.disciplina_id = d.id);

-- Recalcula o total de topicos so onde ele ja era contado (NULL = catalogo, fica NULL).
UPDATE cargo_disciplina cd
SET total_topicos = (SELECT COUNT(*) FROM assunto a WHERE a.disciplina_id = cd.disciplina_id)
WHERE cd.total_topicos IS NOT NULL;

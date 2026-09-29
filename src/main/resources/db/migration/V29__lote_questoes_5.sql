-- Lote 5 de questoes AUTORAIS, gerado por scripts/gerar_questoes.py.
-- Nao edite a mao: altere o script e gere uma nova versao (V16, V17...).
--
-- Questoes escritas no estilo das bancas, nao copiadas de provas (o texto
-- das provas e protegido por direito autoral das bancas).

-- Disciplinas novas
INSERT INTO disciplina (nome) SELECT 'Ética no Serviço Público'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Ética no Serviço Público');

-- Assuntos (so cria os que ainda nao existem)
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Código de Ética (Decreto 1.171/1994)' FROM disciplina d WHERE d.nome = 'Ética no Serviço Público'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Código de Ética (Decreto 1.171/1994)') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Comissões de Ética' FROM disciplina d WHERE d.nome = 'Ética no Serviço Público'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Comissões de Ética') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Crase' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Crase') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Concordância verbal' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Concordância verbal') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Regência verbal' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Regência verbal') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Ortografia' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Ortografia') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Semântica' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Semântica') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Pontuação' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Pontuação') AND a.disciplina_id = d.id);

-- Questoes
WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Segundo o Código de Ética Profissional do Servidor Público Civil do Poder Executivo Federal, a pena aplicável ao servidor pela Comissão de Ética é a de',
           d.id, b.id, 2024, 'Código de Ética (Decreto 1.171/1994)', a.id,
           'Decreto 1.171/1994, Capítulo II, XXII: a pena aplicável pela Comissão de Ética é a censura, fundamentada no parecer assinado por todos os integrantes, com ciência do faltoso. Advertência, suspensão e demissão são penas disciplinares da Lei 8.112/1990, aplicadas em outro processo.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Código de Ética (Decreto 1.171/1994)')
    WHERE d.nome = 'Ética no Serviço Público'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('censura.', TRUE, 1),
        ('multa.', FALSE, 2),
        ('suspensão.', FALSE, 3),
        ('advertência.', FALSE, 4),
        ('demissão.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Para fins de apuração do comprometimento ético, o Código de Ética do servidor do Executivo federal considera servidor público',
           d.id, b.id, 2023, 'Código de Ética (Decreto 1.171/1994)', a.id,
           'Decreto 1.171/1994, XXIV: o conceito é amplo e alcança quem presta serviço ao Estado, ainda que transitoriamente e sem retribuição financeira, desde que ligado a órgão ou entidade pública.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Código de Ética (Decreto 1.171/1994)')
    WHERE d.nome = 'Ética no Serviço Público'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('somente quem recebe remuneração dos cofres públicos.', FALSE, 1),
        ('todo aquele que, por força de lei, contrato ou qualquer ato jurídico, preste serviços de natureza permanente, temporária ou excepcional, ainda que sem retribuição financeira.', TRUE, 2),
        ('apenas o ocupante de cargo em comissão ou função de confiança.', FALSE, 3),
        ('apenas o servidor estável, após o estágio probatório.', FALSE, 4),
        ('apenas o ocupante de cargo de provimento efetivo.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Salvo nos casos de segurança nacional, investigações policiais ou interesse superior do Estado, a publicidade de qualquer ato administrativo constitui requisito de eficácia e moralidade, e sua omissão configura comprometimento ético contra o bem comum.',
           d.id, b.id, 2025, 'Código de Ética (Decreto 1.171/1994)', a.id,
           'É a regra deontológica VII do Decreto 1.171/1994. As exceções dependem de processo previamente declarado sigiloso, nos termos da lei.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Código de Ética (Decreto 1.171/1994)')
    WHERE d.nome = 'Ética no Serviço Público'
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
    SELECT 'Deixar o cidadão à espera de solução que compete ao seu setor, permitindo a formação de longas filas, é atitude contra a ética e causa grave dano moral aos usuários dos serviços públicos.',
           d.id, b.id, 2024, 'Código de Ética (Decreto 1.171/1994)', a.id,
           'Regra deontológica X: além de atitude antiética e ato de desumanidade, o atraso injustificado é principalmente grave dano moral aos usuários.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Código de Ética (Decreto 1.171/1994)')
    WHERE d.nome = 'Ética no Serviço Público'
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
    SELECT 'O servidor pode omitir a verdade quando ela for contrária aos interesses da própria Administração Pública.',
           d.id, b.id, 2023, 'Código de Ética (Decreto 1.171/1994)', a.id,
           'Regra deontológica VIII: o servidor não pode omitir nem falsear a verdade, ainda que contrária aos interesses da própria pessoa interessada ou da Administração.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Código de Ética (Decreto 1.171/1994)')
    WHERE d.nome = 'Ética no Serviço Público'
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
    SELECT 'É vedado ao servidor público usar do cargo ou função, facilidades, amizades, tempo, posição e influências para obter qualquer favorecimento, para si ou para outrem.',
           d.id, b.id, 2025, 'Código de Ética (Decreto 1.171/1994)', a.id,
           'Está entre as vedações do Capítulo I, Seção III, XV, alínea a, do Decreto 1.171/1994.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Código de Ética (Decreto 1.171/1994)')
    WHERE d.nome = 'Ética no Serviço Público'
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
    SELECT 'A pena de censura aplicada pela Comissão de Ética deve constar de parecer fundamentado, assinado por todos os seus integrantes, com ciência do faltoso.',
           d.id, b.id, 2024, 'Comissões de Ética', a.id,
           'Decreto 1.171/1994, XXII. A censura é a única penalidade do Código de Ética.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Comissões de Ética')
    WHERE d.nome = 'Ética no Serviço Público'
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
    SELECT 'A Comissão de Ética pode aplicar ao servidor a pena de demissão quando a falta ética for grave.',
           d.id, b.id, 2023, 'Comissões de Ética', a.id,
           'A Comissão de Ética só aplica censura. A demissão é penalidade disciplinar da Lei 8.112/1990, aplicada em processo administrativo disciplinar.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Comissões de Ética')
    WHERE d.nome = 'Ética no Serviço Público'
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
    SELECT 'Na frase "Fui à Curitiba para a prova", o uso do acento grave está de acordo com a norma-padrão.',
           d.id, b.id, 2024, 'Crase', a.id,
           'Curitiba não admite artigo ("venho de Curitiba", e não "da Curitiba"), então não há crase: "Fui a Curitiba". Compare com "Fui à Bahia" ("venho da Bahia").',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Crase')
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
    SELECT 'A frase "Fazem dez anos que ele estuda para concursos" está correta, pois o verbo concorda com "dez anos".',
           d.id, b.id, 2025, 'Concordância verbal', a.id,
           'O verbo "fazer" indicando tempo decorrido é impessoal e fica no singular: "Faz dez anos".',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Concordância verbal')
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
    SELECT 'No sentido de acarretar, o verbo "implicar" é transitivo direto, como em "A mudança implica novos custos".',
           d.id, b.id, 2023, 'Regência verbal', a.id,
           'Nesse sentido, a norma-padrão pede objeto direto; a construção "implica em" é considerada desvio.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Regência verbal')
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
    SELECT '"A fim de" indica finalidade, enquanto "afim" significa semelhante ou que tem afinidade.',
           d.id, b.id, 2024, 'Ortografia', a.id,
           'Ex.: "Estudou a fim de passar" (finalidade); "disciplinas afins" (semelhantes).',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Ortografia')
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
    SELECT 'Em "Embora estivesse cansado, continuou estudando", a conjunção "embora" introduz ideia de concessão.',
           d.id, b.id, 2025, 'Semântica', a.id,
           'Concessão é um fato que poderia impedir o outro, mas não impede. Outras conjunções concessivas: ainda que, mesmo que, conquanto.',
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
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Assinale a frase corretamente pontuada.',
           d.id, b.id, 2024, 'Pontuação', a.id,
           'Não se separa por vírgula o sujeito ("os candidatos aprovados") do verbo, nem o verbo de seus complementos. A última opção separa, sem motivo, uma circunstância de tempo breve e no fim da oração.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Pontuação')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Os candidatos, aprovados serão convocados em março.', FALSE, 1),
        ('Os candidatos aprovados, serão convocados em março.', FALSE, 2),
        ('Os candidatos aprovados serão convocados em março.', TRUE, 3),
        ('Os candidatos aprovados serão convocados, em março.', FALSE, 4),
        ('Os candidatos aprovados serão, convocados em março.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Assinale a frase em que o termo destacado está empregado corretamente.',
           d.id, b.id, 2023, 'Ortografia', a.id,
           '"Mal" é advérbio (oposto de bem): comportou-se mal, falou mal. "Mau" é adjetivo (oposto de bom): mau candidato, mau hábito. "Mal-entendidos" tem hífen e "mal-humorado" também.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Ortografia')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Ele é um MAL candidato.', FALSE, 1),
        ('Há MAL entendidos no edital.', FALSE, 2),
        ('Ele falou MAU da banca examinadora.', FALSE, 3),
        ('Ele se comportou MAL durante a entrevista.', TRUE, 4),
        ('Ele tem um MAU hábito de chegar MAU humorado.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Assinale a frase em que "há" e "a" estão empregados corretamente.',
           d.id, b.id, 2025, 'Ortografia', a.id,
           '"Há" (verbo haver) indica tempo passado: há dois anos. "A" (preposição) indica tempo futuro ou distância: daqui a um mês.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Ortografia')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Estudo para concursos a dois anos e farei a prova daqui há um mês.', FALSE, 1),
        ('Estudo para concursos a dois anos e farei a prova daqui a um mês.', FALSE, 2),
        ('Estudo para concursos à dois anos e farei a prova daqui à um mês.', FALSE, 3),
        ('Estudo para concursos há dois anos e farei a prova daqui há um mês.', FALSE, 4),
        ('Estudo para concursos há dois anos e farei a prova daqui a um mês.', TRUE, 5)
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

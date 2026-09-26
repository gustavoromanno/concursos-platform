-- Lote 3 de questoes AUTORAIS, gerado por scripts/gerar_questoes.py.
-- Nao edite a mao: altere o script e gere uma nova versao (V16, V17...).
--
-- Questoes escritas no estilo das bancas, nao copiadas de provas (o texto
-- das provas e protegido por direito autoral das bancas).

-- Disciplinas novas

-- Assuntos (so cria os que ainda nao existem)
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Colocação pronominal' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Colocação pronominal') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Regência verbal' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Regência verbal') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Concordância nominal' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Concordância nominal') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Ortografia' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Ortografia') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Semântica' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Semântica') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Acentuação gráfica' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Acentuação gráfica') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Atos administrativos' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Atos administrativos') AND a.disciplina_id = d.id);
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
SELECT d.id, 'Improbidade administrativa' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Improbidade administrativa') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Anulação e revogação' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Anulação e revogação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Servidores públicos' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Servidores públicos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Organização administrativa' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Organização administrativa') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Processo administrativo' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Processo administrativo') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Remédios constitucionais' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Remédios constitucionais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Poder Legislativo' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Poder Legislativo') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Direitos fundamentais' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Direitos fundamentais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Planilhas eletrônicas' FROM disciplina d WHERE d.nome = 'Noções de Informática'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Planilhas eletrônicas') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Arquivos e pastas' FROM disciplina d WHERE d.nome = 'Noções de Informática'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Arquivos e pastas') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Redes e internet' FROM disciplina d WHERE d.nome = 'Noções de Informática'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Redes e internet') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Segurança da informação' FROM disciplina d WHERE d.nome = 'Noções de Informática'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Segurança da informação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Hardware e sistemas operacionais' FROM disciplina d WHERE d.nome = 'Noções de Informática'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Hardware e sistemas operacionais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Porcentagem' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Porcentagem') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Sequências' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Sequências') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Proposições e conectivos' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Proposições e conectivos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Equivalências lógicas' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Equivalências lógicas') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Análise combinatória' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Análise combinatória') AND a.disciplina_id = d.id);

-- Questoes
WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Assinale a frase em que a colocação do pronome oblíquo átono está de acordo com a norma-padrão.',
           d.id, b.id, 2024, 'Colocação pronominal', a.id,
           'Palavras negativas atraem o pronome (próclise): "não me informaram". A norma-padrão não inicia período com pronome oblíquo átono, não admite ênclise ao particípio e, no futuro do presente, pede mesóclise ("informar-lhe-ei") ou próclise.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Colocação pronominal')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Não me informaram o resultado.', TRUE, 1),
        ('Informarei-lhe o resultado amanhã.', FALSE, 2),
        ('Não informaram-me o resultado.', FALSE, 3),
        ('Tinha informado-me o resultado.', FALSE, 4),
        ('Me informaram o resultado ontem.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Assinale a frase em que a regência verbal está de acordo com a norma-padrão.',
           d.id, b.id, 2023, 'Regência verbal', a.id,
           'No sentido de ver, "assistir" pede a preposição "a" (assistir à palestra). "Preferir" rege "a" (preferir algo a algo); "aspirar", no sentido de desejar, e "obedecer" pedem "a"; "esquecer" sem pronome pede objeto direto ("esqueci o prazo").',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Regência verbal')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Aspiro o cargo de analista.', FALSE, 1),
        ('Os servidores assistiram à palestra do diretor.', TRUE, 2),
        ('Prefiro trabalhar do que estudar.', FALSE, 3),
        ('Esqueci do prazo de entrega.', FALSE, 4),
        ('Obedeça o regulamento interno.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Assinale a frase em que a concordância nominal está correta.',
           d.id, b.id, 2025, 'Concordância nominal', a.id,
           'Com o artigo ("a entrada"), o predicativo concorda: "é proibida". Sem artigo, ficaria "é proibido entrada". "Anexo" e "mesmo" são adjetivos e concordam ("anexos", "mesma"); "bastante" como adjetivo vai ao plural ("bastantes pessoas").',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Concordância nominal')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Havia bastante pessoas na fila.', FALSE, 1),
        ('É proibido a entrada de pessoas estranhas.', FALSE, 2),
        ('É proibida a entrada de pessoas estranhas.', TRUE, 3),
        ('Ela mesmo resolveu o problema.', FALSE, 4),
        ('Seguem anexo os documentos solicitados.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Assinale a frase em que a forma do "porquê" está empregada corretamente.',
           d.id, b.id, 2024, 'Ortografia', a.id,
           '"Por que" separado e sem acento aparece em perguntas diretas e indiretas ("não sei por que"). "Porque" junto indica causa; "por quê" vai no fim da frase; "porquê" com acento é substantivo ("o porquê").',
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
        ('Você faltou porquê?', FALSE, 1),
        ('Ele faltou por que estava doente.', FALSE, 2),
        ('Ninguém entendeu o por quê da decisão.', FALSE, 3),
        ('Não sei por que ele faltou à reunião.', TRUE, 4),
        ('Por quê você faltou à reunião?', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No período "Estudou durante meses; contudo, não foi aprovado", o conectivo "contudo" estabelece relação de',
           d.id, b.id, 2023, 'Semântica', a.id,
           '"Contudo" é conjunção adversativa, como "mas", "porém" e "entretanto": introduz uma ideia que contraria a expectativa criada pela anterior.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Semântica')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('conclusão.', FALSE, 1),
        ('condição.', FALSE, 2),
        ('causa.', FALSE, 3),
        ('finalidade.', FALSE, 4),
        ('oposição.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Assinale a palavra grafada de acordo com o Acordo Ortográfico vigente.',
           d.id, b.id, 2025, 'Acentuação gráfica', a.id,
           'O Acordo eliminou o acento dos ditongos abertos "ei" e "oi" em paroxítonas (ideia, heroico, assembleia), o circunflexo de "oo" (voo) e o acento diferencial de "para" (verbo).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Acentuação gráfica')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('ideia', TRUE, 1),
        ('assembléia', FALSE, 2),
        ('pára (verbo parar)', FALSE, 3),
        ('heróico', FALSE, 4),
        ('vôo', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O atributo do ato administrativo que permite à Administração executar suas próprias decisões, sem necessidade de autorização prévia do Poder Judiciário, é a',
           d.id, b.id, 2024, 'Atos administrativos', a.id,
           'Autoexecutoriedade é executar diretamente a decisão. Imperatividade é impor obrigações independentemente da concordância do particular; presunção de legitimidade é a presunção de que o ato é válido até prova em contrário; tipicidade é a correspondência com figuras previstas em lei. Motivação não é atributo, e sim requisito de forma.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Atos administrativos')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('tipicidade.', FALSE, 1),
        ('autoexecutoriedade.', TRUE, 2),
        ('imperatividade.', FALSE, 3),
        ('motivação.', FALSE, 4),
        ('presunção de legitimidade.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O poder conferido à Administração para condicionar e restringir o uso e o gozo de bens, atividades e direitos individuais em benefício do interesse público é o poder',
           d.id, b.id, 2023, 'Poderes administrativos', a.id,
           'Poder de polícia é limitar a liberdade individual em favor do interesse coletivo (fiscalização sanitária, de trânsito, de obras). O hierárquico organiza a estrutura interna; o disciplinar pune servidores e quem tem vínculo especial; o regulamentar edita normas para a fiel execução das leis.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Poderes administrativos')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('hierárquico.', FALSE, 1),
        ('vinculado.', FALSE, 2),
        ('de polícia.', TRUE, 3),
        ('disciplinar.', FALSE, 4),
        ('regulamentar.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'NÃO é modalidade de licitação prevista na Lei nº 14.133/2021:',
           d.id, b.id, 2025, 'Licitações', a.id,
           'A Lei 14.133/2021 prevê pregão, concorrência, concurso, leilão e diálogo competitivo. Tomada de preços e convite eram modalidades da antiga Lei 8.666/1993 e não existem na lei nova.',
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
        ('leilão.', FALSE, 1),
        ('pregão.', FALSE, 2),
        ('diálogo competitivo.', FALSE, 3),
        ('tomada de preços.', TRUE, 4),
        ('concorrência.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Nos termos do art. 37, § 6º, da Constituição Federal, as pessoas jurídicas de direito público respondem pelos danos que seus agentes, nessa qualidade, causarem a terceiros, de forma',
           d.id, b.id, 2024, 'Responsabilidade civil do Estado', a.id,
           'A responsabilidade do Estado é objetiva (teoria do risco administrativo): a vítima prova o dano e o nexo, sem precisar provar culpa. O Estado pode depois cobrar do agente, em ação de regresso, se ele agiu com dolo ou culpa.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Responsabilidade civil do Estado')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('objetiva, sem possibilidade de regresso contra o agente.', FALSE, 1),
        ('solidária com o agente, que responde diretamente perante a vítima.', FALSE, 2),
        ('subjetiva, com regresso automático contra o agente.', FALSE, 3),
        ('subjetiva, dependendo sempre da prova de culpa do agente.', FALSE, 4),
        ('objetiva, assegurado o direito de regresso contra o agente nos casos de dolo ou culpa.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Após as alterações promovidas pela Lei nº 14.230/2021, a configuração de ato de improbidade administrativa exige',
           d.id, b.id, 2024, 'Improbidade administrativa', a.id,
           'A Lei 14.230/2021 eliminou a improbidade culposa: todos os tipos exigem dolo, isto é, vontade livre e consciente de alcançar o resultado ilícito. Nem todo ato de improbidade depende de dano ao erário (há os de enriquecimento ilícito e os que atentam contra princípios).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Improbidade administrativa')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('dolo do agente.', TRUE, 1),
        ('culpa, em qualquer de suas modalidades.', FALSE, 2),
        ('apenas a ilegalidade do ato.', FALSE, 3),
        ('prejuízo ao erário, em todos os casos.', FALSE, 4),
        ('culpa grave do agente.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O Poder Judiciário, no exercício da função jurisdicional, pode revogar ato administrativo por razões de conveniência e oportunidade.',
           d.id, b.id, 2023, 'Anulação e revogação', a.id,
           'A revogação é privativa da Administração que praticou o ato, por ser juízo de mérito. No controle jurisdicional, o Judiciário só anula atos ilegais.',
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
    SELECT 'O servidor público estável só perderá o cargo em virtude de sentença judicial transitada em julgado, mediante processo administrativo em que lhe seja assegurada ampla defesa ou mediante procedimento de avaliação periódica de desempenho, na forma de lei complementar.',
           d.id, b.id, 2024, 'Servidores públicos', a.id,
           'É a redação do art. 41, § 1º, da CF. Há ainda a hipótese de excesso de despesa com pessoal (art. 169, § 4º).',
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
    SELECT 'As empresas públicas e as sociedades de economia mista integram a administração pública indireta.',
           d.id, b.id, 2025, 'Organização administrativa', a.id,
           'A administração indireta é formada por autarquias, fundações públicas, empresas públicas e sociedades de economia mista.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Organização administrativa')
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
    SELECT 'Na esfera federal, o direito da Administração de anular os atos administrativos de que decorram efeitos favoráveis para os destinatários decai em cinco anos, contados da data em que foram praticados, salvo comprovada má-fé.',
           d.id, b.id, 2023, 'Processo administrativo', a.id,
           'Art. 54 da Lei 9.784/1999. Com má-fé comprovada, não há o limite de cinco anos.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Processo administrativo')
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
    SELECT 'De acordo com a Constituição Federal, o mandado de segurança coletivo pode ser impetrado por',
           d.id, b.id, 2024, 'Remédios constitucionais', a.id,
           'Art. 5º, LXX: partido político com representação no Congresso Nacional e organização sindical, entidade de classe ou associação legalmente constituída e em funcionamento há pelo menos um ano, em defesa de seus membros ou associados.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Remédios constitucionais')
    WHERE d.nome = 'Direito Constitucional'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Ministério Público, exclusivamente.', FALSE, 1),
        ('partido político com representação no Congresso Nacional.', TRUE, 2),
        ('associação legalmente constituída e em funcionamento há pelo menos seis meses.', FALSE, 3),
        ('qualquer pessoa jurídica de direito privado.', FALSE, 4),
        ('qualquer cidadão.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A iniciativa popular de projeto de lei federal exige a apresentação à Câmara dos Deputados de projeto subscrito por, no mínimo,',
           d.id, b.id, 2025, 'Poder Legislativo', a.id,
           'Art. 61, § 2º, da CF: 1% do eleitorado nacional, distribuído por pelo menos cinco Estados, com não menos de 0,3% dos eleitores de cada um deles.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Poder Legislativo')
    WHERE d.nome = 'Direito Constitucional'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('dez por cento do eleitorado nacional, sem exigência de distribuição.', FALSE, 1),
        ('um por cento do eleitorado de cada Estado da Federação.', FALSE, 2),
        ('um por cento do eleitorado nacional, distribuído por pelo menos cinco Estados, com não menos de três décimos por cento dos eleitores de cada um deles.', TRUE, 3),
        ('cinco por cento do eleitorado nacional, distribuído por pelo menos nove Estados.', FALSE, 4),
        ('cem mil eleitores, independentemente da distribuição por Estados.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Os deputados federais são eleitos pelo sistema',
           d.id, b.id, 2023, 'Poder Legislativo', a.id,
           'Art. 45 da CF: a Câmara compõe-se de representantes do povo eleitos pelo sistema proporcional em cada Estado, Território e no DF. Os senadores são eleitos pelo princípio majoritário.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Poder Legislativo')
    WHERE d.nome = 'Direito Constitucional'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('distrital puro.', FALSE, 1),
        ('majoritário absoluto.', FALSE, 2),
        ('de lista fechada sem votação nominal.', FALSE, 3),
        ('proporcional.', TRUE, 4),
        ('majoritário simples.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'É livre a manifestação do pensamento, sendo vedado o anonimato.',
           d.id, b.id, 2024, 'Direitos fundamentais', a.id,
           'Art. 5º, IV, da CF. A vedação ao anonimato permite responsabilizar quem abusa da liberdade de expressão.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Direitos fundamentais')
    WHERE d.nome = 'Direito Constitucional'
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
    SELECT 'A lei penal não retroagirá, salvo para beneficiar o réu.',
           d.id, b.id, 2025, 'Direitos fundamentais', a.id,
           'Art. 5º, XL, da CF: a lei penal mais benéfica retroage, inclusive para alcançar fatos já julgados.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Direitos fundamentais')
    WHERE d.nome = 'Direito Constitucional'
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
    SELECT 'O habeas corpus é o remédio adequado para proteger o direito líquido e certo à obtenção de certidões em repartições públicas.',
           d.id, b.id, 2023, 'Remédios constitucionais', a.id,
           'O habeas corpus protege a liberdade de locomoção. O direito de certidão (art. 5º, XXXIV, b) é protegido por mandado de segurança.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Remédios constitucionais')
    WHERE d.nome = 'Direito Constitucional'
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
    SELECT 'Em uma planilha, a célula A1 contém o valor 6. O resultado da fórmula =SE(A1>=7;"Aprovado";"Reprovado") é',
           d.id, b.id, 2024, 'Planilhas eletrônicas', a.id,
           'A função SE testa a condição (6 >= 7 é falso) e devolve o terceiro argumento, "Reprovado". Com A1 igual ou maior que 7, devolveria "Aprovado".',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Planilhas eletrônicas')
    WHERE d.nome = 'Noções de Informática'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Aprovado', FALSE, 1),
        ('6', FALSE, 2),
        ('#VALOR!', FALSE, 3),
        ('7', FALSE, 4),
        ('Reprovado', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A extensão de arquivo padrão das planilhas criadas nas versões atuais do Microsoft Excel é',
           d.id, b.id, 2023, 'Arquivos e pastas', a.id,
           '.xlsx é a planilha do Excel; .docx é documento do Word; .pptx, apresentação do PowerPoint; .pdf e .txt são formatos de documento e texto simples.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Arquivos e pastas')
    WHERE d.nome = 'Noções de Informática'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('.xlsx', TRUE, 1),
        ('.pptx', FALSE, 2),
        ('.txt', FALSE, 3),
        ('.pdf', FALSE, 4),
        ('.docx', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No endereço de correio eletrônico fulano@orgao.gov.br, o trecho após o símbolo @ identifica',
           d.id, b.id, 2025, 'Redes e internet', a.id,
           'Antes do @ fica o nome da caixa postal (usuário); depois, o domínio que hospeda o serviço de e-mail. Protocolo e endereço IP não aparecem no endereço.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Redes e internet')
    WHERE d.nome = 'Noções de Informática'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('a senha de acesso criptografada.', FALSE, 1),
        ('o domínio do provedor ou da organização responsável pela caixa postal.', TRUE, 2),
        ('o endereço IP do computador do usuário.', FALSE, 3),
        ('o protocolo de envio utilizado.', FALSE, 4),
        ('o nome do usuário.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A computação em nuvem permite acessar arquivos armazenados remotamente a partir de diferentes dispositivos conectados à internet.',
           d.id, b.id, 2024, 'Redes e internet', a.id,
           'Na nuvem os dados ficam em servidores do provedor e podem ser acessados de qualquer dispositivo com conexão e credenciais.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Redes e internet')
    WHERE d.nome = 'Noções de Informática'
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
    SELECT 'O worm é um programa malicioso que depende da execução de um arquivo hospedeiro para se propagar.',
           d.id, b.id, 2025, 'Segurança da informação', a.id,
           'Quem depende de arquivo hospedeiro é o vírus. O worm se propaga sozinho, explorando falhas da rede e enviando cópias de si mesmo.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Segurança da informação')
    WHERE d.nome = 'Noções de Informática'
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
    SELECT 'No Windows, o atalho Ctrl + C copia o item selecionado para a área de transferência.',
           d.id, b.id, 2023, 'Hardware e sistemas operacionais', a.id,
           'Ctrl + C copia, Ctrl + X recorta e Ctrl + V cola o conteúdo da área de transferência.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Hardware e sistemas operacionais')
    WHERE d.nome = 'Noções de Informática'
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
    SELECT 'Um capital de R$ 1.000,00 foi aplicado a juros simples de 2% ao mês durante 5 meses. O valor dos juros obtidos é',
           d.id, b.id, 2024, 'Porcentagem', a.id,
           'Juros simples: J = C × i × t = 1.000 × 0,02 × 5 = R$ 100,00. O valor de R$ 104,08 seria o de juros compostos no mesmo período.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Porcentagem')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('R$ 200,00.', FALSE, 1),
        ('R$ 104,08.', FALSE, 2),
        ('R$ 100,00.', TRUE, 3),
        ('R$ 50,00.', FALSE, 4),
        ('R$ 110,00.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Na sequência 2, 6, 18, 54, ..., o próximo termo é',
           d.id, b.id, 2025, 'Sequências', a.id,
           'Cada termo é o anterior multiplicado por 3 (progressão geométrica de razão 3): 54 × 3 = 162.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sequências')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('150.', FALSE, 1),
        ('108.', FALSE, 2),
        ('72.', FALSE, 3),
        ('162.', TRUE, 4),
        ('216.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A tabela-verdade de uma proposição composta formada por três proposições simples distintas tem',
           d.id, b.id, 2023, 'Proposições e conectivos', a.id,
           'O número de linhas é 2 elevado ao número de proposições simples: 2³ = 8.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Proposições e conectivos')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('16 linhas.', FALSE, 1),
        ('3 linhas.', FALSE, 2),
        ('6 linhas.', FALSE, 3),
        ('9 linhas.', FALSE, 4),
        ('8 linhas.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A proposição "Se Pedro é médico, então Pedro é formado" é equivalente a "Pedro não é médico ou Pedro é formado".',
           d.id, b.id, 2024, 'Equivalências lógicas', a.id,
           'A condicional p → q equivale a ~p ∨ q: ela só é falsa quando p é verdadeira e q é falsa, exatamente como a disjunção ~p ∨ q.',
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
    SELECT 'Em um grupo de 5 pessoas, há 10 maneiras distintas de escolher um presidente e um vice-presidente, que devem ser pessoas diferentes.',
           d.id, b.id, 2023, 'Análise combinatória', a.id,
           'A ordem importa (presidente ≠ vice), então é arranjo: A(5,2) = 5 × 4 = 20. O valor 10 seria a combinação C(5,2), que ignora os cargos.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Análise combinatória')
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
    SELECT 'Se 30% de um valor correspondem a 60, então esse valor é 200.',
           d.id, b.id, 2025, 'Porcentagem', a.id,
           '0,30 × V = 60, logo V = 60 / 0,30 = 200.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Porcentagem')
    WHERE d.nome = 'Raciocínio Lógico'
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

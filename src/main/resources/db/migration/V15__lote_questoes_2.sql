-- Lote 2 de questoes AUTORAIS, gerado por scripts/gerar_questoes.py.
-- Nao edite a mao: altere o script e gere uma nova versao (V16, V17...).
--
-- Questoes escritas no estilo das bancas, nao copiadas de provas (o texto
-- das provas e protegido por direito autoral das bancas).

-- Disciplinas novas
INSERT INTO disciplina (nome) SELECT 'Direito Constitucional'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Direito Constitucional');
INSERT INTO disciplina (nome) SELECT 'Noções de Informática'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Noções de Informática');

-- Assuntos (so cria os que ainda nao existem)
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Remédios constitucionais' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Remédios constitucionais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Administração Pública na Constituição' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Administração Pública na Constituição') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Organização do Estado' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Organização do Estado') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Poder Legislativo' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Poder Legislativo') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Direitos fundamentais' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Direitos fundamentais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Controle de constitucionalidade' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Controle de constitucionalidade') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Nacionalidade' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Nacionalidade') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Direitos sociais' FROM disciplina d WHERE d.nome = 'Direito Constitucional'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Direitos sociais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Segurança da informação' FROM disciplina d WHERE d.nome = 'Noções de Informática'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Segurança da informação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Redes e internet' FROM disciplina d WHERE d.nome = 'Noções de Informática'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Redes e internet') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Planilhas eletrônicas' FROM disciplina d WHERE d.nome = 'Noções de Informática'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Planilhas eletrônicas') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Hardware e sistemas operacionais' FROM disciplina d WHERE d.nome = 'Noções de Informática'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Hardware e sistemas operacionais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Negação de proposições' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Negação de proposições') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Equivalências lógicas' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Equivalências lógicas') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Porcentagem' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Porcentagem') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Análise combinatória' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Análise combinatória') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Probabilidade' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Probabilidade') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Proposições e conectivos' FROM disciplina d WHERE d.nome = 'Raciocínio Lógico'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Proposições e conectivos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Crase' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Crase') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Concordância verbal' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Concordância verbal') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Pontuação' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Pontuação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Princípios da Administração Pública' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Princípios da Administração Pública') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Organização administrativa' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Organização administrativa') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Anulação e revogação' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Anulação e revogação') AND a.disciplina_id = d.id);

-- Questoes
WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O remédio constitucional cabível para assegurar o conhecimento de informações relativas à pessoa do impetrante, constantes de registros ou bancos de dados de entidades governamentais ou de caráter público, é o',
           d.id, b.id, 2024, 'Remédios constitucionais', a.id,
           'Art. 5º, LXXII, da CF: o habeas data assegura o conhecimento e a retificação de informações sobre a pessoa do impetrante em registros públicos. O mandado de injunção supre a falta de norma regulamentadora; o mandado de segurança protege direito líquido e certo não amparado por habeas corpus ou habeas data.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Remédios constitucionais')
    WHERE d.nome = 'Direito Constitucional'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('habeas data.', TRUE, 1),
        ('habeas corpus.', FALSE, 2),
        ('ação popular.', FALSE, 3),
        ('mandado de segurança.', FALSE, 4),
        ('mandado de injunção.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Nos termos da Constituição Federal, tem legitimidade para propor ação popular que vise anular ato lesivo ao patrimônio público',
           d.id, b.id, 2023, 'Remédios constitucionais', a.id,
           'Art. 5º, LXXIII: qualquer cidadão, isto é, o eleitor no gozo dos direitos políticos. Pessoa jurídica não tem legitimidade (Súmula 365 do STF). Partido político e associação são legitimados do mandado de segurança coletivo, não da ação popular.',
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
        ('qualquer pessoa física ou jurídica.', FALSE, 1),
        ('qualquer cidadão.', TRUE, 2),
        ('somente o Ministério Público.', FALSE, 3),
        ('partido político com representação no Congresso Nacional.', FALSE, 4),
        ('associação constituída há pelo menos um ano.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A Constituição Federal veda a acumulação remunerada de cargos públicos, mas a admite, havendo compatibilidade de horários, no caso de',
           d.id, b.id, 2023, 'Administração Pública na Constituição', a.id,
           'Art. 37, XVI: é permitida a acumulação de dois cargos de professor; de um cargo de professor com outro técnico ou científico; ou de dois cargos ou empregos privativos de profissionais de saúde com profissões regulamentadas. O limite é sempre de dois cargos.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Administração Pública na Constituição')
    WHERE d.nome = 'Direito Constitucional'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('três cargos privativos de profissionais de saúde.', FALSE, 1),
        ('um cargo de juiz com dois cargos de professor.', FALSE, 2),
        ('dois cargos de professor.', TRUE, 3),
        ('um cargo de professor com dois cargos técnicos.', FALSE, 4),
        ('dois cargos técnicos ou científicos.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'De acordo com a Constituição Federal, o prazo de validade do concurso público será de',
           d.id, b.id, 2024, 'Administração Pública na Constituição', a.id,
           'Art. 37, III, da CF: o concurso tem validade de até dois anos, prorrogável uma única vez por igual período. O edital fixa o prazo dentro desse limite.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Administração Pública na Constituição')
    WHERE d.nome = 'Direito Constitucional'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('cinco anos, contados da homologação do resultado.', FALSE, 1),
        ('até quatro anos, improrrogável.', FALSE, 2),
        ('dois anos, prorrogável duas vezes, por igual período.', FALSE, 3),
        ('até dois anos, prorrogável uma vez, por igual período.', TRUE, 4),
        ('até um ano, prorrogável por mais seis meses.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Nos termos do art. 18 da Constituição Federal, a organização político-administrativa da República Federativa do Brasil compreende, todos autônomos,',
           d.id, b.id, 2025, 'Organização do Estado', a.id,
           'Art. 18: União, Estados, Distrito Federal e Municípios são os entes federativos autônomos. Os Territórios, quando existirem, integram a União e não têm autonomia.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Organização do Estado')
    WHERE d.nome = 'Direito Constitucional'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('a União, os Estados, o Distrito Federal, os Municípios e os Territórios.', FALSE, 1),
        ('a União e os Estados, apenas.', FALSE, 2),
        ('a União, os Estados e os Municípios, apenas.', FALSE, 3),
        ('a União, os Estados e o Distrito Federal, apenas.', FALSE, 4),
        ('a União, os Estados, o Distrito Federal e os Municípios.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Quanto à composição do Senado Federal, é correto afirmar que cada Estado e o Distrito Federal elegem',
           d.id, b.id, 2023, 'Poder Legislativo', a.id,
           'Art. 46 da CF: o Senado compõe-se de representantes dos Estados e do DF eleitos pelo princípio majoritário; cada um elege três senadores, com mandato de oito anos. A representação é renovada a cada quatro anos, alternadamente, por um e dois terços.',
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
        ('três senadores, com mandato de oito anos.', TRUE, 1),
        ('número de senadores proporcional à população.', FALSE, 2),
        ('três senadores, com mandato de quatro anos.', FALSE, 3),
        ('dois senadores, com mandato de oito anos.', FALSE, 4),
        ('quatro senadores, com mandato de quatro anos.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A casa é asilo inviolável do indivíduo, mas nela é possível penetrar sem o consentimento do morador, durante o dia, por determinação judicial.',
           d.id, b.id, 2024, 'Direitos fundamentais', a.id,
           'Art. 5º, XI: sem consentimento do morador, só se entra na casa em flagrante delito, desastre ou para prestar socorro (a qualquer hora), ou, durante o dia, por determinação judicial.',
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
    SELECT 'Havendo determinação judicial, é permitido ingressar na casa do indivíduo durante a noite, sem o consentimento do morador, para cumprir mandado de busca e apreensão.',
           d.id, b.id, 2025, 'Direitos fundamentais', a.id,
           'A ordem judicial só autoriza o ingresso durante o dia (art. 5º, XI). À noite, sem consentimento, apenas em flagrante delito, desastre ou para prestar socorro.',
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
        ('Certo', FALSE, 1),
        ('Errado', TRUE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Compete ao Supremo Tribunal Federal processar e julgar, originariamente, a ação direta de inconstitucionalidade de lei ou ato normativo federal ou estadual.',
           d.id, b.id, 2023, 'Controle de constitucionalidade', a.id,
           'Art. 102, I, a, da CF. Lei municipal em face da Constituição Federal não é objeto de ADI; pode ser questionada por ADPF ou no controle difuso.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Controle de constitucionalidade')
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
    SELECT 'São brasileiros natos os nascidos no estrangeiro, de pai brasileiro ou de mãe brasileira, desde que qualquer deles esteja a serviço da República Federativa do Brasil.',
           d.id, b.id, 2024, 'Nacionalidade', a.id,
           'Art. 12, I, b, da CF. Estar a serviço do Brasil basta; não é necessário registro nem opção posterior, exigidos apenas nas hipóteses da alínea c.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Nacionalidade')
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
    SELECT 'Entre os direitos sociais previstos no art. 6º da Constituição Federal estão a alimentação, a moradia e o transporte.',
           d.id, b.id, 2025, 'Direitos sociais', a.id,
           'O art. 6º lista educação, saúde, alimentação, trabalho, moradia, transporte, lazer, segurança, previdência social, proteção à maternidade e à infância e assistência aos desamparados. O transporte foi incluído pela EC 90/2015.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Direitos sociais')
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
    SELECT 'Os cargos de Presidente e de Vice-Presidente da República podem ser ocupados por brasileiros naturalizados, desde que residentes no país há mais de quinze anos.',
           d.id, b.id, 2023, 'Nacionalidade', a.id,
           'Art. 12, § 3º: são privativos de brasileiro nato os cargos de Presidente e Vice-Presidente da República, entre outros (presidentes da Câmara e do Senado, ministros do STF, carreira diplomática, oficial das Forças Armadas e Ministro de Estado da Defesa).',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Nacionalidade')
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
    SELECT 'O tipo de código malicioso que criptografa os dados do computador da vítima e exige pagamento, geralmente em criptomoeda, para restabelecer o acesso é denominado',
           d.id, b.id, 2024, 'Segurança da informação', a.id,
           'Ransomware sequestra os dados por criptografia e cobra resgate. Spyware monitora as atividades do usuário; worm se propaga sozinho pela rede; adware exibe propaganda; keylogger captura o que é digitado.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Segurança da informação')
    WHERE d.nome = 'Noções de Informática'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('keylogger.', FALSE, 1),
        ('ransomware.', TRUE, 2),
        ('worm.', FALSE, 3),
        ('adware.', FALSE, 4),
        ('spyware.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A técnica em que o golpista se passa por instituição confiável, por e-mail ou mensagem, para induzir a vítima a fornecer senhas e dados pessoais é conhecida como',
           d.id, b.id, 2023, 'Segurança da informação', a.id,
           'Phishing é engenharia social: explora a confiança da vítima em vez de uma falha técnica. DDoS sobrecarrega um serviço; backdoor é uma porta de acesso oculta; rootkit esconde a presença do invasor; sniffing captura o tráfego da rede.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Segurança da informação')
    WHERE d.nome = 'Noções de Informática'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('backdoor.', FALSE, 1),
        ('sniffing.', FALSE, 2),
        ('phishing.', TRUE, 3),
        ('negação de serviço (DDoS).', FALSE, 4),
        ('rootkit.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O protocolo utilizado para o envio de mensagens de correio eletrônico, inclusive entre servidores de e-mail, é o',
           d.id, b.id, 2023, 'Redes e internet', a.id,
           'SMTP envia as mensagens. POP3 e IMAP servem para receber e ler: o POP3 costuma baixar e remover do servidor, e o IMAP mantém as mensagens sincronizadas no servidor. FTP transfere arquivos e DNS resolve nomes.',
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
        ('DNS.', FALSE, 1),
        ('FTP.', FALSE, 2),
        ('POP3.', FALSE, 3),
        ('SMTP.', TRUE, 4),
        ('IMAP.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Na internet, a principal função do serviço DNS é',
           d.id, b.id, 2024, 'Redes e internet', a.id,
           'O DNS converte nomes legíveis, como www.exemplo.gov.br, no endereço IP do servidor. A atribuição automática de IP é feita pelo DHCP; o bloqueio de acessos, pelo firewall; a criptografia, pelo TLS usado no HTTPS.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Redes e internet')
    WHERE d.nome = 'Noções de Informática'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('atribuir endereços IP automaticamente aos computadores da rede local.', FALSE, 1),
        ('bloquear acessos não autorizados à rede.', FALSE, 2),
        ('armazenar em cache as páginas mais visitadas.', FALSE, 3),
        ('criptografar as páginas acessadas pelo navegador.', FALSE, 4),
        ('traduzir nomes de domínio em endereços IP.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Em uma planilha, as células A1, A2 e A3 contêm, respectivamente, os valores 10, 20 e 30. O resultado da fórmula =MÉDIA(A1:A3)*2 é',
           d.id, b.id, 2025, 'Planilhas eletrônicas', a.id,
           'A média de 10, 20 e 30 é 60 / 3 = 20. Multiplicada por 2, resulta em 40. A multiplicação é aplicada ao resultado da função, não a cada célula.',
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
        ('40.', TRUE, 1),
        ('60.', FALSE, 2),
        ('30.', FALSE, 3),
        ('20.', FALSE, 4),
        ('120.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Ao copiar uma fórmula para outras células de uma planilha, a referência que permanece inalterada tanto na linha quanto na coluna é',
           d.id, b.id, 2023, 'Planilhas eletrônicas', a.id,
           'O cifrão fixa o que vem logo depois dele. Em $A$1, coluna e linha estão fixas (referência absoluta). A$1 fixa só a linha e $A1 só a coluna (referências mistas); A1 é relativa. O símbolo # não cria referência.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Planilhas eletrônicas')
    WHERE d.nome = 'Noções de Informática'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('$A1', FALSE, 1),
        ('$A$1', TRUE, 2),
        ('#A#1', FALSE, 3),
        ('A$1', FALSE, 4),
        ('A1', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A memória do computador cujo conteúdo é perdido quando o equipamento é desligado é a',
           d.id, b.id, 2024, 'Hardware e sistemas operacionais', a.id,
           'A RAM é volátil: guarda os programas e dados em uso e se apaga sem energia. ROM, SSD, HD e memória flash são não voláteis.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Hardware e sistemas operacionais')
    WHERE d.nome = 'Noções de Informática'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('memória flash de um pendrive.', FALSE, 1),
        ('unidade SSD.', FALSE, 2),
        ('memória RAM.', TRUE, 3),
        ('unidade de disco rígido (HD).', FALSE, 4),
        ('memória ROM.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No Windows, o atalho de teclado que desfaz a última ação na maioria dos aplicativos é',
           d.id, b.id, 2025, 'Hardware e sistemas operacionais', a.id,
           'Ctrl + Z desfaz. Ctrl + Y refaz a ação desfeita; Ctrl + X recorta; Ctrl + V cola; Ctrl + P abre a impressão.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Hardware e sistemas operacionais')
    WHERE d.nome = 'Noções de Informática'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Ctrl + X.', FALSE, 1),
        ('Ctrl + Y.', FALSE, 2),
        ('Ctrl + V.', FALSE, 3),
        ('Ctrl + Z.', TRUE, 4),
        ('Ctrl + P.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O backup incremental copia apenas os arquivos criados ou alterados desde o último backup realizado, seja ele completo ou incremental.',
           d.id, b.id, 2024, 'Segurança da informação', a.id,
           'Essa é a definição do incremental. O backup diferencial, por sua vez, copia tudo o que mudou desde o último backup completo, e por isso cresce a cada execução.',
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
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Um firewall bem configurado é suficiente, por si só, para impedir a infecção do computador por vírus recebidos em anexos de e-mail.',
           d.id, b.id, 2023, 'Segurança da informação', a.id,
           'O firewall filtra conexões de rede; ele não analisa o conteúdo dos anexos que o usuário decide abrir. A proteção contra vírus depende de antivírus atualizado e do comportamento do usuário.',
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
    SELECT 'O protocolo HTTPS utiliza criptografia para proteger os dados trafegados entre o navegador e o servidor.',
           d.id, b.id, 2025, 'Redes e internet', a.id,
           'HTTPS é o HTTP sobre TLS: os dados são cifrados no trajeto e o certificado digital permite verificar a identidade do site.',
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
    SELECT 'A intranet é uma rede de acesso restrito, geralmente interna a uma organização, que utiliza os mesmos protocolos da internet, como o TCP/IP.',
           d.id, b.id, 2024, 'Redes e internet', a.id,
           'A intranet usa a mesma tecnologia da internet (TCP/IP, HTTP, navegadores), mas com acesso limitado aos membros da organização.',
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
    SELECT 'A negação da proposição "Todos os servidores são pontuais" é',
           d.id, b.id, 2023, 'Negação de proposições', a.id,
           'Para negar "todos são", basta mostrar que pelo menos um não é. "Nenhum servidor é pontual" é uma afirmação mais forte, que não corresponde à negação.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Negação de proposições')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Algum servidor é pontual.', FALSE, 1),
        ('Nenhum servidor é impontual.', FALSE, 2),
        ('Todos os servidores não são pontuais.', FALSE, 3),
        ('Nenhum servidor é pontual.', FALSE, 4),
        ('Algum servidor não é pontual.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A negação da proposição "João estuda e Maria trabalha" é',
           d.id, b.id, 2024, 'Negação de proposições', a.id,
           'Pela lei de De Morgan, ~(p ∧ q) ≡ ~p ∨ ~q: nega-se cada parte e troca-se "e" por "ou".',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Negação de proposições')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('João não estuda ou Maria não trabalha.', TRUE, 1),
        ('João não estuda e Maria não trabalha.', FALSE, 2),
        ('João estuda ou Maria trabalha.', FALSE, 3),
        ('João não estuda e Maria trabalha.', FALSE, 4),
        ('Se João estuda, então Maria trabalha.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Uma proposição logicamente equivalente a "Se chove, então a rua fica molhada" é',
           d.id, b.id, 2024, 'Equivalências lógicas', a.id,
           'A contrapositiva ~q → ~p é equivalente a p → q. A recíproca (q → p) e a inversa (~p → ~q) não são equivalentes. "Chove e a rua não fica molhada" é a negação da condicional.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Equivalências lógicas')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Chove e a rua não fica molhada.', FALSE, 1),
        ('Se a rua não fica molhada, então não chove.', TRUE, 2),
        ('Chove ou a rua fica molhada.', FALSE, 3),
        ('Se não chove, então a rua não fica molhada.', FALSE, 4),
        ('Se a rua fica molhada, então chove.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A negação da proposição "Se estudo, então sou aprovado" é',
           d.id, b.id, 2025, 'Negação de proposições', a.id,
           'A condicional p → q só é falsa quando p é verdadeira e q é falsa. Por isso sua negação é p ∧ ~q.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Negação de proposições')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Não estudo ou sou aprovado.', FALSE, 1),
        ('Não estudo e não sou aprovado.', FALSE, 2),
        ('Estudo e não sou aprovado.', TRUE, 3),
        ('Se sou aprovado, então estudo.', FALSE, 4),
        ('Se não estudo, então não sou aprovado.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Um produto teve o preço aumentado em 20% e, em seguida, reduzido em 20%. Em relação ao preço inicial, o preço final',
           d.id, b.id, 2023, 'Porcentagem', a.id,
           'Aplicam-se os fatores em sequência: 1,20 × 0,80 = 0,96. O preço final é 96% do inicial, uma redução de 4%. Os 20% da redução incidem sobre um valor já aumentado.',
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
        ('aumentou 4%.', FALSE, 1),
        ('permaneceu o mesmo.', FALSE, 2),
        ('diminuiu 2%.', FALSE, 3),
        ('diminuiu 4%.', TRUE, 4),
        ('diminuiu 20%.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A quantidade de anagramas da palavra PROVA é',
           d.id, b.id, 2024, 'Análise combinatória', a.id,
           'PROVA tem 5 letras distintas, então o número de anagramas é 5! = 5 × 4 × 3 × 2 × 1 = 120.',
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
        ('720.', FALSE, 1),
        ('60.', FALSE, 2),
        ('25.', FALSE, 3),
        ('24.', FALSE, 4),
        ('120.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O número de comissões de 3 pessoas que podem ser formadas a partir de um grupo de 6 pessoas é',
           d.id, b.id, 2025, 'Análise combinatória', a.id,
           'Em comissão a ordem não importa: C(6,3) = 6! / (3! × 3!) = 20. O valor 120 seria o arranjo A(6,3), em que a ordem importa.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Análise combinatória')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('20.', TRUE, 1),
        ('60.', FALSE, 2),
        ('216.', FALSE, 3),
        ('120.', FALSE, 4),
        ('18.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Ao lançar dois dados comuns, não viciados, a probabilidade de a soma dos resultados ser igual a 7 é',
           d.id, b.id, 2023, 'Probabilidade', a.id,
           'Há 36 resultados possíveis e 6 somam 7: (1,6), (2,5), (3,4), (4,3), (5,2) e (6,1). Logo, 6/36 = 1/6.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Probabilidade')
    WHERE d.nome = 'Raciocínio Lógico'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('5/36.', FALSE, 1),
        ('1/6.', TRUE, 2),
        ('1/36.', FALSE, 3),
        ('7/36.', FALSE, 4),
        ('1/12.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A proposição "Se 2 + 2 = 5, então a Lua é feita de queijo" é verdadeira.',
           d.id, b.id, 2024, 'Proposições e conectivos', a.id,
           'A condicional só é falsa quando o antecedente é verdadeiro e o consequente é falso. Como "2 + 2 = 5" é falso, a condicional é verdadeira, qualquer que seja o consequente.',
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
    SELECT 'A disjunção "p ou q" é falsa somente quando as proposições p e q são ambas falsas.',
           d.id, b.id, 2023, 'Proposições e conectivos', a.id,
           'Na disjunção inclusiva basta uma parte verdadeira para o todo ser verdadeiro, então ela só é falsa quando as duas partes são falsas.',
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
    SELECT 'A negação da proposição "Algum candidato foi aprovado" é "Algum candidato não foi aprovado".',
           d.id, b.id, 2025, 'Negação de proposições', a.id,
           'A negação de "algum é" é "nenhum é": "Nenhum candidato foi aprovado". As proposições "algum foi" e "algum não foi" podem ser verdadeiras ao mesmo tempo.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Negação de proposições')
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
    SELECT 'Um produto que custava R$ 200,00 e passou a custar R$ 250,00 sofreu aumento de 25%.',
           d.id, b.id, 2024, 'Porcentagem', a.id,
           'O aumento foi de R$ 50,00 sobre R$ 200,00: 50 / 200 = 0,25, ou seja, 25%. A base da porcentagem é sempre o valor inicial.',
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

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Na frase "Refiro-me a esta proposta", o acento grave seria obrigatório antes do pronome demonstrativo "esta".',
           d.id, b.id, 2024, 'Crase', a.id,
           'Não ocorre crase antes de "esta", "essa" e "este", porque esses pronomes não admitem artigo. Há crase apenas com "aquele(s)", "aquela(s)" e "aquilo" ("refiro-me àquela proposta").',
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
    SELECT 'Na frase "Havia muitos candidatos na sala", o verbo "haver" está corretamente empregado no singular, por ser impessoal.',
           d.id, b.id, 2023, 'Concordância verbal', a.id,
           'No sentido de existir, "haver" é impessoal e fica na 3ª pessoa do singular. Com "existir", a concordância seria no plural: "Existiam muitos candidatos".',
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
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Na frase "Os servidores, que chegaram atrasados, foram advertidos", a retirada das vírgulas alteraria o sentido do período.',
           d.id, b.id, 2025, 'Pontuação', a.id,
           'Com vírgulas, a oração é explicativa: todos os servidores chegaram atrasados e foram advertidos. Sem vírgulas, ela vira restritiva: só os que chegaram atrasados foram advertidos.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Pontuação')
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
    SELECT 'O princípio da publicidade admite exceções, como nos casos em que o sigilo seja imprescindível à segurança da sociedade e do Estado.',
           d.id, b.id, 2024, 'Princípios da Administração Pública', a.id,
           'A publicidade é a regra, mas a própria CF (art. 5º, XXXIII) e a Lei de Acesso à Informação preveem sigilo quando imprescindível à segurança da sociedade e do Estado, além da proteção à intimidade.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Princípios da Administração Pública')
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
    SELECT 'A autarquia é pessoa jurídica de direito privado, criada por lei, para executar atividades típicas da Administração Pública.',
           d.id, b.id, 2023, 'Organização administrativa', a.id,
           'A autarquia é pessoa jurídica de direito público, criada por lei específica (art. 37, XIX, da CF). Empresas públicas e sociedades de economia mista é que têm personalidade de direito privado.',
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
        ('Certo', FALSE, 1),
        ('Errado', TRUE, 2)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A anulação de ato administrativo ilegal produz, em regra, efeitos retroativos (ex tunc).',
           d.id, b.id, 2025, 'Anulação e revogação', a.id,
           'A anulação atinge o ato desde a origem, por vício de legalidade, e pode ser feita pela Administração ou pelo Judiciário. A revogação, por conveniência e oportunidade, produz efeitos ex nunc e só cabe à Administração (Súmula 473 do STF).',
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

-- Sprint 8: lote de questoes para dar corpo ao banco.
--
-- IMPORTANTE: as questoes abaixo sao AUTORAIS, escritas no estilo das bancas.
-- Nao sao reproducoes literais de provas anteriores (o texto das provas e
-- protegido por direito autoral das bancas). O conteudo cobrado, esse sim,
-- segue o que CESPE e FGV costumam exigir.

-- Normaliza o assunto que veio do seed inicial em caixa baixa.
UPDATE assunto SET nome = 'Morfologia' WHERE lower(nome) = 'morfologia';

-- Assuntos (nao recria os que ja existem).
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
SELECT d.id, 'Pontuação' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Pontuação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Morfologia' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Morfologia') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Formação de palavras' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Formação de palavras') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Sintaxe' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Sintaxe') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Vozes verbais' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Vozes verbais') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Colocação pronominal' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Colocação pronominal') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Concordância nominal' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Concordância nominal') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Semântica' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Semântica') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Ortografia' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Ortografia') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Coesão textual' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Coesão textual') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Orações subordinadas' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Orações subordinadas') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Predicação verbal' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Predicação verbal') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Acentuação gráfica' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Acentuação gráfica') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Emprego de pronomes relativos' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Emprego de pronomes relativos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Uso de conjunções' FROM disciplina d WHERE d.nome = 'Língua Portuguesa'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Uso de conjunções') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Princípios da Administração Pública' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Princípios da Administração Pública') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Atos administrativos' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Atos administrativos') AND a.disciplina_id = d.id);
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
SELECT d.id, 'Contratos administrativos' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Contratos administrativos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Servidores públicos' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Servidores públicos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Improbidade administrativa' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Improbidade administrativa') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Organização administrativa' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Organização administrativa') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Responsabilidade civil do Estado' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Responsabilidade civil do Estado') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Processo administrativo' FROM disciplina d WHERE d.nome = 'Direito Administrativo'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Processo administrativo') AND a.disciplina_id = d.id);

-- Questoes e alternativas.

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa em que o sinal indicativo de crase foi empregado corretamente.',
           d.id, b.id, 2023, 'Crase', a.id,
           'A crase ocorre na fusão da preposição "a" com o artigo "a". Em "entreguei algo a alguém", o verbo exige a preposição, e "diretora" admite o artigo. Não há crase antes de palavra masculina, de pronome indefinido, de verbo, nem em "de segunda a sexta" (expressão sem artigo).'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Crase')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('Entreguei o relatório à diretora do setor.', TRUE),
        ('Passei à cavalo pela fazenda.', FALSE),
        ('Refiro-me à qualquer servidor da repartição.', FALSE),
        ('O expediente vai de segunda à sexta.', FALSE),
        ('Começou à chover durante a sessão.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa em que a concordância verbal está de acordo com a norma-padrão.',
           d.id, b.id, 2023, 'Concordância verbal', a.id,
           'O verbo "fazer" indicando tempo decorrido é impessoal e fica na 3ª pessoa do singular. O mesmo vale para "haver" no sentido de existir. Já "existir" é pessoal e concorda com o sujeito ("existem diversos processos").'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Concordância verbal')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('Faz dez anos que ele ingressou no serviço público.', TRUE),
        ('Fazem dez anos que ele ingressou no serviço público.', FALSE),
        ('Houveram muitas falhas no procedimento.', FALSE),
        ('Existe diversos processos pendentes de análise.', FALSE),
        ('Devem haver razões para o indeferimento.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa em que a regência verbal está correta.',
           d.id, b.id, 2022, 'Regência verbal', a.id,
           'No sentido de "ver, presenciar", o verbo assistir é transitivo indireto e exige a preposição "a". "Preferir" não admite reforço de superioridade, "obedecer" pede a preposição "a" (à ordem) e "chegar" rege "a", não "em".'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Regência verbal')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('Assistimos ao julgamento pela televisão.', TRUE),
        ('Assistimos o julgamento pela televisão.', FALSE),
        ('Prefiro mais trabalhar de manhã do que à tarde.', FALSE),
        ('O servidor obedeceu a ordem do superior sem questionar.', FALSE),
        ('Cheguei na repartição antes do horário.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa em que a vírgula foi empregada corretamente.',
           d.id, b.id, 2022, 'Pontuação', a.id,
           'As vírgulas isolam uma oração subordinada adjetiva explicativa. É erro separar por vírgula o sujeito do verbo, o verbo do seu complemento ou introduzir vírgula antes de "que" integrante.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Pontuação')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('Os servidores, que haviam sido convocados, compareceram à reunião.', TRUE),
        ('Os servidores convocados, compareceram à reunião.', FALSE),
        ('O diretor informou, que a sessão seria adiada.', FALSE),
        ('Compareceram à reunião os servidores, e os estagiários.', FALSE),
        ('A comissão analisou, o processo administrativo.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Na frase "Ele agiu com muita cautela durante a investigação", a palavra destacada "muita" classifica-se como:',
           d.id, b.id, 2023, 'Morfologia', a.id,
           'Antes de substantivo ("cautela"), "muita" é pronome indefinido e flexiona em gênero e número. Só seria advérbio se acompanhasse verbo, adjetivo ou outro advérbio, caso em que permaneceria invariável.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Morfologia')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('pronome indefinido', TRUE),
        ('advérbio de intensidade', FALSE),
        ('adjetivo', FALSE),
        ('substantivo', FALSE),
        ('preposição', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'A palavra "infelizmente" é formada por qual processo?',
           d.id, b.id, 2023, 'Formação de palavras', a.id,
           'A palavra admite a retirada isolada do prefixo ("felizmente") e do sufixo ("infeliz"), e ambas as formas existem na língua. Na parassíntese, prefixo e sufixo são acrescidos simultaneamente e a retirada de apenas um resulta em forma inexistente, como em "entardecer".'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Formação de palavras')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('derivação prefixal e sufixal', TRUE),
        ('derivação parassintética', FALSE),
        ('composição por justaposição', FALSE),
        ('derivação regressiva', FALSE),
        ('hibridismo', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Em "Precisa-se de servidores qualificados", o sujeito da oração é classificado como:',
           d.id, b.id, 2022, 'Sintaxe', a.id,
           'Com verbo transitivo indireto acompanhado do índice de indeterminação do sujeito "se", o sujeito é indeterminado. Se o verbo fosse transitivo direto, o "se" seria partícula apassivadora e haveria sujeito paciente, como em "vendem-se casas".'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sintaxe')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('indeterminado', TRUE),
        ('oculto', FALSE),
        ('simples', FALSE),
        ('inexistente', FALSE),
        ('composto', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa que apresenta a voz passiva sintética.',
           d.id, b.id, 2022, 'Vozes verbais', a.id,
           'Na passiva sintética, o "se" é partícula apassivadora ligada a verbo transitivo direto, e o verbo concorda com o sujeito paciente ("os editais"). A alternativa com "foram publicados" é passiva analítica, e a com "precisa-se" traz sujeito indeterminado.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Vozes verbais')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('Publicaram-se os editais no diário oficial.', TRUE),
        ('Os editais foram publicados no diário oficial.', FALSE),
        ('A comissão publicou os editais.', FALSE),
        ('Ele se machucou durante o expediente.', FALSE),
        ('Precisa-se de auxiliares administrativos.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa em que a colocação pronominal está de acordo com a norma-padrão.',
           d.id, b.id, 2023, 'Colocação pronominal', a.id,
           'Palavras de sentido negativo, pronomes interrogativos, pronomes relativos e orações optativas atraem o pronome para antes do verbo (próclise). Além disso, não se inicia período com pronome átono na norma-padrão escrita.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Colocação pronominal')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('Nunca me disseram a verdade sobre o caso.', TRUE),
        ('Nunca disseram-me a verdade sobre o caso.', FALSE),
        ('Me parece que o prazo já venceu.', FALSE),
        ('Quem avisou-o do adiamento?', FALSE),
        ('Que Deus abençoe-nos sempre.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa correta quanto à concordância nominal.',
           d.id, b.id, 2023, 'Concordância nominal', a.id,
           '"Anexo" é adjetivo e concorda em gênero e número com o substantivo a que se refere. "É proibido" só fica invariável sem determinante ("é proibida a entrada"), "meio" como advérbio é invariável e "mesmo" como reforço concorda com o sujeito.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Concordância nominal')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('Seguem anexas as certidões solicitadas.', TRUE),
        ('Segue anexo as certidões solicitadas.', FALSE),
        ('É proibido a entrada de pessoas não autorizadas.', FALSE),
        ('As candidatas ficaram meias nervosas com o resultado.', FALSE),
        ('Elas mesmo redigiram o parecer.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa em que a palavra foi empregada com o sentido adequado.',
           d.id, b.id, 2022, 'Semântica', a.id,
           '"Tráfego" é movimento de veículos; "tráfico" é comércio ilícito. "Iminente" indica algo prestes a ocorrer (eminente = ilustre), "infringir" é violar (infligir = aplicar punição) e "retificar" é corrigir (ratificar = confirmar).'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Semântica')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('O tráfego na avenida estava intenso pela manhã.', TRUE),
        ('O tráfico na avenida estava intenso pela manhã.', FALSE),
        ('A comissão emitiu um parecer sobre o eminente perigo.', FALSE),
        ('O servidor foi acusado de infligir o regulamento.', FALSE),
        ('O relatório ratificou os dados errados que constavam do sistema.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa em que o emprego de "por que", "porque", "por quê" e "porquê" está correto.',
           d.id, b.id, 2022, 'Ortografia', a.id,
           'Separado e sem acento, equivale a "por qual razão" em pergunta direta ou indireta. Junto e sem acento introduz explicação; separado e com acento aparece no fim da frase; junto e com acento é substantivo, acompanhado de determinante.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Ortografia')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('Não sei por que o processo foi arquivado.', TRUE),
        ('Não sei porque o processo foi arquivado.', FALSE),
        ('Ele faltou por quê estava doente.', FALSE),
        ('O porque da decisão não foi esclarecido.', FALSE),
        ('Você não compareceu por que?', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Em "O prazo foi prorrogado; ____, os candidatos puderam concluir a inscrição", o conectivo que completa adequadamente a lacuna, mantendo a relação de conclusão, é:',
           d.id, b.id, 2023, 'Coesão textual', a.id,
           '"Portanto" expressa conclusão. "Entretanto" e "porém" indicam oposição, "porquanto" introduz explicação ou causa e "conquanto" tem valor concessivo, equivalendo a "embora".'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Coesão textual')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('portanto', TRUE),
        ('entretanto', FALSE),
        ('porquanto', FALSE),
        ('conquanto', FALSE),
        ('porém', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Em "Embora o prazo tivesse expirado, o recurso foi analisado", a oração destacada classifica-se como:',
           d.id, b.id, 2023, 'Orações subordinadas', a.id,
           '"Embora" introduz oração subordinada adverbial concessiva, que apresenta um fato contrário ao da oração principal sem impedi-lo. Condicionais usam "se" e "caso"; causais, "porque" e "visto que".'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Orações subordinadas')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('subordinada adverbial concessiva', TRUE),
        ('subordinada adverbial condicional', FALSE),
        ('subordinada adverbial causal', FALSE),
        ('subordinada adjetiva restritiva', FALSE),
        ('coordenada sindética adversativa', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Em "O servidor entregou o documento ao chefe", o verbo classifica-se como:',
           d.id, b.id, 2022, 'Predicação verbal', a.id,
           'O verbo apresenta dois complementos: "o documento" é objeto direto, sem preposição, e "ao chefe" é objeto indireto, com preposição. Daí a classificação como bitransitivo.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Predicação verbal')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('transitivo direto e indireto', TRUE),
        ('transitivo direto', FALSE),
        ('transitivo indireto', FALSE),
        ('intransitivo', FALSE),
        ('de ligação', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa em que todas as palavras estão corretamente acentuadas.',
           d.id, b.id, 2022, 'Acentuação gráfica', a.id,
           '"Júri" é paroxítona terminada em -i; "saída" tem hiato tônico com "i"; "três" é monossílabo tônico terminado em -es. "Item", "rubrica" e "gratuito" não são acentuados, e o acordo ortográfico eliminou o acento em "ideia" e o diferencial em "polo".'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Acentuação gráfica')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('júri, saída, três', TRUE),
        ('juri, saida, tres', FALSE),
        ('ítem, rúbrica, gratuíto', FALSE),
        ('pólo, féia, idéia', FALSE),
        ('caráter, orgão, tôrre', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa em que o pronome relativo foi empregado corretamente.',
           d.id, b.id, 2023, 'Emprego de pronomes relativos', a.id,
           '"Onde" refere-se exclusivamente a lugar físico. Para acontecimentos, documentos ou circunstâncias, empregam-se "em que", "no qual" ou "quando", conforme o caso.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Emprego de pronomes relativos')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('A repartição onde trabalho fica no centro da cidade.', TRUE),
        ('A reunião onde discutimos o assunto foi longa.', FALSE),
        ('O relatório onde constam os dados está anexo.', FALSE),
        ('A situação onde nos encontramos exige cautela.', FALSE),
        ('O momento onde tudo começou foi registrado.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa em que a conjunção estabelece relação de oposição.',
           d.id, b.id, 2023, 'Uso de conjunções', a.id,
           '"Mas" é conjunção coordenativa adversativa, que marca oposição. "Pois" (anteposto) indica causa, "logo" indica conclusão, "e" é aditiva e "ou... ou" é alternativa.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Uso de conjunções')
    WHERE d.nome = 'Língua Portuguesa'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('O prazo era curto, mas o trabalho foi concluído.', TRUE),
        ('O prazo era curto, pois havia muitas etapas.', FALSE),
        ('O prazo era curto; logo, houve atraso.', FALSE),
        ('O prazo era curto e o trabalho foi concluído.', FALSE),
        ('Ou o prazo é ampliado, ou o projeto será suspenso.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Acerca do princípio da legalidade, assinale a alternativa correta.',
           d.id, b.id, 2023, 'Princípios da Administração Pública', a.id,
           'Para o particular vigora a autonomia da vontade (pode fazer tudo o que a lei não proíbe); para a Administração vale a legalidade estrita, exigindo autorização legal. O princípio está expresso no art. 37, caput, da Constituição e alcança também os atos discricionários.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Princípios da Administração Pública')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('A Administração Pública só pode fazer o que a lei autoriza.', TRUE),
        ('A Administração Pública pode fazer tudo o que a lei não proíbe.', FALSE),
        ('O princípio da legalidade aplica-se apenas aos atos vinculados.', FALSE),
        ('O administrador pode afastar a lei por razões de conveniência.', FALSE),
        ('A legalidade não está prevista expressamente na Constituição.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'A vedação de que conste nome, símbolo ou imagem de autoridade em publicidade de atos e programas de órgãos públicos decorre diretamente do princípio da:',
           d.id, b.id, 2022, 'Princípios da Administração Pública', a.id,
           'O art. 37, §1º, da Constituição veda a promoção pessoal na publicidade oficial, exigindo caráter educativo, informativo ou de orientação social. A regra é desdobramento direto da impessoalidade.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Princípios da Administração Pública')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('impessoalidade', TRUE),
        ('eficiência', FALSE),
        ('moralidade', FALSE),
        ('publicidade', FALSE),
        ('razoabilidade', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale o atributo do ato administrativo que permite à Administração executá-lo diretamente, sem necessidade de autorização judicial prévia.',
           d.id, b.id, 2023, 'Atos administrativos', a.id,
           'A autoexecutoriedade dispensa a via judicial para dar efetividade ao ato, mas não está presente em todos eles: exige previsão legal ou situação de urgência. A presunção de legitimidade diz respeito à veracidade dos fatos alegados e a imperatividade, à imposição unilateral de obrigações.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Atos administrativos')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('autoexecutoriedade', TRUE),
        ('presunção de legitimidade', FALSE),
        ('imperatividade', FALSE),
        ('tipicidade', FALSE),
        ('discricionariedade', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'São requisitos (ou elementos) do ato administrativo:',
           d.id, b.id, 2023, 'Atos administrativos', a.id,
           'A doutrina majoritária, apoiada na Lei 4.717/1965, aponta cinco requisitos. Competência, finalidade e forma são sempre vinculados; motivo e objeto podem ser discricionários, e é neles que reside o mérito.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Atos administrativos')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('competência, finalidade, forma, motivo e objeto', TRUE),
        ('competência, oportunidade, forma, mérito e objeto', FALSE),
        ('legalidade, moralidade, publicidade, eficiência e forma', FALSE),
        ('sujeito, causa, prazo, condição e encargo', FALSE),
        ('competência, motivo, mérito, eficácia e validade', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Quanto à extinção dos atos administrativos, é correto afirmar que:',
           d.id, b.id, 2022, 'Anulação e revogação', a.id,
           'A anulação atinge ato ilegal e opera, em regra, com efeitos ex tunc, ressalvados os direitos de terceiros de boa-fé. A revogação recai sobre ato legal e inconveniente, com efeitos ex nunc, e não alcança atos vinculados nem atos que já exauriram seus efeitos.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Anulação e revogação')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('a anulação decorre de ilegalidade e, em regra, produz efeitos retroativos.', TRUE),
        ('a revogação decorre de ilegalidade e produz efeitos retroativos.', FALSE),
        ('a anulação só pode ser feita pelo Poder Judiciário.', FALSE),
        ('a revogação pode atingir atos vinculados já exauridos.', FALSE),
        ('a anulação produz efeitos apenas a partir da decisão.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'A atuação da Administração que limita o exercício de direitos individuais em benefício do interesse coletivo caracteriza o exercício do:',
           d.id, b.id, 2022, 'Poderes administrativos', a.id,
           'O poder de polícia condiciona e restringe o uso de bens, o exercício de direitos e a prática de atividades em favor do interesse público. Seus ciclos são a ordem, o consentimento, a fiscalização e a sanção.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Poderes administrativos')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('poder de polícia', TRUE),
        ('poder hierárquico', FALSE),
        ('poder disciplinar', FALSE),
        ('poder regulamentar', FALSE),
        ('poder vinculado', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'A aplicação de penalidade a servidor público em razão de infração funcional configura exercício do:',
           d.id, b.id, 2023, 'Poderes administrativos', a.id,
           'O poder disciplinar permite punir agentes públicos e pessoas sujeitas à disciplina interna da Administração. Embora decorra do poder hierárquico, com ele não se confunde, e sua aplicação exige contraditório e ampla defesa.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Poderes administrativos')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('poder disciplinar', TRUE),
        ('poder de polícia', FALSE),
        ('poder normativo', FALSE),
        ('poder hierárquico, exclusivamente', FALSE),
        ('poder vinculado', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Nos termos da Lei 14.133/2021, a modalidade de licitação destinada à contratação de bens e serviços comuns, qualquer que seja o valor, é:',
           d.id, b.id, 2023, 'Licitações', a.id,
           'O pregão destina-se a bens e serviços comuns, sem limite de valor. A concorrência aplica-se a bens e serviços especiais e obras; o concurso, a trabalho técnico ou artístico; o leilão, à alienação de bens; e o diálogo competitivo, a objetos de inovação técnica ou tecnológica.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Licitações')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('pregão', TRUE),
        ('concorrência', FALSE),
        ('concurso', FALSE),
        ('leilão', FALSE),
        ('diálogo competitivo', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'A contratação direta em razão da inviabilidade de competição caracteriza hipótese de:',
           d.id, b.id, 2022, 'Licitações', a.id,
           'Na inexigibilidade a competição é inviável, como na contratação de fornecedor exclusivo ou de profissional de notória especialização; o rol é exemplificativo. Na dispensa a competição seria possível, mas a lei autoriza a contratação direta em rol taxativo.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Licitações')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('inexigibilidade de licitação', TRUE),
        ('dispensa de licitação', FALSE),
        ('licitação deserta', FALSE),
        ('licitação fracassada', FALSE),
        ('adjudicação compulsória', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'As chamadas cláusulas exorbitantes dos contratos administrativos caracterizam-se por:',
           d.id, b.id, 2022, 'Contratos administrativos', a.id,
           'As cláusulas exorbitantes decorrem da supremacia do interesse público e incluem alteração e rescisão unilaterais, fiscalização, aplicação de sanções e ocupação temporária. Decorrem da lei, independentemente de previsão contratual, e não afastam o direito ao equilíbrio econômico-financeiro.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Contratos administrativos')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('conferir prerrogativas à Administração em posição de superioridade sobre o contratado.', TRUE),
        ('garantir igualdade absoluta entre as partes contratantes.', FALSE),
        ('ser nulas por violarem o equilíbrio contratual.', FALSE),
        ('depender sempre de previsão expressa no edital para existirem.', FALSE),
        ('aplicar-se apenas aos contratos de obra pública.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Nos termos da Constituição Federal, o servidor nomeado para cargo de provimento efetivo em virtude de concurso público adquire estabilidade após:',
           d.id, b.id, 2023, 'Servidores públicos', a.id,
           'O art. 41 da Constituição, com a redação da EC 19/1998, fixa três anos de efetivo exercício, sendo obrigatória a avaliação especial de desempenho por comissão. A estabilidade não se confunde com a vitaliciedade, restrita a determinadas carreiras.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Servidores públicos')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('três anos de efetivo exercício, aprovado em avaliação de desempenho.', TRUE),
        ('dois anos de efetivo exercício.', FALSE),
        ('cinco anos de efetivo exercício.', FALSE),
        ('a posse no cargo.', FALSE),
        ('um ano de efetivo exercício.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'De acordo com a Lei 8.112/1990, o prazo para a posse, contado da publicação do ato de provimento, é de:',
           d.id, b.id, 2023, 'Servidores públicos', a.id,
           'A posse deve ocorrer em até 30 dias da publicação do ato de provimento; findo o prazo sem posse, o ato é tornado sem efeito. Empossado, o servidor tem 15 dias para entrar em exercício, sob pena de exoneração.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Servidores públicos')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('30 dias', TRUE),
        ('15 dias', FALSE),
        ('10 dias', FALSE),
        ('45 dias', FALSE),
        ('60 dias', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Após as alterações promovidas pela Lei 14.230/2021 na Lei 8.429/1992, é correto afirmar que os atos de improbidade administrativa:',
           d.id, b.id, 2022, 'Improbidade administrativa', a.id,
           'A Lei 14.230/2021 suprimiu a modalidade culposa, antes admitida no dano ao erário, e passou a exigir dolo, entendido como a vontade livre e consciente de alcançar o resultado ilícito. A improbidade tem natureza civil e não se confunde com ilícito penal.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Improbidade administrativa')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('exigem a demonstração de dolo específico do agente.', TRUE),
        ('admitem a modalidade culposa em qualquer hipótese.', FALSE),
        ('admitem a modalidade culposa apenas quanto ao enriquecimento ilícito.', FALSE),
        ('dispensam a comprovação do elemento subjetivo.', FALSE),
        ('constituem crime de responsabilidade.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Assinale a alternativa que apresenta corretamente uma característica das autarquias.',
           d.id, b.id, 2022, 'Organização administrativa', a.id,
           'A autarquia é criada por lei específica, tem personalidade jurídica de direito público e integra a Administração Indireta. Seus bens são públicos e impenhoráveis, com pagamento de dívidas judiciais por precatório, e não se sujeita à falência.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Organização administrativa')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('São pessoas jurídicas de direito público criadas por lei específica.', TRUE),
        ('São pessoas jurídicas de direito privado criadas por decreto.', FALSE),
        ('Integram a Administração Direta.', FALSE),
        ('Têm seus bens sujeitos a penhora como os das empresas privadas.', FALSE),
        ('Submetem-se ao regime falimentar.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'A distribuição interna de competências dentro de uma mesma pessoa jurídica denomina-se:',
           d.id, b.id, 2023, 'Organização administrativa', a.id,
           'Na desconcentração a repartição ocorre internamente, entre órgãos da mesma pessoa jurídica, com relação de hierarquia. Na descentralização a atividade é transferida a outra pessoa, física ou jurídica, sem hierarquia, mas sob controle finalístico.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Organização administrativa')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('desconcentração', TRUE),
        ('descentralização', FALSE),
        ('delegação legislativa', FALSE),
        ('avocação', FALSE),
        ('terceirização', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Segundo o art. 37, §6º, da Constituição Federal, a responsabilidade civil das pessoas jurídicas de direito público por danos causados por seus agentes a terceiros é, em regra:',
           d.id, b.id, 2023, 'Responsabilidade civil do Estado', a.id,
           'A responsabilidade é objetiva: basta conduta, dano e nexo causal, dispensada a prova de culpa. Adotou-se o risco administrativo, que admite excludentes como culpa exclusiva da vítima, caso fortuito e força maior. Contra o agente, cabe ação regressiva mediante dolo ou culpa.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Responsabilidade civil do Estado')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('objetiva, na modalidade risco administrativo.', TRUE),
        ('subjetiva, exigindo comprovação de culpa do agente.', FALSE),
        ('objetiva, na modalidade risco integral, sem excludentes.', FALSE),
        ('solidária com o agente causador do dano.', FALSE),
        ('afastada quando o agente atua fora do horário de expediente.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Quanto à responsabilidade do Estado por condutas omissivas, prevalece o entendimento de que:',
           d.id, b.id, 2022, 'Responsabilidade civil do Estado', a.id,
           'Na omissão genérica, a orientação predominante exige a culpa do serviço, configurada quando o serviço não funciona, funciona mal ou tardiamente. Já na omissão específica, em que o Estado tem dever legal de agir e posição de garante, a responsabilidade é objetiva.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'CESPE/CEBRASPE'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Responsabilidade civil do Estado')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('é subjetiva, exigindo demonstração de falha no serviço.', TRUE),
        ('é sempre objetiva, como nas condutas comissivas.', FALSE),
        ('é integral, sem admitir excludentes.', FALSE),
        ('não existe, por ausência de conduta estatal.', FALSE),
        ('depende de prévia condenação criminal do agente.', FALSE)
) AS v(texto, correta);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao)
    SELECT 'Nos termos da Lei 9.784/1999, o prazo para a Administração anular seus próprios atos de que decorram efeitos favoráveis aos destinatários, salvo comprovada má-fé, é de:',
           d.id, b.id, 2022, 'Processo administrativo', a.id,
           'O art. 54 da Lei 9.784/1999 fixa prazo decadencial de cinco anos, contados da prática do ato. Comprovada a má-fé do beneficiário, não há decadência. O dispositivo concretiza os princípios da segurança jurídica e da proteção à confiança.'
    FROM disciplina d
    JOIN banca b ON b.nome = 'FGV'
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Processo administrativo')
    WHERE d.nome = 'Direito Administrativo'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta)
SELECT nova.id, v.texto, v.correta
FROM nova, (VALUES
        ('cinco anos, contados da data em que foram praticados.', TRUE),
        ('dois anos, contados da ciência do interessado.', FALSE),
        ('dez anos, contados da publicação do ato.', FALSE),
        ('três anos, contados do trânsito em julgado.', FALSE),
        ('prazo indeterminado, por se tratar de ato nulo.', FALSE)
) AS v(texto, correta);

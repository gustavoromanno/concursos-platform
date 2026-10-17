-- Lote 9 de questoes AUTORAIS, gerado por scripts/gerar_questoes.py.
-- Nao edite a mao: altere o script e gere uma nova versao (V16, V17...).
--
-- Questoes escritas no estilo das bancas, nao copiadas de provas (o texto
-- das provas e protegido por direito autoral das bancas).

-- Disciplinas novas
INSERT INTO disciplina (nome) SELECT 'Redes de Computadores'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Redes de Computadores');

-- Assuntos (so cria os que ainda nao existem)
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Modelo OSI e TCP/IP' FROM disciplina d WHERE d.nome = 'Redes de Computadores'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Modelo OSI e TCP/IP') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Protocolos de transporte' FROM disciplina d WHERE d.nome = 'Redes de Computadores'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Protocolos de transporte') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Protocolos de aplicação' FROM disciplina d WHERE d.nome = 'Redes de Computadores'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Protocolos de aplicação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Endereçamento IP' FROM disciplina d WHERE d.nome = 'Redes de Computadores'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Endereçamento IP') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Normalização' FROM disciplina d WHERE d.nome = 'Banco de Dados'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Normalização') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'SQL' FROM disciplina d WHERE d.nome = 'Banco de Dados'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('SQL') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Transações' FROM disciplina d WHERE d.nome = 'Banco de Dados'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Transações') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Modelo relacional' FROM disciplina d WHERE d.nome = 'Banco de Dados'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Modelo relacional') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Criptografia' FROM disciplina d WHERE d.nome = 'Segurança da Informação'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Criptografia') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Malwares e ataques' FROM disciplina d WHERE d.nome = 'Segurança da Informação'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Malwares e ataques') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Backup' FROM disciplina d WHERE d.nome = 'Segurança da Informação'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Backup') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Controle de acesso' FROM disciplina d WHERE d.nome = 'Segurança da Informação'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Controle de acesso') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Metodologias ágeis' FROM disciplina d WHERE d.nome = 'Engenharia de Software'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Metodologias ágeis') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Processos de software' FROM disciplina d WHERE d.nome = 'Engenharia de Software'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Processos de software') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Testes de software' FROM disciplina d WHERE d.nome = 'Engenharia de Software'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Testes de software') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Requisitos' FROM disciplina d WHERE d.nome = 'Engenharia de Software'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Requisitos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'UML' FROM disciplina d WHERE d.nome = 'Engenharia de Software'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('UML') AND a.disciplina_id = d.id);

-- Questoes
WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No modelo de referência OSI, a camada responsável pelo endereçamento lógico e pelo roteamento de pacotes entre redes distintas é a camada de',
           d.id, b.id, 2026, 'Modelo OSI e TCP/IP', a.id,
           'A camada de Rede (3) cuida do endereçamento lógico (por exemplo, IP) e do roteamento entre redes. O Enlace (2) trata do quadro e do endereço físico (MAC) dentro do mesmo segmento; o Transporte (4) cuida da comunicação fim a fim entre processos (portas).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Modelo OSI e TCP/IP')
    WHERE d.nome = 'Redes de Computadores'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Rede', TRUE, 1),
        ('Física', FALSE, 2),
        ('Transporte', FALSE, 3),
        ('Enlace', FALSE, 4),
        ('Sessão', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Em comparação com o TCP, o protocolo UDP',
           d.id, b.id, 2026, 'Protocolos de transporte', a.id,
           'O UDP é não orientado a conexão: não faz handshake, não confirma recebimento nem reordena dados. Handshake, ACK, retransmissão e janela deslizante são características do TCP. Ambos operam na camada de transporte.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Protocolos de transporte')
    WHERE d.nome = 'Redes de Computadores'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('estabelece conexão por meio do three-way handshake antes de enviar os dados.', FALSE, 1),
        ('não estabelece conexão nem garante a entrega ou a ordem dos datagramas, o que o torna mais leve para aplicações como streaming e DNS.', TRUE, 2),
        ('garante a entrega e a ordem dos segmentos por meio de confirmações (ACK) e retransmissões.', FALSE, 3),
        ('opera na camada de rede, junto com o protocolo IP.', FALSE, 4),
        ('possui controle de congestionamento e de fluxo por janela deslizante.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O DNS utiliza, por padrão, a porta 53, e as consultas comuns de resolução de nomes são feitas preferencialmente sobre UDP, podendo recorrer ao TCP em respostas grandes e em transferências de zona.',
           d.id, b.id, 2026, 'Protocolos de aplicação', a.id,
           'Certo. A porta padrão do DNS é a 53, tanto em UDP quanto em TCP. Consultas comuns usam UDP; o TCP é usado em transferência de zona e quando a resposta não cabe em um datagrama UDP.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Protocolos de aplicação')
    WHERE d.nome = 'Redes de Computadores'
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
    SELECT 'Uma sub-rede IPv4 com máscara /26 (255.255.255.192) comporta, no máximo, quantos endereços utilizáveis por hosts?',
           d.id, b.id, 2026, 'Endereçamento IP', a.id,
           'Com /26 sobram 32 − 26 = 6 bits para hosts: 2⁶ = 64 endereços. Descontando o endereço de rede e o de broadcast, restam 64 − 2 = 62 endereços utilizáveis.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Endereçamento IP')
    WHERE d.nome = 'Redes de Computadores'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('126', FALSE, 1),
        ('30', FALSE, 2),
        ('62', TRUE, 3),
        ('64', FALSE, 4),
        ('254', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O endereço 172.32.10.1 pertence a uma das faixas de endereços IPv4 privados definidas na RFC 1918.',
           d.id, b.id, 2025, 'Endereçamento IP', a.id,
           'Errado. As faixas privadas são 10.0.0.0/8, 172.16.0.0/12 (de 172.16.0.0 a 172.31.255.255) e 192.168.0.0/16. O endereço 172.32.10.1 está fora da faixa 172.16–172.31, portanto é público.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Endereçamento IP')
    WHERE d.nome = 'Redes de Computadores'
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
    SELECT 'O protocolo que permite a um computador obter automaticamente, ao se conectar à rede, um endereço IP, a máscara de sub-rede, o gateway padrão e os servidores DNS é o',
           d.id, b.id, 2025, 'Protocolos de aplicação', a.id,
           'O DHCP distribui automaticamente a configuração de rede. O ARP descobre o MAC a partir do IP; o ICMP transporta mensagens de controle e erro (como o ping); o SNMP serve para gerenciar equipamentos; o NAT traduz endereços privados em públicos.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Protocolos de aplicação')
    WHERE d.nome = 'Redes de Computadores'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('SNMP', FALSE, 1),
        ('NAT', FALSE, 2),
        ('ICMP', FALSE, 3),
        ('DHCP', TRUE, 4),
        ('ARP', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O protocolo ARP é utilizado para descobrir o endereço físico (MAC) correspondente a um endereço IPv4 conhecido, dentro da mesma rede local.',
           d.id, b.id, 2025, 'Modelo OSI e TCP/IP', a.id,
           'Certo. O ARP envia um pedido em broadcast perguntando quem tem determinado IP, e o dono responde com seu endereço MAC. No IPv6, essa função é feita pelo Neighbor Discovery.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Modelo OSI e TCP/IP')
    WHERE d.nome = 'Redes de Computadores'
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
    SELECT 'Uma tabela relacional está na segunda forma normal (2FN) quando está na 1FN e',
           d.id, b.id, 2026, 'Normalização', a.id,
           '2FN: eliminar dependências parciais, ou seja, nenhum atributo não chave pode depender de só uma parte da chave composta. Sem dependência transitiva é a 3FN; valores atômicos, a 1FN; todo determinante ser chave candidata, a FNBC; sem dependência multivalorada, a 4FN.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Normalização')
    WHERE d.nome = 'Banco de Dados'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('todos os seus atributos possuem valores atômicos, sem grupos repetidos.', FALSE, 1),
        ('todo determinante de uma dependência funcional é uma chave candidata.', FALSE, 2),
        ('não possui dependências multivaloradas independentes.', FALSE, 3),
        ('nenhum atributo não chave depende de outro atributo não chave, sem dependências transitivas.', FALSE, 4),
        ('todo atributo não chave depende da chave primária inteira, sem dependências parciais de parte de uma chave composta.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Em SQL, o comando que remove todas as linhas de uma tabela, mantendo a sua estrutura, e que é classificado como DDL é o',
           d.id, b.id, 2026, 'SQL', a.id,
           'TRUNCATE esvazia a tabela e mantém a estrutura; é tratado como DDL. DROP apaga a tabela inteira (estrutura e dados). DELETE também remove linhas, mas é DML e pode usar WHERE. ALTER muda a estrutura; REVOKE retira permissões (DCL).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('SQL')
    WHERE d.nome = 'Banco de Dados'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('TRUNCATE', TRUE, 1),
        ('ALTER', FALSE, 2),
        ('REVOKE', FALSE, 3),
        ('DELETE', FALSE, 4),
        ('DROP', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Em uma consulta SQL, a cláusula WHERE filtra as linhas antes do agrupamento, enquanto a cláusula HAVING filtra os grupos formados pelo GROUP BY, podendo utilizar funções de agregação como COUNT e SUM.',
           d.id, b.id, 2026, 'SQL', a.id,
           'Certo. A ordem lógica é FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY. Por isso, condições sobre agregações (por exemplo, COUNT(*) > 5) ficam no HAVING, e não no WHERE.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('SQL')
    WHERE d.nome = 'Banco de Dados'
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
    SELECT 'Entre as propriedades ACID das transações em um SGBD, o isolamento garante que',
           d.id, b.id, 2025, 'Transações', a.id,
           'Isolamento: transações concorrentes não interferem umas nas outras. Não se perder após o commit é durabilidade; tudo ou nada é atomicidade; manter as regras de integridade é consistência. Replicação não é uma propriedade ACID.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Transações')
    WHERE d.nome = 'Banco de Dados'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('a transação leve o banco de um estado válido a outro estado válido, respeitando as restrições de integridade.', FALSE, 1),
        ('a execução concorrente de transações produza o mesmo resultado que se elas fossem executadas uma após a outra, sem que uma veja os efeitos intermediários da outra.', TRUE, 2),
        ('todas as operações de uma transação sejam executadas por completo ou nenhuma delas seja.', FALSE, 3),
        ('os dados sejam replicados em mais de um servidor para tolerar falhas.', FALSE, 4),
        ('as alterações de uma transação confirmada não se percam, mesmo diante de falhas do sistema.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O LEFT JOIN retorna apenas as linhas que possuem correspondência nas duas tabelas envolvidas na junção.',
           d.id, b.id, 2025, 'SQL', a.id,
           'Errado. Retornar só as linhas com correspondência nas duas tabelas é o INNER JOIN. O LEFT JOIN retorna todas as linhas da tabela da esquerda e, quando não há correspondência na da direita, preenche as colunas dela com NULL.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('SQL')
    WHERE d.nome = 'Banco de Dados'
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
    SELECT 'No modelo relacional, a restrição que exige que o valor de um atributo exista como chave primária em outra tabela (ou seja nulo, quando permitido) é a de',
           d.id, b.id, 2025, 'Modelo relacional', a.id,
           'A chave estrangeira garante a integridade referencial: não é possível apontar para um registro inexistente. A integridade de entidade exige chave primária não nula e única; a de domínio limita os valores possíveis do atributo.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Modelo relacional')
    WHERE d.nome = 'Banco de Dados'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('integridade semântica, implementada por visões (views).', FALSE, 1),
        ('integridade de entidade, implementada por chave primária.', FALSE, 2),
        ('integridade referencial, implementada por chave estrangeira.', TRUE, 3),
        ('unicidade, implementada por índice UNIQUE.', FALSE, 4),
        ('integridade de domínio, implementada pelo tipo de dado da coluna.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Usando criptografia assimétrica, Alice deseja enviar a Bob uma mensagem que somente Bob consiga ler. Para isso, Alice deve cifrar a mensagem com a',
           d.id, b.id, 2026, 'Criptografia', a.id,
           'Para confidencialidade, cifra-se com a chave pública do destinatário: só a chave privada dele decifra. Cifrar com a chave privada do remetente serve para assinatura (autenticidade), não para sigilo, já que qualquer um decifra com a chave pública dele.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Criptografia')
    WHERE d.nome = 'Segurança da Informação'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('chave simétrica gerada por Bob e enviada pela internet sem proteção.', FALSE, 1),
        ('chave pública de Alice.', FALSE, 2),
        ('chave privada de Alice.', FALSE, 3),
        ('chave pública de Bob.', TRUE, 4),
        ('chave privada de Bob.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A assinatura digital garante a integridade, a autenticidade e o não repúdio de um documento, mas, por si só, não garante a sua confidencialidade.',
           d.id, b.id, 2026, 'Criptografia', a.id,
           'Certo. A assinatura é feita com a chave privada do autor sobre o hash do documento e pode ser verificada por qualquer pessoa com a chave pública. O conteúdo continua legível; para sigilo, é preciso também cifrar.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Criptografia')
    WHERE d.nome = 'Segurança da Informação'
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
    SELECT 'O tipo de código malicioso que cifra os arquivos da vítima e exige pagamento, geralmente em criptomoeda, para liberar o acesso aos dados é denominado',
           d.id, b.id, 2025, 'Malwares e ataques', a.id,
           'Ransomware sequestra os dados por criptografia e cobra resgate. Worm se propaga sozinho pela rede; spyware coleta informações; rootkit esconde a presença do invasor; keylogger registra o que é digitado.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Malwares e ataques')
    WHERE d.nome = 'Segurança da Informação'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('keylogger', FALSE, 1),
        ('worm', FALSE, 2),
        ('rootkit', FALSE, 3),
        ('spyware', FALSE, 4),
        ('ransomware', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Funções de hash criptográfico, como o SHA-256, são reversíveis quando se conhece a chave correta, o que permite recuperar o dado original a partir do resumo.',
           d.id, b.id, 2025, 'Criptografia', a.id,
           'Errado. Hash não usa chave e é uma função de mão única: não é possível obter o dado original a partir do resumo. Ele serve para verificar integridade e guardar senhas (com salt). Recuperar o original com uma chave é próprio da criptografia, não do hash.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Criptografia')
    WHERE d.nome = 'Segurança da Informação'
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
    SELECT 'O tipo de backup que copia os arquivos alterados desde o último backup completo, de modo que a restauração exige apenas o último backup completo e o último backup desse tipo, é o backup',
           d.id, b.id, 2026, 'Backup', a.id,
           'O diferencial acumula tudo o que mudou desde o último completo; na restauração, bastam o completo e o último diferencial. O incremental copia só o que mudou desde o último backup de qualquer tipo, e a restauração exige o completo e todos os incrementais seguintes.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Backup')
    WHERE d.nome = 'Segurança da Informação'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('diferencial', TRUE, 1),
        ('incremental', FALSE, 2),
        ('completo (full)', FALSE, 3),
        ('espelho', FALSE, 4),
        ('de cópia simples', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A autenticação multifator combina fatores de categorias diferentes, como algo que o usuário sabe e algo que ele possui; exigir duas senhas distintas, por isso, não caracteriza autenticação multifator.',
           d.id, b.id, 2026, 'Controle de acesso', a.id,
           'Certo. As categorias são: algo que se sabe (senha, PIN), algo que se possui (token, celular) e algo que se é (biometria). Duas senhas são dois fatores da mesma categoria.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Controle de acesso')
    WHERE d.nome = 'Segurança da Informação'
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
    SELECT 'No Scrum, o responsável por maximizar o valor do produto e por gerenciar o Product Backlog, incluindo a ordenação dos seus itens, é o',
           d.id, b.id, 2026, 'Metodologias ágeis', a.id,
           'O Product Owner responde pelo valor do produto e pelo Product Backlog. O Scrum Master cuida da eficácia do time e do processo; os Developers constroem o incremento. O Scrum não prevê o papel de gerente de projetos.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Metodologias ágeis')
    WHERE d.nome = 'Engenharia de Software'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('time de desenvolvimento (Developers)', FALSE, 1),
        ('Product Owner', TRUE, 2),
        ('Scrum Master', FALSE, 3),
        ('gerente de projetos', FALSE, 4),
        ('conjunto de stakeholders', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No Scrum, a Sprint Retrospective tem como principal objetivo inspecionar o incremento produzido junto aos stakeholders e adaptar o Product Backlog.',
           d.id, b.id, 2026, 'Metodologias ágeis', a.id,
           'Errado. Inspecionar o incremento com os stakeholders e adaptar o Product Backlog é o objetivo da Sprint Review. A Retrospective avalia como foi a Sprint (pessoas, interações, processos e ferramentas) e planeja melhorias para a próxima.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Metodologias ágeis')
    WHERE d.nome = 'Engenharia de Software'
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
    SELECT 'O modelo de processo de software em cascata caracteriza-se por',
           d.id, b.id, 2025, 'Processos de software', a.id,
           'O cascata é linear e sequencial, com cada fase terminando antes da seguinte. Entregas iterativas são típicas dos métodos ágeis e incrementais; a análise de riscos por ciclo é do modelo espiral (Boehm); protótipos descartáveis, da prototipação.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Processos de software')
    WHERE d.nome = 'Engenharia de Software'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('construção rápida de protótipos descartáveis para validar os requisitos com o usuário.', FALSE, 1),
        ('ausência de documentação, com foco exclusivo no código.', FALSE, 2),
        ('fases sequenciais, em que cada etapa (requisitos, projeto, implementação, testes e implantação) começa após a conclusão da anterior.', TRUE, 3),
        ('ciclos em espiral guiados pela análise de riscos a cada volta.', FALSE, 4),
        ('entregas curtas e frequentes de incrementos funcionais, com requisitos revistos a cada iteração.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A técnica de teste que se baseia no conhecimento da estrutura interna do código, buscando exercitar caminhos, condições e laços do programa, é o teste',
           d.id, b.id, 2026, 'Testes de software', a.id,
           'Caixa-branca usa o código-fonte (cobertura de comandos, desvios e caminhos). Caixa-preta testa entradas e saídas sem olhar o código. Aceitação, regressão e carga referem-se ao objetivo ou ao nível do teste, não à técnica baseada na estrutura interna.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Testes de software')
    WHERE d.nome = 'Engenharia de Software'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('de caixa-preta (funcional)', FALSE, 1),
        ('de regressão', FALSE, 2),
        ('de aceitação', FALSE, 3),
        ('de caixa-branca (estrutural)', TRUE, 4),
        ('de carga', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Requisitos não funcionais descrevem restrições e atributos de qualidade do sistema, como desempenho, segurança, disponibilidade e usabilidade, e não as funções específicas que ele deve executar.',
           d.id, b.id, 2025, 'Requisitos', a.id,
           'Certo. Requisitos funcionais dizem o que o sistema faz (por exemplo, emitir boleto); os não funcionais dizem como ele deve se comportar (tempo de resposta, segurança, disponibilidade, usabilidade).',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Requisitos')
    WHERE d.nome = 'Engenharia de Software'
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
    SELECT 'Na UML, o diagrama que representa a troca de mensagens entre objetos ao longo do tempo, destacando a ordem em que as interações ocorrem, é o diagrama de',
           d.id, b.id, 2025, 'UML', a.id,
           'O diagrama de sequência mostra as mensagens entre objetos em ordem temporal (linhas de vida verticais). O de classes é estrutural; o de casos de uso mostra atores e funcionalidades; os de implantação e de componentes descrevem a arquitetura física e a modular.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('UML')
    WHERE d.nome = 'Engenharia de Software'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('componentes', FALSE, 1),
        ('implantação', FALSE, 2),
        ('casos de uso', FALSE, 3),
        ('classes', FALSE, 4),
        ('sequência', TRUE, 5)
) AS v(texto, correta, ordem);

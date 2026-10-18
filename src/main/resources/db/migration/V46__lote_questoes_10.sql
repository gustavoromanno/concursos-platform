-- Lote 10 de questoes AUTORAIS, gerado por scripts/gerar_questoes.py.
-- Nao edite a mao: altere o script e gere uma nova versao (V16, V17...).
--
-- Questoes escritas no estilo das bancas, nao copiadas de provas (o texto
-- das provas e protegido por direito autoral das bancas).

-- Disciplinas novas
INSERT INTO disciplina (nome) SELECT 'Sistemas Operacionais'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Sistemas Operacionais');
INSERT INTO disciplina (nome) SELECT 'Arquitetura de Computadores'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Arquitetura de Computadores');
INSERT INTO disciplina (nome) SELECT 'Governança de TI'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Governança de TI');
INSERT INTO disciplina (nome) SELECT 'Programação'
WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = 'Programação');

-- Assuntos (so cria os que ainda nao existem)
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Linux: permissões' FROM disciplina d WHERE d.nome = 'Sistemas Operacionais'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Linux: permissões') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Linux: estrutura de diretórios' FROM disciplina d WHERE d.nome = 'Sistemas Operacionais'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Linux: estrutura de diretórios') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Linux: processos' FROM disciplina d WHERE d.nome = 'Sistemas Operacionais'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Linux: processos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Windows: sistemas de arquivos' FROM disciplina d WHERE d.nome = 'Sistemas Operacionais'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Windows: sistemas de arquivos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Gerenciamento de memória' FROM disciplina d WHERE d.nome = 'Sistemas Operacionais'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Gerenciamento de memória') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Gerenciamento de processos' FROM disciplina d WHERE d.nome = 'Sistemas Operacionais'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Gerenciamento de processos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Memórias' FROM disciplina d WHERE d.nome = 'Arquitetura de Computadores'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Memórias') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Armazenamento e RAID' FROM disciplina d WHERE d.nome = 'Arquitetura de Computadores'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Armazenamento e RAID') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Sistemas de numeração' FROM disciplina d WHERE d.nome = 'Arquitetura de Computadores'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Sistemas de numeração') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'ITIL' FROM disciplina d WHERE d.nome = 'Governança de TI'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('ITIL') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'COBIT' FROM disciplina d WHERE d.nome = 'Governança de TI'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('COBIT') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Orientação a objetos' FROM disciplina d WHERE d.nome = 'Programação'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Orientação a objetos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Java' FROM disciplina d WHERE d.nome = 'Programação'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Java') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Estruturas de dados' FROM disciplina d WHERE d.nome = 'Programação'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Estruturas de dados') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Algoritmos' FROM disciplina d WHERE d.nome = 'Programação'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Algoritmos') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Equipamentos e VLAN' FROM disciplina d WHERE d.nome = 'Redes de Computadores'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Equipamentos e VLAN') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Protocolos de aplicação' FROM disciplina d WHERE d.nome = 'Redes de Computadores'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Protocolos de aplicação') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'SQL' FROM disciplina d WHERE d.nome = 'Banco de Dados'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('SQL') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'Índices' FROM disciplina d WHERE d.nome = 'Banco de Dados'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('Índices') AND a.disciplina_id = d.id);
INSERT INTO assunto (disciplina_id, nome)
SELECT d.id, 'LGPD' FROM disciplina d WHERE d.nome = 'Segurança da Informação'
  AND NOT EXISTS (SELECT 1 FROM assunto a
                  WHERE lower(a.nome) = lower('LGPD') AND a.disciplina_id = d.id);

-- Questoes
WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Em um sistema Linux, após o comando chmod 750 relatorio.sh, as permissões do arquivo passam a ser:',
           d.id, b.id, 2026, 'Linux: permissões', a.id,
           'Cada dígito soma leitura (4), escrita (2) e execução (1), na ordem dono, grupo e outros. 7 = 4 + 2 + 1 (rwx); 5 = 4 + 1 (r-x); 0 = nenhuma permissão. Resultado: rwxr-x---.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Linux: permissões')
    WHERE d.nome = 'Sistemas Operacionais'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('dono: leitura, escrita e execução; grupo: leitura e execução; outros: nenhuma.', TRUE, 1),
        ('dono: leitura e escrita; grupo: leitura; outros: nenhuma.', FALSE, 2),
        ('dono: execução; grupo: leitura e execução; outros: leitura, escrita e execução.', FALSE, 3),
        ('dono: leitura, escrita e execução; grupo: nenhuma; outros: leitura e execução.', FALSE, 4),
        ('dono: leitura, escrita e execução; grupo: leitura, escrita e execução; outros: leitura.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No padrão de hierarquia de diretórios do Linux (FHS), os arquivos de configuração do sistema ficam, por padrão, no diretório',
           d.id, b.id, 2025, 'Linux: estrutura de diretórios', a.id,
           '/etc guarda as configurações do sistema (por exemplo, /etc/passwd e /etc/fstab). /bin tem comandos essenciais; /var, dados variáveis como logs; /home, as pastas dos usuários; /tmp, arquivos temporários.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Linux: estrutura de diretórios')
    WHERE d.nome = 'Sistemas Operacionais'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('/home', FALSE, 1),
        ('/etc', TRUE, 2),
        ('/tmp', FALSE, 3),
        ('/bin', FALSE, 4),
        ('/var', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No Linux, o comando ps lista os processos em execução, e o comando kill envia sinais a processos, sendo o sinal 9 (SIGKILL) o que encerra o processo imediatamente, sem permitir que ele trate o sinal.',
           d.id, b.id, 2026, 'Linux: processos', a.id,
           'Certo. O kill sem opção envia o SIGTERM (15), que pede o encerramento e pode ser tratado pelo processo. O kill -9 envia o SIGKILL, que não pode ser capturado nem ignorado.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Linux: processos')
    WHERE d.nome = 'Sistemas Operacionais'
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
    SELECT 'Em comparação com o FAT32, o sistema de arquivos NTFS, usado pelo Windows,',
           d.id, b.id, 2025, 'Windows: sistemas de arquivos', a.id,
           'O NTFS tem listas de controle de acesso (permissões por usuário e grupo), journaling, criptografia (EFS), compactação e suporte a arquivos muito grandes. O limite de 4 GB por arquivo é do FAT32, que é mais simples e compatível com mais dispositivos.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Windows: sistemas de arquivos')
    WHERE d.nome = 'Sistemas Operacionais'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('só pode ser usado em pen drives e cartões de memória.', FALSE, 1),
        ('é limitado a arquivos de no máximo 4 GB, mas é compatível com mais dispositivos.', FALSE, 2),
        ('permite definir permissões de acesso por usuário e armazenar arquivos maiores que 4 GB.', TRUE, 3),
        ('não oferece registro de transações (journaling), o que o torna mais rápido.', FALSE, 4),
        ('não permite criptografia de arquivos nem compactação.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A memória virtual permite que os processos utilizem mais memória do que a RAM física disponível, usando o disco como extensão; quando há trocas excessivas de páginas entre a RAM e o disco, ocorre o fenômeno conhecido como thrashing.',
           d.id, b.id, 2025, 'Gerenciamento de memória', a.id,
           'Certo. A memória virtual pagina partes dos processos para o disco. Se a RAM é insuficiente para o conjunto de páginas em uso, o sistema passa mais tempo trocando páginas do que executando, e o desempenho despenca (thrashing).',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Gerenciamento de memória')
    WHERE d.nome = 'Sistemas Operacionais'
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
    SELECT 'O algoritmo de escalonamento de processos que atribui a cada processo uma fatia fixa de tempo de CPU (quantum), atendendo-os em fila circular, é o',
           d.id, b.id, 2026, 'Gerenciamento de processos', a.id,
           'No Round Robin, cada processo roda por um quantum e, se não terminar, volta ao fim da fila. O FIFO atende por ordem de chegada até o fim; o SJF escolhe o menor job; o EDF, o prazo mais próximo.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Gerenciamento de processos')
    WHERE d.nome = 'Sistemas Operacionais'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('FIFO (First In, First Out)', FALSE, 1),
        ('SJF (Shortest Job First)', FALSE, 2),
        ('EDF (Earliest Deadline First)', FALSE, 3),
        ('Round Robin', TRUE, 4),
        ('por prioridade não preemptivo', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A memória principal do computador, volátil, cujo conteúdo se perde quando o equipamento é desligado, é a',
           d.id, b.id, 2025, 'Memórias', a.id,
           'A RAM é volátil e guarda os programas e dados em uso. ROM e EEPROM (firmware), a memória flash dos SSDs e os discos rígidos são não voláteis: mantêm os dados sem energia.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Memórias')
    WHERE d.nome = 'Arquitetura de Computadores'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('disco rígido (HD)', FALSE, 1),
        ('memória flash de um SSD', FALSE, 2),
        ('EEPROM do firmware', FALSE, 3),
        ('ROM', FALSE, 4),
        ('RAM', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O nível de RAID que distribui os dados e a paridade entre todos os discos do conjunto, tolera a falha de um disco e exige no mínimo três discos é o',
           d.id, b.id, 2026, 'Armazenamento e RAID', a.id,
           'RAID 5 usa paridade distribuída: com 3 ou mais discos, sobrevive à perda de um. RAID 0 só divide os dados (sem redundância); RAID 1 espelha; RAID 6 tem paridade dupla (aguenta dois discos, mínimo quatro); RAID 10 combina espelhamento e divisão.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Armazenamento e RAID')
    WHERE d.nome = 'Arquitetura de Computadores'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('RAID 5', TRUE, 1),
        ('RAID 0', FALSE, 2),
        ('RAID 10', FALSE, 3),
        ('RAID 1', FALSE, 4),
        ('RAID 6', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A memória cache, mais rápida e mais cara que a RAM, armazena dados e instruções usados com frequência pelo processador, reduzindo o tempo médio de acesso à memória.',
           d.id, b.id, 2026, 'Memórias', a.id,
           'Certo. A cache fica entre o processador e a RAM (níveis L1, L2 e L3) e explora o princípio da localidade: o que foi usado recentemente, ou está perto disso, tende a ser usado de novo.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Memórias')
    WHERE d.nome = 'Arquitetura de Computadores'
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
    SELECT 'O número decimal 45, representado no sistema binário, é',
           d.id, b.id, 2025, 'Sistemas de numeração', a.id,
           '45 = 32 + 8 + 4 + 1 = 2⁵ + 2³ + 2² + 2⁰. Marcando as posições de 2⁵ a 2⁰: 1 0 1 1 0 1 → 101101.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Sistemas de numeração')
    WHERE d.nome = 'Arquitetura de Computadores'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('111001', FALSE, 1),
        ('101101', TRUE, 2),
        ('100101', FALSE, 3),
        ('110101', FALSE, 4),
        ('101011', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No ITIL 4, o propósito da prática de gerenciamento de incidente é',
           d.id, b.id, 2026, 'ITIL', a.id,
           'Incidente foca em restaurar o serviço rápido. Causa raiz e erros conhecidos são do gerenciamento de problema; autorizar mudanças, da habilitação de mudança; itens de configuração, do gerenciamento de configuração; pedidos padronizados, do gerenciamento de requisição de serviço.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('ITIL')
    WHERE d.nome = 'Governança de TI'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('identificar a causa raiz dos incidentes e eliminar erros conhecidos.', FALSE, 1),
        ('avaliar e autorizar mudanças antes de sua implantação.', FALSE, 2),
        ('minimizar o impacto negativo dos incidentes, restaurando a operação normal do serviço o mais rápido possível.', TRUE, 3),
        ('manter informações sobre os itens de configuração e seus relacionamentos.', FALSE, 4),
        ('atender pedidos padronizados dos usuários, como acesso e informações.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'No ITIL, um problema é a causa, ou causa potencial, de um ou mais incidentes, e o gerenciamento de problemas busca reduzir a probabilidade e o impacto de incidentes, identificando suas causas reais.',
           d.id, b.id, 2025, 'ITIL', a.id,
           'Certo. Enquanto o incidente cuida de restabelecer o serviço, o problema investiga a causa. Uma causa identificada e ainda não corrigida, com solução de contorno documentada, é um erro conhecido.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('ITIL')
    WHERE d.nome = 'Governança de TI'
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
    SELECT 'No COBIT 2019, os objetivos de governança estão agrupados no domínio',
           d.id, b.id, 2026, 'COBIT', a.id,
           'O COBIT separa governança e gestão. A governança fica no EDM; os domínios de gestão são APO, BAI, DSS e MEA.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('COBIT')
    WHERE d.nome = 'Governança de TI'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('MEA – Monitorar, Avaliar e Analisar', FALSE, 1),
        ('BAI – Construir, Adquirir e Implementar', FALSE, 2),
        ('DSS – Entregar, Servir e Suportar', FALSE, 3),
        ('EDM – Avaliar, Dirigir e Monitorar', TRUE, 4),
        ('APO – Alinhar, Planejar e Organizar', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Segundo o COBIT, governança e gestão são a mesma disciplina, ambas exercidas diretamente pela equipe técnica de TI.',
           d.id, b.id, 2026, 'COBIT', a.id,
           'Errado. O COBIT distingue as duas: a governança (normalmente a alta administração, o conselho) avalia, dirige e monitora; a gestão planeja, constrói, executa e monitora as atividades conforme a direção dada pela governança.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('COBIT')
    WHERE d.nome = 'Governança de TI'
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
    SELECT 'Em orientação a objetos, o princípio que consiste em ocultar os atributos de uma classe, permitindo o acesso a eles apenas por meio de métodos públicos, é denominado',
           d.id, b.id, 2025, 'Orientação a objetos', a.id,
           'Encapsulamento esconde o estado interno (atributos private) e expõe operações controladas (getters, setters e outros métodos). Herança reaproveita uma classe; polimorfismo permite o mesmo método com comportamentos diferentes; sobrecarga são métodos de mesmo nome com parâmetros diferentes.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Orientação a objetos')
    WHERE d.nome = 'Programação'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('sobrecarga', FALSE, 1),
        ('polimorfismo', FALSE, 2),
        ('herança', FALSE, 3),
        ('acoplamento', FALSE, 4),
        ('encapsulamento', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Considere o trecho em Java: int x = 7; int y = 2; System.out.println(x / y + x % y); O valor exibido é',
           d.id, b.id, 2026, 'Java', a.id,
           'Entre inteiros, a divisão descarta a parte decimal: 7 / 2 = 3. O operador % devolve o resto: 7 % 2 = 1. Logo, 3 + 1 = 4.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Java')
    WHERE d.nome = 'Programação'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('4', TRUE, 1),
        ('5', FALSE, 2),
        ('3.5', FALSE, 3),
        ('4.5', FALSE, 4),
        ('3', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Em Java, uma classe pode estender apenas uma superclasse, mas pode implementar várias interfaces.',
           d.id, b.id, 2026, 'Java', a.id,
           'Certo. Java não tem herança múltipla de classes (extends aceita uma só), mas uma classe pode implementar quantas interfaces quiser (implements A, B, C).',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Java')
    WHERE d.nome = 'Programação'
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
    SELECT 'A estrutura de dados em que o último elemento inserido é o primeiro a ser removido (LIFO) é a',
           d.id, b.id, 2025, 'Estruturas de dados', a.id,
           'Pilha é LIFO (push e pop no topo). Fila é FIFO: o primeiro que entra é o primeiro que sai. Listas, árvores e tabelas hash não têm essa regra fixa de remoção.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Estruturas de dados')
    WHERE d.nome = 'Programação'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('tabela hash', FALSE, 1),
        ('pilha', TRUE, 2),
        ('árvore binária de busca', FALSE, 3),
        ('lista duplamente encadeada', FALSE, 4),
        ('fila', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'A complexidade de tempo, no pior caso, da busca binária em um vetor ordenado de n elementos é',
           d.id, b.id, 2026, 'Algoritmos', a.id,
           'A cada comparação, a busca binária descarta metade do vetor: n, n/2, n/4... até 1, o que leva cerca de log₂ n passos. A busca sequencial é que é O(n).',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Algoritmos')
    WHERE d.nome = 'Programação'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('O(n)', FALSE, 1),
        ('O(n log n)', FALSE, 2),
        ('O(log n)', TRUE, 3),
        ('O(1)', FALSE, 4),
        ('O(n²)', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O recurso que permite dividir logicamente um mesmo switch em domínios de broadcast distintos, isolando, por exemplo, a rede administrativa da rede de visitantes, é a',
           d.id, b.id, 2026, 'Equipamentos e VLAN', a.id,
           'VLANs segmentam a rede na camada 2: cada uma é um domínio de broadcast separado, e a comunicação entre elas passa por um roteador ou switch de camada 3. NAT traduz endereços; VPN cria um túnel seguro; DHCP distribui IPs; proxy intermedia requisições.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Equipamentos e VLAN')
    WHERE d.nome = 'Redes de Computadores'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('VPN', FALSE, 1),
        ('proxy', FALSE, 2),
        ('NAT', FALSE, 3),
        ('VLAN', TRUE, 4),
        ('DHCP', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'O HTTPS utiliza o protocolo TLS para cifrar a comunicação entre o navegador e o servidor e opera, por padrão, na porta 443.',
           d.id, b.id, 2026, 'Protocolos de aplicação', a.id,
           'Certo. O HTTP comum usa a porta 80, sem criptografia. O HTTPS é o HTTP sobre TLS (sucessor do SSL), na porta 443.',
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
    SELECT 'A consulta SELECT setor, COUNT(*) FROM funcionario GROUP BY setor HAVING COUNT(*) > 5 retorna',
           d.id, b.id, 2025, 'SQL', a.id,
           'GROUP BY forma um grupo por setor e COUNT(*) conta as linhas de cada um. O HAVING filtra os grupos depois da contagem, mantendo só os setores com mais de cinco funcionários.',
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
        ('todos os setores, exibindo a quantidade somente quando ela passa de cinco.', FALSE, 1),
        ('apenas os cinco primeiros setores em ordem alfabética.', FALSE, 2),
        ('os funcionários que trabalham em mais de cinco setores.', FALSE, 3),
        ('o total geral de funcionários, desde que seja maior que cinco.', FALSE, 4),
        ('cada setor com mais de cinco funcionários, acompanhado da sua quantidade de funcionários.', TRUE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Criar um índice em uma coluna sempre torna mais rápidas tanto as consultas quanto as operações de inserção e de atualização da tabela.',
           d.id, b.id, 2025, 'Índices', a.id,
           'Errado. O índice acelera buscas e ordenações pela coluna, mas cada inserção, atualização ou exclusão também precisa atualizar o índice, o que deixa essas operações mais lentas e ocupa espaço.',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('Índices')
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
    SELECT 'Segundo a Lei Geral de Proteção de Dados (Lei nº 13.709/2018), é exemplo de dado pessoal sensível o dado',
           d.id, b.id, 2026, 'LGPD', a.id,
           'Dado sensível (art. 5º, II) é o que trata de origem racial ou étnica, convicção religiosa, opinião política, filiação sindical ou a organização religiosa, filosófica ou política, saúde, vida sexual, dado genético ou biométrico. Nome, e-mail, CPF e telefone são dados pessoais comuns.',
           'MULTIPLA_ESCOLHA'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('fcc', 'fundação carlos chagas', 'fundacao carlos chagas'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('LGPD')
    WHERE d.nome = 'Segurança da Informação'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('referente à saúde do titular.', TRUE, 1),
        ('do nome completo do titular.', FALSE, 2),
        ('do número de telefone do titular.', FALSE, 3),
        ('do endereço de e-mail do titular.', FALSE, 4),
        ('do número de CPF do titular.', FALSE, 5)
) AS v(texto, correta, ordem);

WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT 'Na LGPD, o encarregado é a pessoa indicada pelo controlador e pelo operador para atuar como canal de comunicação entre o controlador, os titulares dos dados e a Autoridade Nacional de Proteção de Dados (ANPD).',
           d.id, b.id, 2026, 'LGPD', a.id,
           'Certo. É a definição do art. 5º, VIII, da LGPD (redação dada pela Lei nº 13.853/2019). O encarregado é muitas vezes chamado de DPO (Data Protection Officer).',
           'CERTO_ERRADO'
    FROM disciplina d
    JOIN banca b ON b.id = (SELECT MIN(x.id) FROM banca x WHERE lower(x.nome) IN ('cespe/cebraspe', 'cebraspe', 'cespe'))
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower('LGPD')
    WHERE d.nome = 'Segurança da Informação'
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
        ('Certo', TRUE, 1),
        ('Errado', FALSE, 2)
) AS v(texto, correta, ordem);

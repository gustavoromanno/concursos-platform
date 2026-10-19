-- Teoria por assunto, lote 2: resumos AUTORAIS gerados por scripts/gerar_teoria.py.
-- Nao edite a mao. Para corrigir um texto ja publicado, use o editor no painel de admin.
-- So insere se o assunto existir e ainda nao tiver resumo (rodar de novo nao duplica).

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Crase: quando usar, quando é proibida e quando é opcional',
       'Crase é a fusão da preposição **a** com o artigo feminino **a(s)** ou com o "a" inicial de **aquele(s), aquela(s), aquilo**. Só existe crase quando as duas coisas estão presentes: um termo que exige a preposição e uma palavra que aceita o artigo.

## O teste do masculino
Troque a palavra feminina por uma masculina. Se aparecer **ao**, há crase.
- Fui à praia → Fui **ao** clube (crase)
- Comprei a blusa → Comprei **o** casaco (sem crase: não há preposição)

## Crase obrigatória
- Horas exatas: **às 10h**, **à uma hora** (aqui "uma" é número).
- Locuções femininas: **às vezes**, **à noite**, **à medida que**, **à custa de**, **à vista**.
- "À moda de", mesmo subentendido: bife **à** milanesa, sapato **à** Luís XV.
- Distância determinada: ficou **à** distância de 10 metros.

## Crase proibida
- Antes de palavra masculina (fora o "à moda"): andar **a** cavalo.
- Antes de verbo: **a** partir de hoje, começou **a** estudar.
- Antes de pronomes pessoais e da maioria dos de tratamento: entreguei **a** ela, **a** Vossa Excelência.
- Entre palavras repetidas: cara **a** cara, gota **a** gota.
- "A" no singular antes de palavra no plural: falei **a** pessoas estranhas.
- Antes de "uma" artigo indefinido: dirigiu-se **a** uma colega.
- "Casa" (o próprio lar) e "terra" (chão firme) sem especificação: voltei **a** casa; desceram **a** terra.

## Lugares
Use o teste do "voltar": **volto da** → crase; **volto de** → sem crase.
- Vou **à** Bahia (volto **da** Bahia).
- Vou **a** Brasília (volto **de** Brasília), mas: vou **à** Brasília de JK (especificada).

## Crase facultativa
- Antes de pronome possessivo feminino no singular: **à** minha casa / **a** minha casa.
- Antes de nome próprio feminino de pessoa: entreguei **à** Maria / **a** Maria.
- Depois de "até": fui até **à** porta / até **a** porta.',
       TRUE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Língua Portuguesa' AND lower(a2.nome) = lower('Crase'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Concordância verbal: as regras que mais caem',
       '**Regra geral:** o verbo concorda com o **núcleo do sujeito** em número e pessoa, esteja o sujeito antes ou depois do verbo. Ache o sujeito perguntando "quem/o que" ao verbo e ignore os termos intercalados.
- **Chegaram**, ontem à noite, os documentos do processo.

## Sujeito composto
- Antes do verbo: plural. O diretor e a secretária **saíram**.
- Depois do verbo: plural ou concordância com o núcleo mais próximo. **Saiu** (ou **saíram**) o diretor e a secretária.
- Pessoas diferentes: a 1ª prevalece sobre as outras, e a 2ª sobre a 3ª. Eu e você **iremos**.

## Verbos impessoais (ficam no singular)
- **Haver** no sentido de existir ou de tempo decorrido: **Havia** muitos candidatos. **Há** dois anos não o vejo. O auxiliar também fica no singular: **Deve haver** soluções.
- **Fazer** indicando tempo: **Faz** dez anos que mudei.
- Atenção: **existir** não é impessoal. **Existem** muitos candidatos.

## Partícula "se"
- Voz passiva sintética (verbo transitivo direto): o termo é sujeito, e o verbo concorda. **Vendem-se** casas. **Alugam-se** salas.
- Índice de indeterminação do sujeito (verbo com preposição): singular sempre. **Precisa-se** de técnicos. **Trata-se** de questões difíceis.

## Casos especiais
- **A maioria dos / grande parte de** + plural: singular ou plural. A maioria dos alunos **faltou** / **faltaram**.
- **Mais de um**: singular. Mais de um candidato **desistiu**.
- **Porcentagem**: concorda com o número ou com o termo que vem depois. 1% dos eleitores **votou** / **votaram**; 30% da turma **faltou**.
- **Um dos que**: o plural é o preferido pelas bancas. Ele é um dos que mais **estudam**.
- **Ser** entre pessoa e coisa concorda com a pessoa: O problema **são** as provas (com coisa no plural, prefere-se o plural).',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Língua Portuguesa' AND lower(a2.nome) = lower('Concordância verbal'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Regência verbal: os verbos campeões de prova',
       'Regência é a relação entre o verbo e o seu complemento: com preposição (**transitivo indireto**, VTI), sem preposição (**transitivo direto**, VTD) ou com os dois.

## Verbos que mudam de sentido conforme a regência
| Verbo | Sentido | Regência | Exemplo |
|---|---|---|---|
| Assistir | ver, presenciar | VTI (a) | Assisti **ao** jogo. |
| Assistir | ajudar, dar assistência | VTD | O médico assistiu **o** paciente. |
| Aspirar | desejar, almejar | VTI (a) | Aspira **ao** cargo. |
| Aspirar | inspirar, sorver | VTD | Aspirou **o** ar puro. |
| Visar | pretender, ter em vista | VTI (a) | O projeto visa **ao** lucro. |
| Visar | mirar ou pôr visto | VTD | Visou **o** alvo; visou **o** cheque. |

## Outros que caem sempre
- **Obedecer / desobedecer**: VTI (a). Obedeça **às** normas.
- **Preferir**: preferir uma coisa **a** outra. Prefiro estudar **a** sair. Errado: "prefiro mais", "prefiro... do que".
- **Implicar** (acarretar): VTD na norma tradicional. A decisão implica **riscos** (sem "em").
- **Chegar / ir** (movimento): preposição **a**. Chegou **ao** trabalho (a norma culta não aceita "chegou no").
- **Pagar / perdoar**: coisa sem preposição, pessoa com "a". Paguei **a conta ao** banco. Perdoou **o erro ao** colega.
- **Esquecer / lembrar**: sem pronome, VTD (esqueci **o** livro); com pronome, preposição "de" (esqueci-me **do** livro).
- **Namorar**: VTD. Ela namora **o** vizinho (sem "com").
- **Proceder** (realizar): VTI (a). Procedeu-se **à** análise dos recursos.
- **Informar / avisar**: quem informa, informa algo a alguém ou alguém de algo. Informei **o fato ao** cliente / Informei **o cliente do** fato.

## Pegadinha frequente: regência e crase juntas
Se o verbo pede "a" e o complemento é feminino com artigo, há crase: assistir **à** palestra, obedecer **à** lei, proceder **à** votação.',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Língua Portuguesa' AND lower(a2.nome) = lower('Regência verbal'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Pontuação: o uso da vírgula sem medo',
       '## A regra de ouro
**Não se separa por vírgula** o sujeito do verbo, nem o verbo dos seus complementos, mesmo quando o sujeito é longo.
- Errado: Os candidatos aprovados na primeira fase**,** devem enviar os documentos.
- Certo: Os candidatos aprovados na primeira fase devem enviar os documentos.

## Usa-se vírgula para
- **Adjunto adverbial deslocado** (facultativa se for curto): Ontem**,** saímos cedo / Ontem saímos cedo. Se for longo, prefira a vírgula.
- **Aposto explicativo**: Brasília**,** a capital federal**,** foi inaugurada em 1960.
- **Vocativo**: Senhores**,** a sessão está aberta.
- **Oração adjetiva explicativa**: Os servidores**,** que estudaram**,** foram aprovados (todos estudaram).
- **Orações coordenadas** sem conjunção ou com "mas, porém, contudo, pois, portanto": Estudou muito**,** mas não passou.
- **Conjunção deslocada**: Ele**,** porém**,** não compareceu.
- **Omissão de verbo (zeugma)**: Eu fiquei em casa; ela**,** no trabalho.

## Vírgula que muda o sentido
- Sem vírgula (**restritiva**): Os servidores que estudaram foram aprovados → só os que estudaram.
- Com vírgula (**explicativa**): Os servidores, que estudaram, foram aprovados → todos estudaram.

## E o "e"?
Normalmente não leva vírgula. Leva quando liga orações com **sujeitos diferentes** (O pai saiu**,** e o filho ficou), quando tem valor adversativo (Estudou**,** e não passou) e na repetição enfática (e chora**,** e grita**,** e pede).

## Outros sinais
- **Ponto e vírgula**: separa itens longos de uma enumeração e orações coordenadas extensas.
- **Dois-pontos**: anuncia enumeração, citação ou explicação.
- **Travessão e parênteses**: isolam comentários, como a vírgula, com mais destaque (travessão) ou menos (parênteses).',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Língua Portuguesa' AND lower(a2.nome) = lower('Pontuação'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Colocação pronominal: próclise, ênclise e mesóclise',
       'Trata da posição dos pronomes oblíquos átonos (**me, te, se, o, a, lhe, nos, vos**) em relação ao verbo.
- **Próclise**: antes do verbo (não **me** disseram).
- **Ênclise**: depois do verbo, com hífen (disseram-**me**).
- **Mesóclise**: no meio do verbo (dir-**me**-ão).

## Próclise obrigatória: as palavras atrativas
- Palavras negativas: **não, nunca, jamais, ninguém**. Não **se** esqueça.
- Pronomes relativos: o livro que **me** emprestaste.
- Conjunções subordinativas: Quando **o** encontrei, saí.
- Advérbios sem vírgula depois: Aqui **se** trabalha. (Com vírgula: Aqui, trabalha-se.)
- Pronomes indefinidos e interrogativos: Alguém **me** chamou? Quem **te** contou?
- Frases que exprimem desejo: Deus **te** abençoe.

## Ênclise
- No **início da frase**: a norma culta não começa oração com pronome átono. Empreste-**me** o livro (e não "Me empreste").
- Depois de pausa (vírgula): Se puder, ajude-**me**.
- No **imperativo afirmativo**: Faça-**o** agora.
- No **gerúndio** sem a preposição "em": Saiu, deixando-**nos** preocupados.

## Mesóclise
Só com **futuro do presente** ou **futuro do pretérito**, sem palavra atrativa antes:
- Far-**se**-á a revisão. Dir-**te**-ia a verdade.
- Com palavra atrativa, volta a próclise: Não **se** fará a revisão.
- Nunca ênclise no futuro: "fará-se" e "faria-se" estão errados.

## Locuções verbais
- Com infinitivo ou gerúndio: depois do auxiliar com hífen ou depois do principal. Quero-**lhe** falar / Quero falar-**lhe**.
- Com **particípio**: nunca depois do particípio. Tinha-**me** dito (e não "tinha dito-me").',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Língua Portuguesa' AND lower(a2.nome) = lower('Colocação pronominal'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Proposições e conectivos: a base da lógica de prova',
       '**Proposição** é uma frase declarativa que pode ser classificada como **verdadeira ou falsa**, nunca as duas.

## O que não é proposição
- Perguntas: "Que horas são?"
- Ordens e pedidos: "Estude mais."
- Exclamações: "Que dia lindo!"
- Sentenças abertas, com variável: "x + 2 = 5".
- Paradoxos: "Esta frase é falsa."

## Os conectivos e quando cada um é verdadeiro
| Conectivo | Símbolo | Leitura | É verdadeiro quando |
|---|---|---|---|
| Negação | ~p | não p | p é falsa |
| Conjunção | p ∧ q | p e q | as duas são verdadeiras |
| Disjunção | p ∨ q | p ou q | pelo menos uma é verdadeira |
| Disjunção exclusiva | p ⊻ q | ou p, ou q | exatamente uma é verdadeira |
| Condicional | p → q | se p, então q | não acontecer "V então F" |
| Bicondicional | p ↔ q | p se e somente se q | as duas têm o mesmo valor |

## O condicional, que mais derruba
**p → q só é falso quando p é verdadeiro e q é falso.**
"Se chover, levo o guarda-chuva": só há mentira se chover e eu não levar. Se não chover, a frase é verdadeira, leve eu ou não.

## Condição suficiente e necessária
Em **p → q**:
- p é condição **suficiente** para q;
- q é condição **necessária** para p.
"Ser aprovado é suficiente para ser nomeado" = "Se é aprovado, então é nomeado".

## Tabela-verdade
Com **n** proposições simples, a tabela tem **2ⁿ linhas** (3 proposições → 8 linhas).
- **Tautologia**: verdadeira em todas as linhas (p ∨ ~p).
- **Contradição**: falsa em todas (p ∧ ~p).
- **Contingência**: às vezes verdadeira, às vezes falsa.',
       TRUE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Raciocínio Lógico' AND lower(a2.nome) = lower('Proposições e conectivos'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Negação de proposições: De Morgan, condicional e quantificadores',
       'Negar é escrever a frase que é **sempre o oposto** da original: verdadeira quando ela é falsa, e falsa quando ela é verdadeira.

## Leis de De Morgan
- ~(p **e** q) = ~p **ou** ~q
- ~(p **ou** q) = ~p **e** ~q

Exemplo: "Estudo e trabalho" → negação: "**Não** estudo **ou não** trabalho".

## Negação do condicional (a mais cobrada)
**~(p → q) = p e ~q.** Mantém a primeira, **troca o "se... então" por "e"** e nega a segunda.
- "Se chove, levo o guarda-chuva" → "**Chove e não** levo o guarda-chuva".
- Erro clássico: "Se não chove, não levo" **não** é a negação.

## Negação do bicondicional
~(p ↔ q) = **ou p, ou q** (disjunção exclusiva), o mesmo que p ↔ ~q.

## Quantificadores
| Frase | Negação |
|---|---|
| Todo A é B | **Algum** A **não** é B |
| Algum A é B | **Nenhum** A é B |
| Nenhum A é B | **Algum** A é B |
| Algum A não é B | **Todo** A é B |

Pegadinha: a negação de "Todo servidor é pontual" **não** é "Nenhum servidor é pontual". Basta um não ser: "**Algum** servidor **não** é pontual" (ou "Existe servidor que não é pontual").

## Dica para conferir
Monte a situação em que a original é falsa: a negação correta tem que descrever exatamente essa situação.',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Raciocínio Lógico' AND lower(a2.nome) = lower('Negação de proposições'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Equivalências lógicas: o que diz a mesma coisa',
       'Duas proposições são **equivalentes** quando têm a mesma tabela-verdade: sempre verdadeiras e falsas juntas.

## As três formas do condicional
**p → q ≡ ~q → ~p ≡ ~p ou q**
- "Se estudo, passo."
- ≡ "Se não passo, não estudei." (**contrapositiva**: inverte e nega as duas)
- ≡ "Não estudo **ou** passo." (nega a primeira, troca por "ou", mantém a segunda)

## As que parecem, mas não são
- **Recíproca**, q → p: "Se passo, estudei". Não é equivalente.
- **Inversa**, ~p → ~q: "Se não estudo, não passo". Não é equivalente.
As bancas adoram colocar essas duas entre as alternativas.

## Bicondicional
p ↔ q ≡ (p → q) **e** (q → p) ≡ ~p ↔ ~q.

## Outras úteis
- Dupla negação: ~(~p) ≡ p.
- Comutativas: p e q ≡ q e p; p ou q ≡ q ou p (o condicional **não** é comutativo).
- Distributivas: p e (q ou r) ≡ (p e q) ou (p e r).
- "Ou... ou" (exclusivo) ≡ ~(p ↔ q).

## Passo a passo para questões
1. Traduza as frases para símbolos.
2. Se for condicional, teste as duas formas: contrapositiva e "~p ou q".
3. Desconfie de alternativas que só trocam a ordem (recíproca) ou só negam (inversa).',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Raciocínio Lógico' AND lower(a2.nome) = lower('Equivalências lógicas'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Análise combinatória: arranjo ou combinação?',
       '## Princípio fundamental da contagem
Se uma escolha tem **m** opções e outra, independente, tem **n**, as duas juntas têm **m × n** possibilidades.
Exemplo: 3 camisas e 4 calças → 12 combinações de roupa.

## Fatorial
n! = n × (n − 1) × ... × 1. Por definição, 0! = 1. Exemplo: 5! = 120.

## Permutação: todos os elementos, em ordem
- **Simples**: Pₙ = n!. Anagramas de AMOR: 4! = 24.
- **Com repetição**: n! dividido pelo fatorial de cada repetição. ARARA: 5! / (3! × 2!) = 10.
- **Circular** (pessoas em volta de uma mesa): (n − 1)!.

## Arranjo × combinação: o teste da troca
Pegue um resultado e **troque a ordem** dos escolhidos:
- Se virou **outro** resultado → a ordem importa → **arranjo**.
- Se continua o **mesmo** resultado → a ordem não importa → **combinação**.

| | Fórmula | Exemplo com 5 pessoas, escolhendo 3 |
|---|---|---|
| Arranjo | A(n,p) = n! / (n − p)! | Presidente, vice e secretário: 5 × 4 × 3 = **60** |
| Combinação | C(n,p) = n! / [p! (n − p)!] | Comissão de 3 membros: 60 / 3! = **10** |

## Dicas de prova
- "Comissão", "grupo", "equipe", "subconjunto" → combinação.
- "Fila", "senha", "pódio", "cargos diferentes", "número de algarismos distintos" → arranjo ou princípio da contagem.
- "Pelo menos um": calcule o total e subtraia os casos com nenhum.',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Raciocínio Lógico' AND lower(a2.nome) = lower('Análise combinatória'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Redes e internet: protocolos, navegadores e nuvem',
       '## Internet, intranet e extranet
- **Internet**: rede pública mundial.
- **Intranet**: rede privada de uma organização, que usa as **mesmas tecnologias** da internet (HTTP, navegador).
- **Extranet**: parte da intranet liberada para parceiros externos, como fornecedores e clientes.

## Protocolos que mais caem
| Protocolo | Função | Porta padrão |
|---|---|---|
| HTTP | páginas web sem criptografia | 80 |
| HTTPS | páginas web com criptografia (TLS) | 443 |
| SMTP | **envio** de e-mail | 25 / 587 |
| POP3 | **recebimento** de e-mail, baixando para o computador | 110 / 995 |
| IMAP | recebimento mantendo as mensagens **sincronizadas no servidor** | 143 / 993 |
| FTP | transferência de arquivos | 20 / 21 |
| DNS | traduz nomes (site.com.br) em endereços IP | 53 |
| DHCP | entrega IP automaticamente aos dispositivos | 67 / 68 |

## URL
**https://www.site.gov.br/editais/2026.pdf** → protocolo (https), domínio (www.site.gov.br) e caminho do recurso (/editais/2026.pdf).

## Navegadores
- **Cookies**: pequenos arquivos que o site grava para lembrar login e preferências.
- **Cache**: cópia local de páginas e imagens para carregar mais rápido.
- **Navegação anônima/privativa**: não grava histórico, cookies e senhas **no seu computador**, mas **não esconde** a navegação do provedor, da empresa ou dos sites visitados.

## Computação em nuvem
- **SaaS**: software pronto, usado pelo navegador (Gmail, Microsoft 365).
- **PaaS**: plataforma para desenvolver e publicar aplicações.
- **IaaS**: infraestrutura (servidores, armazenamento) sob demanda.

## Busca no Google
- **"frase exata"** entre aspas; **-palavra** exclui; **site:gov.br** limita ao domínio; **filetype:pdf** busca só PDFs.',
       TRUE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Noções de Informática' AND lower(a2.nome) = lower('Redes e internet'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Segurança da informação: malwares, ataques e defesas',
       '## Pilares
- **Confidencialidade**: só quem tem permissão acessa.
- **Integridade**: a informação não é alterada indevidamente.
- **Disponibilidade**: está acessível quando necessário.
Também cobrados: **autenticidade** (garantia da origem) e **não repúdio** (o autor não pode negar a autoria).

## Malwares
| Tipo | Característica principal |
|---|---|
| Vírus | infecta arquivos e precisa ser **executado** para agir e se espalhar |
| Worm | se **propaga sozinho** pela rede, explorando falhas, sem precisar de hospedeiro |
| Cavalo de Troia | parece um programa legítimo, mas executa ações maliciosas escondidas |
| Ransomware | **criptografa** os dados e cobra resgate |
| Spyware / keylogger | espiona; o keylogger registra o que é digitado |
| Botnet | rede de máquinas infectadas, controladas a distância (bots) |
| Rootkit | esconde a presença do invasor no sistema |

## Ataques
- **Phishing**: mensagem falsa que imita banco, órgão ou empresa para roubar dados.
- **Engenharia social**: explora a confiança das pessoas, não falhas técnicas.
- **DoS / DDoS**: sobrecarrega um serviço até deixá-lo indisponível (DDoS usa muitas máquinas).
- **Força bruta**: tenta senhas em sequência até acertar.

## Defesas
- **Antivírus**: detecta e remove malware.
- **Firewall**: filtra o tráfego da rede conforme regras. **Não** é antivírus e não remove vírus.
- **Autenticação multifator**: combina algo que você sabe, algo que tem e algo que é.
- **Backup**: completo (tudo), incremental (o que mudou desde o último backup de qualquer tipo) e diferencial (o que mudou desde o último completo).
- Atualizações do sistema e dos programas fecham falhas exploradas por ataques.

## Criptografia em uma linha
Simétrica usa **uma** chave; assimétrica usa um **par** (pública e privada). A assinatura digital garante integridade, autenticidade e não repúdio; o certificado digital (ICP-Brasil) liga a chave pública a uma pessoa ou empresa.',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Noções de Informática' AND lower(a2.nome) = lower('Segurança da informação'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Planilhas: referências, funções e erros',
       '## Referências: o que muda ao copiar a fórmula
| Tipo | Exemplo | Ao copiar |
|---|---|---|
| Relativa | A1 | linha e coluna se ajustam |
| Absoluta | $A$1 | nada muda |
| Mista | $A1 ou A$1 | só a parte sem $ se ajusta |

Exemplo: em C1, **=A1*$B$1**. Copiada para C2, vira **=A2*$B$1**. Copiada para D1, vira **=B1*$B$1**.

## Funções mais cobradas (Excel em português)
- **=SOMA(A1:A10)**, **=MÉDIA(...)**, **=MÁXIMO(...)**, **=MÍNIMO(...)**
- **=CONT.NÚM(A1:A10)**: conta só as células com **números**.
- **=CONT.VALORES(A1:A10)**: conta as células **não vazias**, de qualquer tipo.
- **=CONT.SE(A1:A10;">7")** e **=SOMASE(A1:A10;"Pago";B1:B10)**: contam ou somam com critério.
- **=SE(A1>=7;"Aprovado";"Reprovado")**
- **=PROCV(valor;tabela;coluna;FALSO)**: busca na 1ª coluna da tabela e devolve a coluna indicada; FALSO = correspondência exata.

## Sinais
- **:** indica intervalo (A1:A5 = de A1 a A5). **;** separa argumentos (A1;A5 = só as duas células).
- Precedência: primeiro a potência (^), depois multiplicação e divisão, por fim soma e subtração. Use parênteses para mudar a ordem.
- **&** junta textos: =A1&" "&B1.

## Erros
- **#DIV/0!**: divisão por zero.
- **#N/D**: valor não encontrado (comum no PROCV).
- **#REF!**: referência a uma célula que foi apagada.
- **#NOME?**: nome de função escrito errado.
- **#VALOR!**: tipo de dado errado, como somar texto.

## Excel × LibreOffice Calc
A ideia é a mesma. Diferença que cai: a referência a outra planilha é **Plan1!A1** no Excel e **$Planilha1.A1** no Calc.',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Noções de Informática' AND lower(a2.nome) = lower('Planilhas eletrônicas'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

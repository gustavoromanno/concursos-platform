"""
Gera a migration Flyway com os resumos de TEORIA por assunto (textos autorais).

Uso:
    python scripts/gerar_teoria.py <lote> <arquivo de saida>
    python scripts/gerar_teoria.py 1 src/main/resources/db/migration/V42__teoria_lote_1.sql

Nunca altere um lote ja aplicado (o Flyway recusa checksum diferente): para
corrigir um texto ja publicado, use o editor de teoria no painel de admin,
ou crie um lote novo que atualize o resumo.

Formato do conteudo (o site converte na hora de exibir):
    ## Subtitulo
    Paragrafo comum (linhas em branco separam paragrafos)
    - item de lista
    **negrito**
"""
import sys

BANC, MFIN = "Conhecimentos Bancários", "Matemática Financeira"
PORT, RLM, INFO = "Língua Portuguesa", "Raciocínio Lógico", "Noções de Informática"


def resumo(disc, assunto, titulo, gratuito, conteudo):
    return dict(disc=disc, assunto=assunto, titulo=titulo, gratuito=gratuito, conteudo=conteudo.strip())


LOTE_1 = [
    resumo(BANC, "Sistema Financeiro Nacional", "Sistema Financeiro Nacional: quem regula, quem fiscaliza, quem opera", True, """
O Sistema Financeiro Nacional (SFN) é o conjunto de órgãos e instituições que transferem recursos de quem poupa para quem precisa de crédito. As provas cobram principalmente **quem faz o quê**, organizado em três níveis.

## 1. Órgãos normativos (criam as regras)
- **Conselho Monetário Nacional (CMN):** órgão máximo do sistema. É formado pelo Ministro da Fazenda (que o preside), pelo Ministro do Planejamento e Orçamento e pelo Presidente do Banco Central. Define as diretrizes das políticas monetária, de crédito e cambial e fixa a meta de inflação.
- **CNSP:** normas de seguros, capitalização e previdência aberta.
- **CNPC:** normas da previdência complementar fechada (fundos de pensão).

## 2. Supervisores (fiscalizam e executam)
- **Banco Central do Brasil (Bacen):** executa a política monetária, emite a moeda, fiscaliza bancos, cooperativas de crédito e demais instituições financeiras, e é a Secretaria-Executiva do CMN. Tem autonomia definida pela Lei Complementar 179/2021.
- **Comissão de Valores Mobiliários (CVM):** regula e fiscaliza o mercado de valores mobiliários, como ações, debêntures e cotas de fundos.
- **Susep:** seguros, capitalização, previdência aberta e resseguros.
- **Previc:** fundos de pensão (previdência fechada).

## 3. Operadores
Bancos, caixas econômicas, cooperativas de crédito, corretoras, distribuidoras, bolsa de valores, seguradoras e entidades de previdência, entre outros.

## Pegadinhas frequentes
- O CMN **não executa** nem fiscaliza: ele normatiza. Quem executa é o Banco Central.
- A CVM **não fiscaliza bancos**; cuida do mercado de valores mobiliários.
- Fundos de pensão são da **Previc**; previdência aberta é da **Susep**.
"""),
    resumo(BANC, "Política monetária", "Política monetária: Copom, Selic e os instrumentos do Banco Central", False, """
A política monetária controla a quantidade de dinheiro e o custo do crédito na economia, com o objetivo principal de manter a **estabilidade de preços** (inflação na meta).

## Quem decide o quê
- O **CMN** fixa a meta de inflação.
- O **Copom**, comitê do Banco Central, define a **meta da taxa Selic**, o principal instrumento para levar a inflação à meta. O Copom se reúne oito vezes por ano.

## Como a Selic afeta a economia
Selic mais alta encarece o crédito e torna a poupança mais atrativa. O consumo e o investimento diminuem, a demanda cai e a inflação tende a ceder. Selic mais baixa tem o efeito contrário: estimula a economia, com risco de mais inflação.

## Instrumentos clássicos
- **Operações de mercado aberto (open market):** compra e venda de títulos públicos pelo Banco Central. **Comprar** títulos coloca dinheiro na economia (expansionista); **vender** retira dinheiro (contracionista). É o instrumento usado no dia a dia para manter a Selic perto da meta.
- **Recolhimento compulsório:** parte dos depósitos que os bancos precisam deixar no Banco Central. **Aumentar** o compulsório reduz o dinheiro disponível para empréstimos (contracionista).
- **Redesconto:** empréstimos do Banco Central aos bancos para cobrir falta de liquidez. Redesconto mais caro ou com prazo menor é contracionista.

## Para não confundir
- Política **monetária** (Banco Central: juros, moeda, crédito) é diferente de política **fiscal** (governo: impostos e gastos).
- Em prova, associe: **contracionista** = menos liquidez = combate à inflação.
"""),
    resumo(BANC, "Garantias do sistema financeiro", "FGC: o que o Fundo Garantidor de Créditos cobre", True, """
O **Fundo Garantidor de Créditos (FGC)** é uma entidade privada, sem fins lucrativos, mantida por contribuições das próprias instituições financeiras associadas. Ele devolve ao cliente, até um limite, os valores aplicados em produtos cobertos quando a instituição sofre intervenção ou liquidação decretada pelo Banco Central.

## Limites
- **Até R$ 250 mil por CPF ou CNPJ, por instituição ou conglomerado financeiro.** Valores em instituições do mesmo conglomerado somam para esse limite.
- **Teto global de R$ 1 milhão** por titular, considerando os pagamentos feitos pelo FGC em um período de quatro anos.

## O que é coberto
- Depósitos à vista (conta corrente) e **poupança**
- **CDB** e RDB
- **LCI** e **LCA**
- Letras de câmbio e letras hipotecárias

## O que não é coberto
- **Fundos de investimento**
- **Ações**
- **Debêntures**, CRI e CRA
- **Títulos públicos do Tesouro Direto** (o risco é do Tesouro Nacional, não do FGC)
- Previdência privada (PGBL e VGBL)

## Pegadinhas frequentes
- O FGC **não é órgão público** nem é mantido pelo governo.
- O limite é **por instituição**: R$ 200 mil na conta e R$ 200 mil num CDB do mesmo banco têm cobertura de R$ 250 mil no total, e não de R$ 400 mil.
- Cooperativas de crédito têm um fundo próprio, o **FGCoop**.
"""),
    resumo(BANC, "Prevenção à lavagem de dinheiro", "Lavagem de dinheiro: etapas, Coaf e deveres das instituições", False, """
Lavagem de dinheiro é dar aparência lícita a recursos obtidos com crimes. A lei brasileira sobre o tema é a **Lei 9.613/1998**.

## As três etapas clássicas
1. **Colocação:** o dinheiro ilícito entra no sistema econômico (depósitos, compra de bens, câmbio).
2. **Ocultação (ou estratificação):** várias operações sucessivas para dificultar o rastreamento da origem.
3. **Integração:** o recurso volta à economia com aparência legítima, por exemplo em investimentos ou empresas.

## Coaf
O **Conselho de Controle de Atividades Financeiras (Coaf)** é a unidade de inteligência financeira do Brasil. Recebe e analisa as comunicações de operações suspeitas e as encaminha às autoridades competentes. Desde a Lei 13.974/2020, é **vinculado administrativamente ao Banco Central**, com autonomia técnica e operacional.

## Deveres das instituições financeiras
- **Conhecer o cliente:** identificar e manter os dados cadastrais atualizados.
- **Manter registros** das operações.
- **Comunicar ao Coaf** as operações suspeitas e as previstas na regulamentação.
- **Não avisar o cliente** de que ele foi comunicado: a comunicação é sigilosa.

## Pegadinhas frequentes
- As etapas não precisam ocorrer todas, nem nessa ordem, para o crime existir.
- Quem comunica é a instituição; quem analisa é o Coaf; quem investiga e processa são a polícia e o Ministério Público.
"""),
    resumo(BANC, "Sistema de Pagamentos Brasileiro", "Sistema de Pagamentos Brasileiro e o Pix", False, """
O **Sistema de Pagamentos Brasileiro (SPB)** reúne as regras, os sistemas e as instituições que fazem o dinheiro circular entre contas: transferências, boletos, cartões e o Pix. O Banco Central regula e supervisiona o sistema.

## Pix
- Arranjo de **pagamentos instantâneos** criado e regulamentado pelo Banco Central.
- Funciona **24 horas por dia, 7 dias por semana**, inclusive em fins de semana e feriados.
- A transação é concluída em poucos segundos.
- Usa **chaves** para identificar a conta: CPF ou CNPJ, e-mail, número de celular ou chave aleatória.
- A liquidação acontece no **SPI** (Sistema de Pagamentos Instantâneos), operado pelo Banco Central.

## Outros meios
- **TED:** transferência entre bancos diferentes, com crédito no mesmo dia, em horário definido.
- **Boleto:** documento de cobrança pago em qualquer banco ou correspondente até o vencimento.
- **Cartões:** de débito (sai na hora da conta) e de crédito (pagamento na fatura).

## STR
O **Sistema de Transferência de Reservas (STR)**, operado pelo Banco Central, liquida em tempo real as transferências entre as reservas bancárias das instituições, uma a uma, sem compensação.

## Pegadinhas frequentes
- Pix e TED não são a mesma coisa: o Pix funciona todos os dias, a qualquer hora.
- A chave Pix identifica a conta, mas **não é** o número da conta.
"""),
    resumo(MFIN, "Juros simples", "Juros simples: fórmula, unidades e exemplos", True, """
No regime de **juros simples**, os juros de cada período são calculados sempre sobre o **capital inicial**. Por isso, o montante cresce em linha reta.

## Fórmulas
- **J = C × i × t**
- **M = C + J = C × (1 + i × t)**

Onde C é o capital, i é a taxa, t é o tempo, J são os juros e M é o montante.

## Regra de ouro: mesma unidade
Taxa e tempo precisam estar na **mesma unidade**. Com taxa ao mês, use o tempo em meses. Em juros simples, converter é proporcional: 2% ao mês correspondem a 24% ao ano.

## Exemplo
Capital de R$ 7.500,00 a 1,5% ao mês por 8 meses:
J = 7.500 × 0,015 × 8 = **R$ 900,00**
M = 7.500 + 900 = **R$ 8.400,00**

## Encontrando o capital
Se a questão dá os juros e pede o capital, isole C: **C = J / (i × t)**.
Exemplo: J = R$ 900,00, i = 1,5% ao mês, t = 8 meses → C = 900 / 0,12 = R$ 7.500,00.

## Pegadinha frequente
Em juros simples, taxas proporcionais são equivalentes (12% ao ano = 1% ao mês). Em juros compostos, **não**.
"""),
    resumo(MFIN, "Juros compostos", "Juros compostos: o juro sobre juro", False, """
No regime de **juros compostos**, os juros de cada período entram no saldo e passam a render juros no período seguinte: é o **juro sobre juro**. O montante cresce de forma exponencial.

## Fórmula
- **M = C × (1 + i)ⁿ**
- **J = M − C**

## Exemplo
R$ 10.000,00 a 2% ao mês por 3 meses:
M = 10.000 × 1,02³ = 10.000 × 1,061208 = **R$ 10.612,08**
Em juros simples, o mesmo caso daria R$ 10.600,00. A diferença de R$ 12,08 são os juros sobre juros.

## Simples ou compostos?
- Para **um único período**, os dois regimes dão o mesmo resultado.
- Para **mais de um período**, os compostos rendem mais.
- Para **menos de um período** (fração), os simples rendem mais.

## Dica de prova
As bancas costumam fornecer uma tabela de potências, como (1,02)³ = 1,0612. Leia o enunciado procurando esses valores antes de fazer as contas na mão.
"""),
    resumo(MFIN, "Taxas equivalentes", "Taxas equivalentes, nominais, efetivas e taxa real", False, """
## Taxas equivalentes
Duas taxas são **equivalentes** quando, aplicadas ao mesmo capital pelo mesmo prazo, produzem o mesmo montante. Em juros compostos, a conversão é por potência:
**(1 + i_anual) = (1 + i_mensal)¹²**

Exemplo: 10% ao semestre equivalem a (1,10)² − 1 = **21% ao ano**, e não 20%.

## Taxa nominal e taxa efetiva
- **Nominal:** o período da taxa é diferente do período de capitalização. Exemplo: 12% ao ano com capitalização mensal.
- Para usar, transforme em taxa do período de capitalização de forma **proporcional**: 12% ao ano ÷ 12 = 1% ao mês.
- **Efetiva:** a taxa que realmente incide. No exemplo: (1,01)¹² − 1 ≈ **12,68% ao ano**.

## Taxa real (fórmula de Fisher)
Desconta a inflação do rendimento nominal:
**(1 + nominal) = (1 + real) × (1 + inflação)**

Exemplo: rendimento de 10,25% com inflação de 5%:
1,1025 / 1,05 = 1,05 → taxa real de **5%**.
Subtrair as taxas (10,25% − 5% = 5,25%) é só uma aproximação e costuma ser a alternativa errada.
"""),
    resumo(MFIN, "Descontos", "Descontos: comercial (por fora) e racional (por dentro)", False, """
Desconto é o abatimento por antecipar o pagamento ou o resgate de um título. **N** é o valor nominal (no vencimento), **A** é o valor atual (pago hoje) e **D = N − A** é o desconto.

## Desconto comercial simples (por fora)
A taxa incide sobre o **valor nominal**:
- **D = N × i × n**
- **A = N × (1 − i × n)**

Exemplo: N = R$ 5.000,00, 2% ao mês, 3 meses antes do vencimento.
D = 5.000 × 0,02 × 3 = R$ 300,00 → A = **R$ 4.700,00**

## Desconto racional simples (por dentro)
A taxa incide sobre o **valor atual**:
- **A = N / (1 + i × n)**

Mesmo exemplo: A = 5.000 / 1,06 ≈ **R$ 4.716,98** (desconto de R$ 283,02).

## Desconto racional composto
- **A = N / (1 + i)ⁿ**

## Como não errar
- **Por fora** (comercial): o desconto é **maior**, porque a base de cálculo é o nominal.
- **Por dentro** (racional): o desconto é **menor**, porque a base é o valor atual.
- Se a questão não disser o tipo, procure as palavras "comercial", "bancário", "por fora", "racional" ou "por dentro".
"""),
    resumo(MFIN, "Sistemas de amortização", "SAC e Price: como cada sistema paga a dívida", False, """
Toda prestação tem duas partes: **juros** (sobre o saldo devedor) e **amortização** (o que de fato abate a dívida).
**Prestação = Juros + Amortização**

## SAC: Sistema de Amortização Constante
- A **amortização é sempre a mesma**: A = valor financiado ÷ número de parcelas.
- Os juros caem a cada mês, porque o saldo diminui.
- Por isso, a **prestação é decrescente**.

Exemplo: R$ 120.000,00 em 24 parcelas, 1% ao mês.
Amortização = R$ 5.000,00.
1ª prestação: 5.000 + 1% de 120.000 = **R$ 6.200,00**
2ª prestação: 5.000 + 1% de 115.000 = **R$ 6.150,00**
As prestações caem R$ 50,00 por mês (1% de 5.000).

## Price (Sistema Francês)
- A **prestação é constante**: PMT = PV × i / [1 − (1 + i)⁻ⁿ].
- Como os juros caem, a **amortização cresce** a cada mês.

Exemplo: R$ 10.000,00 em 2 parcelas, 2% ao mês.
PMT ≈ R$ 5.150,50. Juros do 1º mês: R$ 200,00. Amortização: **R$ 4.950,50**.

## Comparação rápida
| | SAC | Price |
|---|---|---|
| Prestação | decrescente | constante |
| Amortização | constante | crescente |
| Juros | decrescentes | decrescentes |
| Total de juros pagos | menor | maior |
"""),
]


LOTE_2 = [
    # ------------------------------------------------------------------ Língua Portuguesa
    resumo(PORT, "Crase", "Crase: quando usar, quando é proibida e quando é opcional", True, """
Crase é a fusão da preposição **a** com o artigo feminino **a(s)** ou com o "a" inicial de **aquele(s), aquela(s), aquilo**. Só existe crase quando as duas coisas estão presentes: um termo que exige a preposição e uma palavra que aceita o artigo.

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
- Depois de "até": fui até **à** porta / até **a** porta.
"""),
    resumo(PORT, "Concordância verbal", "Concordância verbal: as regras que mais caem", False, """
**Regra geral:** o verbo concorda com o **núcleo do sujeito** em número e pessoa, esteja o sujeito antes ou depois do verbo. Ache o sujeito perguntando "quem/o que" ao verbo e ignore os termos intercalados.
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
- **Ser** entre pessoa e coisa concorda com a pessoa: O problema **são** as provas (com coisa no plural, prefere-se o plural).
"""),
    resumo(PORT, "Regência verbal", "Regência verbal: os verbos campeões de prova", False, """
Regência é a relação entre o verbo e o seu complemento: com preposição (**transitivo indireto**, VTI), sem preposição (**transitivo direto**, VTD) ou com os dois.

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
Se o verbo pede "a" e o complemento é feminino com artigo, há crase: assistir **à** palestra, obedecer **à** lei, proceder **à** votação.
"""),
    resumo(PORT, "Pontuação", "Pontuação: o uso da vírgula sem medo", False, """
## A regra de ouro
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
- **Travessão e parênteses**: isolam comentários, como a vírgula, com mais destaque (travessão) ou menos (parênteses).
"""),
    resumo(PORT, "Colocação pronominal", "Colocação pronominal: próclise, ênclise e mesóclise", False, """
Trata da posição dos pronomes oblíquos átonos (**me, te, se, o, a, lhe, nos, vos**) em relação ao verbo.
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
- Com **particípio**: nunca depois do particípio. Tinha-**me** dito (e não "tinha dito-me").
"""),

    # ------------------------------------------------------------------ Raciocínio Lógico
    resumo(RLM, "Proposições e conectivos", "Proposições e conectivos: a base da lógica de prova", True, """
**Proposição** é uma frase declarativa que pode ser classificada como **verdadeira ou falsa**, nunca as duas.

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
- **Contingência**: às vezes verdadeira, às vezes falsa.
"""),
    resumo(RLM, "Negação de proposições", "Negação de proposições: De Morgan, condicional e quantificadores", False, """
Negar é escrever a frase que é **sempre o oposto** da original: verdadeira quando ela é falsa, e falsa quando ela é verdadeira.

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
Monte a situação em que a original é falsa: a negação correta tem que descrever exatamente essa situação.
"""),
    resumo(RLM, "Equivalências lógicas", "Equivalências lógicas: o que diz a mesma coisa", False, """
Duas proposições são **equivalentes** quando têm a mesma tabela-verdade: sempre verdadeiras e falsas juntas.

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
3. Desconfie de alternativas que só trocam a ordem (recíproca) ou só negam (inversa).
"""),
    resumo(RLM, "Análise combinatória", "Análise combinatória: arranjo ou combinação?", False, """
## Princípio fundamental da contagem
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
- "Pelo menos um": calcule o total e subtraia os casos com nenhum.
"""),

    # ------------------------------------------------------------------ Noções de Informática
    resumo(INFO, "Redes e internet", "Redes e internet: protocolos, navegadores e nuvem", True, """
## Internet, intranet e extranet
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
- **"frase exata"** entre aspas; **-palavra** exclui; **site:gov.br** limita ao domínio; **filetype:pdf** busca só PDFs.
"""),
    resumo(INFO, "Segurança da informação", "Segurança da informação: malwares, ataques e defesas", False, """
## Pilares
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
Simétrica usa **uma** chave; assimétrica usa um **par** (pública e privada). A assinatura digital garante integridade, autenticidade e não repúdio; o certificado digital (ICP-Brasil) liga a chave pública a uma pessoa ou empresa.
"""),
    resumo(INFO, "Planilhas eletrônicas", "Planilhas: referências, funções e erros", False, """
## Referências: o que muda ao copiar a fórmula
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
A ideia é a mesma. Diferença que cai: a referência a outra planilha é **Plan1!A1** no Excel e **$Planilha1.A1** no Calc.
"""),
]


LOTES = {"1": LOTE_1, "2": LOTE_2}


def sql(texto):
    return "'" + texto.replace("'", "''") + "'"


def gerar(lote):
    linhas = [
        f"-- Teoria por assunto, lote {lote}: resumos AUTORAIS gerados por scripts/gerar_teoria.py.",
        "-- Nao edite a mao. Para corrigir um texto ja publicado, use o editor no painel de admin.",
        "-- So insere se o assunto existir e ainda nao tiver resumo (rodar de novo nao duplica).",
        "",
    ]
    for r in LOTES[lote]:
        linhas.append(f"""INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, {sql(r['titulo'])},
       {sql(r['conteudo'])},
       {'TRUE' if r['gratuito'] else 'FALSE'}
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = {sql(r['disc'])} AND lower(a2.nome) = lower({sql(r['assunto'])}))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);
""")
    return "\n".join(linhas)


if __name__ == "__main__":
    if len(sys.argv) != 3 or sys.argv[1] not in LOTES:
        print(f"uso: python scripts/gerar_teoria.py <lote: {', '.join(LOTES)}> <arquivo de saida>")
        sys.exit(1)
    lote, destino = sys.argv[1], sys.argv[2]
    with open(destino, "w", encoding="utf-8", newline="\n") as f:
        f.write(gerar(lote))
    qs = LOTES[lote]
    print(f"{destino}: {len(qs)} resumos ({sum(r['gratuito'] for r in qs)} gratuitos)")

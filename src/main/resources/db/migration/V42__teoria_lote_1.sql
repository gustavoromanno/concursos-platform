-- Teoria por assunto, lote 1: resumos AUTORAIS gerados por scripts/gerar_teoria.py.
-- Nao edite a mao. Para corrigir um texto ja publicado, use o editor no painel de admin.
-- So insere se o assunto existir e ainda nao tiver resumo (rodar de novo nao duplica).

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Sistema Financeiro Nacional: quem regula, quem fiscaliza, quem opera',
       'O Sistema Financeiro Nacional (SFN) é o conjunto de órgãos e instituições que transferem recursos de quem poupa para quem precisa de crédito. As provas cobram principalmente **quem faz o quê**, organizado em três níveis.

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
- Fundos de pensão são da **Previc**; previdência aberta é da **Susep**.',
       TRUE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Conhecimentos Bancários' AND lower(a2.nome) = lower('Sistema Financeiro Nacional'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Política monetária: Copom, Selic e os instrumentos do Banco Central',
       'A política monetária controla a quantidade de dinheiro e o custo do crédito na economia, com o objetivo principal de manter a **estabilidade de preços** (inflação na meta).

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
- Em prova, associe: **contracionista** = menos liquidez = combate à inflação.',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Conhecimentos Bancários' AND lower(a2.nome) = lower('Política monetária'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'FGC: o que o Fundo Garantidor de Créditos cobre',
       'O **Fundo Garantidor de Créditos (FGC)** é uma entidade privada, sem fins lucrativos, mantida por contribuições das próprias instituições financeiras associadas. Ele devolve ao cliente, até um limite, os valores aplicados em produtos cobertos quando a instituição sofre intervenção ou liquidação decretada pelo Banco Central.

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
- Cooperativas de crédito têm um fundo próprio, o **FGCoop**.',
       TRUE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Conhecimentos Bancários' AND lower(a2.nome) = lower('Garantias do sistema financeiro'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Lavagem de dinheiro: etapas, Coaf e deveres das instituições',
       'Lavagem de dinheiro é dar aparência lícita a recursos obtidos com crimes. A lei brasileira sobre o tema é a **Lei 9.613/1998**.

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
- Quem comunica é a instituição; quem analisa é o Coaf; quem investiga e processa são a polícia e o Ministério Público.',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Conhecimentos Bancários' AND lower(a2.nome) = lower('Prevenção à lavagem de dinheiro'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Sistema de Pagamentos Brasileiro e o Pix',
       'O **Sistema de Pagamentos Brasileiro (SPB)** reúne as regras, os sistemas e as instituições que fazem o dinheiro circular entre contas: transferências, boletos, cartões e o Pix. O Banco Central regula e supervisiona o sistema.

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
- A chave Pix identifica a conta, mas **não é** o número da conta.',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Conhecimentos Bancários' AND lower(a2.nome) = lower('Sistema de Pagamentos Brasileiro'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Juros simples: fórmula, unidades e exemplos',
       'No regime de **juros simples**, os juros de cada período são calculados sempre sobre o **capital inicial**. Por isso, o montante cresce em linha reta.

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
Em juros simples, taxas proporcionais são equivalentes (12% ao ano = 1% ao mês). Em juros compostos, **não**.',
       TRUE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Matemática Financeira' AND lower(a2.nome) = lower('Juros simples'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Juros compostos: o juro sobre juro',
       'No regime de **juros compostos**, os juros de cada período entram no saldo e passam a render juros no período seguinte: é o **juro sobre juro**. O montante cresce de forma exponencial.

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
As bancas costumam fornecer uma tabela de potências, como (1,02)³ = 1,0612. Leia o enunciado procurando esses valores antes de fazer as contas na mão.',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Matemática Financeira' AND lower(a2.nome) = lower('Juros compostos'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Taxas equivalentes, nominais, efetivas e taxa real',
       '## Taxas equivalentes
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
Subtrair as taxas (10,25% − 5% = 5,25%) é só uma aproximação e costuma ser a alternativa errada.',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Matemática Financeira' AND lower(a2.nome) = lower('Taxas equivalentes'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'Descontos: comercial (por fora) e racional (por dentro)',
       'Desconto é o abatimento por antecipar o pagamento ou o resgate de um título. **N** é o valor nominal (no vencimento), **A** é o valor atual (pago hoje) e **D = N − A** é o desconto.

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
- Se a questão não disser o tipo, procure as palavras "comercial", "bancário", "por fora", "racional" ou "por dentro".',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Matemática Financeira' AND lower(a2.nome) = lower('Descontos'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

INSERT INTO resumo_assunto (assunto_id, titulo, conteudo, gratuito)
SELECT a.id, 'SAC e Price: como cada sistema paga a dívida',
       'Toda prestação tem duas partes: **juros** (sobre o saldo devedor) e **amortização** (o que de fato abate a dívida).
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
| Total de juros pagos | menor | maior |',
       FALSE
FROM assunto a
WHERE a.id = (SELECT MIN(a2.id) FROM assunto a2 JOIN disciplina d ON d.id = a2.disciplina_id
              WHERE d.nome = 'Matemática Financeira' AND lower(a2.nome) = lower('Sistemas de amortização'))
  AND NOT EXISTS (SELECT 1 FROM resumo_assunto x WHERE x.assunto_id = a.id);

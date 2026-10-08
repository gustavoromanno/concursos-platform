"""
Gera uma migration Flyway com um lote de questoes AUTORAIS.

Uso:
    python scripts/gerar_questoes.py <lote> <arquivo de saida>
    python scripts/gerar_questoes.py 4 src/main/resources/db/migration/V24__lote_questoes_4.sql

O numero da migration (V15, V18...) nao precisa seguir o numero do lote: vale o
proximo numero livre na pasta de migrations no momento em que o lote e criado.

Cada lote e uma lista em LOTES. Para um lote novo, crie LOTE_4 e registre em LOTES.
Nunca altere um lote ja aplicado: regenerar deve produzir o MESMO arquivo, senao
o Flyway recusa a subida (checksum diferente).

Formato de cada questao:
    ME (multipla escolha): alternativas = [texto_correto, errada1, errada2, ...]
       A ordem de exibicao e sorteada aqui, de forma deterministica (mesma
       entrada gera sempre o mesmo arquivo), para a correta nao ficar na "A".
    CE (certo/errado): gabarito = True (Certo) ou False (Errado).
"""
import hashlib
import random
import sys

# Disciplinas novas de cada lote (as existentes sao ignoradas pelo NOT EXISTS).
DISCIPLINAS_POR_LOTE = {
    "2": ["Direito Constitucional", "Noções de Informática"],
    "3": [],
    "4": ["Conhecimentos Bancários", "Matemática Financeira"],
    "5": ["Ética no Serviço Público"],
    "6": ["Economia", "Estatística"],
    "7": [],
}

# Cargo do concurso de exemplo que passa a cobrar tambem as disciplinas novas.
CARGO_EXEMPLO = "Analista Administrativo"

CESPE, FGV = "CESPE/CEBRASPE", "FGV"
BANC, MFIN = "Conhecimentos Bancários", "Matemática Financeira"
ETICA = "Ética no Serviço Público"
ECON, EST = "Economia", "Estatística"
PORT, DADM, DCON, INFO, RLM = (
    "Língua Portuguesa", "Direito Administrativo", "Direito Constitucional",
    "Noções de Informática", "Raciocínio Lógico",
)


def me(disc, assunto, banca, ano, enunciado, alternativas, explicacao):
    return dict(tipo="MULTIPLA_ESCOLHA", disc=disc, assunto=assunto, banca=banca, ano=ano,
                enunciado=enunciado, alternativas=alternativas, explicacao=explicacao)


def ce(disc, assunto, banca, ano, enunciado, gabarito, explicacao):
    return dict(tipo="CERTO_ERRADO", disc=disc, assunto=assunto, banca=banca, ano=ano,
                enunciado=enunciado, gabarito=gabarito, explicacao=explicacao)


LOTE_2 = [
    # ------------------------------------------------------------------
    # Direito Constitucional
    # ------------------------------------------------------------------
    me(DCON, "Remédios constitucionais", FGV, 2024,
       "O remédio constitucional cabível para assegurar o conhecimento de informações relativas à pessoa do impetrante, constantes de registros ou bancos de dados de entidades governamentais ou de caráter público, é o",
       ["habeas data.", "mandado de injunção.", "mandado de segurança.", "habeas corpus.", "ação popular."],
       "Art. 5º, LXXII, da CF: o habeas data assegura o conhecimento e a retificação de informações sobre a pessoa do impetrante em registros públicos. O mandado de injunção supre a falta de norma regulamentadora; o mandado de segurança protege direito líquido e certo não amparado por habeas corpus ou habeas data."),
    me(DCON, "Remédios constitucionais", CESPE, 2023,
       "Nos termos da Constituição Federal, tem legitimidade para propor ação popular que vise anular ato lesivo ao patrimônio público",
       ["qualquer cidadão.", "qualquer pessoa física ou jurídica.", "somente o Ministério Público.",
        "partido político com representação no Congresso Nacional.", "associação constituída há pelo menos um ano."],
       "Art. 5º, LXXIII: qualquer cidadão, isto é, o eleitor no gozo dos direitos políticos. Pessoa jurídica não tem legitimidade (Súmula 365 do STF). Partido político e associação são legitimados do mandado de segurança coletivo, não da ação popular."),
    me(DCON, "Administração Pública na Constituição", FGV, 2023,
       "A Constituição Federal veda a acumulação remunerada de cargos públicos, mas a admite, havendo compatibilidade de horários, no caso de",
       ["dois cargos de professor.", "dois cargos técnicos ou científicos.",
        "um cargo de professor com dois cargos técnicos.", "três cargos privativos de profissionais de saúde.",
        "um cargo de juiz com dois cargos de professor."],
       "Art. 37, XVI: é permitida a acumulação de dois cargos de professor; de um cargo de professor com outro técnico ou científico; ou de dois cargos ou empregos privativos de profissionais de saúde com profissões regulamentadas. O limite é sempre de dois cargos."),
    me(DCON, "Administração Pública na Constituição", CESPE, 2024,
       "De acordo com a Constituição Federal, o prazo de validade do concurso público será de",
       ["até dois anos, prorrogável uma vez, por igual período.", "até quatro anos, improrrogável.",
        "dois anos, prorrogável duas vezes, por igual período.", "até um ano, prorrogável por mais seis meses.",
        "cinco anos, contados da homologação do resultado."],
       "Art. 37, III, da CF: o concurso tem validade de até dois anos, prorrogável uma única vez por igual período. O edital fixa o prazo dentro desse limite."),
    me(DCON, "Organização do Estado", FGV, 2025,
       "Nos termos do art. 18 da Constituição Federal, a organização político-administrativa da República Federativa do Brasil compreende, todos autônomos,",
       ["a União, os Estados, o Distrito Federal e os Municípios.",
        "a União, os Estados, o Distrito Federal, os Municípios e os Territórios.",
        "a União e os Estados, apenas.", "a União, os Estados e os Municípios, apenas.",
        "a União, os Estados e o Distrito Federal, apenas."],
       "Art. 18: União, Estados, Distrito Federal e Municípios são os entes federativos autônomos. Os Territórios, quando existirem, integram a União e não têm autonomia."),
    me(DCON, "Poder Legislativo", CESPE, 2023,
       "Quanto à composição do Senado Federal, é correto afirmar que cada Estado e o Distrito Federal elegem",
       ["três senadores, com mandato de oito anos.", "três senadores, com mandato de quatro anos.",
        "dois senadores, com mandato de oito anos.", "número de senadores proporcional à população.",
        "quatro senadores, com mandato de quatro anos."],
       "Art. 46 da CF: o Senado compõe-se de representantes dos Estados e do DF eleitos pelo princípio majoritário; cada um elege três senadores, com mandato de oito anos. A representação é renovada a cada quatro anos, alternadamente, por um e dois terços."),
    ce(DCON, "Direitos fundamentais", CESPE, 2024,
       "A casa é asilo inviolável do indivíduo, mas nela é possível penetrar sem o consentimento do morador, durante o dia, por determinação judicial.",
       True,
       "Art. 5º, XI: sem consentimento do morador, só se entra na casa em flagrante delito, desastre ou para prestar socorro (a qualquer hora), ou, durante o dia, por determinação judicial."),
    ce(DCON, "Direitos fundamentais", CESPE, 2025,
       "Havendo determinação judicial, é permitido ingressar na casa do indivíduo durante a noite, sem o consentimento do morador, para cumprir mandado de busca e apreensão.",
       False,
       "A ordem judicial só autoriza o ingresso durante o dia (art. 5º, XI). À noite, sem consentimento, apenas em flagrante delito, desastre ou para prestar socorro."),
    ce(DCON, "Controle de constitucionalidade", CESPE, 2023,
       "Compete ao Supremo Tribunal Federal processar e julgar, originariamente, a ação direta de inconstitucionalidade de lei ou ato normativo federal ou estadual.",
       True,
       "Art. 102, I, a, da CF. Lei municipal em face da Constituição Federal não é objeto de ADI; pode ser questionada por ADPF ou no controle difuso."),
    ce(DCON, "Nacionalidade", CESPE, 2024,
       "São brasileiros natos os nascidos no estrangeiro, de pai brasileiro ou de mãe brasileira, desde que qualquer deles esteja a serviço da República Federativa do Brasil.",
       True,
       "Art. 12, I, b, da CF. Estar a serviço do Brasil basta; não é necessário registro nem opção posterior, exigidos apenas nas hipóteses da alínea c."),
    ce(DCON, "Direitos sociais", CESPE, 2025,
       "Entre os direitos sociais previstos no art. 6º da Constituição Federal estão a alimentação, a moradia e o transporte.",
       True,
       "O art. 6º lista educação, saúde, alimentação, trabalho, moradia, transporte, lazer, segurança, previdência social, proteção à maternidade e à infância e assistência aos desamparados. O transporte foi incluído pela EC 90/2015."),
    ce(DCON, "Nacionalidade", CESPE, 2023,
       "Os cargos de Presidente e de Vice-Presidente da República podem ser ocupados por brasileiros naturalizados, desde que residentes no país há mais de quinze anos.",
       False,
       "Art. 12, § 3º: são privativos de brasileiro nato os cargos de Presidente e Vice-Presidente da República, entre outros (presidentes da Câmara e do Senado, ministros do STF, carreira diplomática, oficial das Forças Armadas e Ministro de Estado da Defesa)."),

    # ------------------------------------------------------------------
    # Noções de Informática
    # ------------------------------------------------------------------
    me(INFO, "Segurança da informação", FGV, 2024,
       "O tipo de código malicioso que criptografa os dados do computador da vítima e exige pagamento, geralmente em criptomoeda, para restabelecer o acesso é denominado",
       ["ransomware.", "spyware.", "worm.", "adware.", "keylogger."],
       "Ransomware sequestra os dados por criptografia e cobra resgate. Spyware monitora as atividades do usuário; worm se propaga sozinho pela rede; adware exibe propaganda; keylogger captura o que é digitado."),
    me(INFO, "Segurança da informação", CESPE, 2023,
       "A técnica em que o golpista se passa por instituição confiável, por e-mail ou mensagem, para induzir a vítima a fornecer senhas e dados pessoais é conhecida como",
       ["phishing.", "negação de serviço (DDoS).", "backdoor.", "rootkit.", "sniffing."],
       "Phishing é engenharia social: explora a confiança da vítima em vez de uma falha técnica. DDoS sobrecarrega um serviço; backdoor é uma porta de acesso oculta; rootkit esconde a presença do invasor; sniffing captura o tráfego da rede."),
    me(INFO, "Redes e internet", FGV, 2023,
       "O protocolo utilizado para o envio de mensagens de correio eletrônico, inclusive entre servidores de e-mail, é o",
       ["SMTP.", "POP3.", "IMAP.", "FTP.", "DNS."],
       "SMTP envia as mensagens. POP3 e IMAP servem para receber e ler: o POP3 costuma baixar e remover do servidor, e o IMAP mantém as mensagens sincronizadas no servidor. FTP transfere arquivos e DNS resolve nomes."),
    me(INFO, "Redes e internet", CESPE, 2024,
       "Na internet, a principal função do serviço DNS é",
       ["traduzir nomes de domínio em endereços IP.", "criptografar as páginas acessadas pelo navegador.",
        "atribuir endereços IP automaticamente aos computadores da rede local.",
        "bloquear acessos não autorizados à rede.", "armazenar em cache as páginas mais visitadas."],
       "O DNS converte nomes legíveis, como www.exemplo.gov.br, no endereço IP do servidor. A atribuição automática de IP é feita pelo DHCP; o bloqueio de acessos, pelo firewall; a criptografia, pelo TLS usado no HTTPS."),
    me(INFO, "Planilhas eletrônicas", FGV, 2025,
       "Em uma planilha, as células A1, A2 e A3 contêm, respectivamente, os valores 10, 20 e 30. O resultado da fórmula =MÉDIA(A1:A3)*2 é",
       ["40.", "20.", "60.", "120.", "30."],
       "A média de 10, 20 e 30 é 60 / 3 = 20. Multiplicada por 2, resulta em 40. A multiplicação é aplicada ao resultado da função, não a cada célula."),
    me(INFO, "Planilhas eletrônicas", CESPE, 2023,
       "Ao copiar uma fórmula para outras células de uma planilha, a referência que permanece inalterada tanto na linha quanto na coluna é",
       ["$A$1", "A$1", "$A1", "A1", "#A#1"],
       "O cifrão fixa o que vem logo depois dele. Em $A$1, coluna e linha estão fixas (referência absoluta). A$1 fixa só a linha e $A1 só a coluna (referências mistas); A1 é relativa. O símbolo # não cria referência."),
    me(INFO, "Hardware e sistemas operacionais", FGV, 2024,
       "A memória do computador cujo conteúdo é perdido quando o equipamento é desligado é a",
       ["memória RAM.", "memória ROM.", "unidade SSD.", "unidade de disco rígido (HD).", "memória flash de um pendrive."],
       "A RAM é volátil: guarda os programas e dados em uso e se apaga sem energia. ROM, SSD, HD e memória flash são não voláteis."),
    me(INFO, "Hardware e sistemas operacionais", CESPE, 2025,
       "No Windows, o atalho de teclado que desfaz a última ação na maioria dos aplicativos é",
       ["Ctrl + Z.", "Ctrl + Y.", "Ctrl + X.", "Ctrl + V.", "Ctrl + P."],
       "Ctrl + Z desfaz. Ctrl + Y refaz a ação desfeita; Ctrl + X recorta; Ctrl + V cola; Ctrl + P abre a impressão."),
    ce(INFO, "Segurança da informação", CESPE, 2024,
       "O backup incremental copia apenas os arquivos criados ou alterados desde o último backup realizado, seja ele completo ou incremental.",
       True,
       "Essa é a definição do incremental. O backup diferencial, por sua vez, copia tudo o que mudou desde o último backup completo, e por isso cresce a cada execução."),
    ce(INFO, "Segurança da informação", CESPE, 2023,
       "Um firewall bem configurado é suficiente, por si só, para impedir a infecção do computador por vírus recebidos em anexos de e-mail.",
       False,
       "O firewall filtra conexões de rede; ele não analisa o conteúdo dos anexos que o usuário decide abrir. A proteção contra vírus depende de antivírus atualizado e do comportamento do usuário."),
    ce(INFO, "Redes e internet", CESPE, 2025,
       "O protocolo HTTPS utiliza criptografia para proteger os dados trafegados entre o navegador e o servidor.",
       True,
       "HTTPS é o HTTP sobre TLS: os dados são cifrados no trajeto e o certificado digital permite verificar a identidade do site."),
    ce(INFO, "Redes e internet", CESPE, 2024,
       "A intranet é uma rede de acesso restrito, geralmente interna a uma organização, que utiliza os mesmos protocolos da internet, como o TCP/IP.",
       True,
       "A intranet usa a mesma tecnologia da internet (TCP/IP, HTTP, navegadores), mas com acesso limitado aos membros da organização."),

    # ------------------------------------------------------------------
    # Raciocínio Lógico
    # ------------------------------------------------------------------
    me(RLM, "Negação de proposições", FGV, 2023,
       "A negação da proposição \"Todos os servidores são pontuais\" é",
       ["Algum servidor não é pontual.", "Nenhum servidor é pontual.", "Todos os servidores não são pontuais.",
        "Algum servidor é pontual.", "Nenhum servidor é impontual."],
       "Para negar \"todos são\", basta mostrar que pelo menos um não é. \"Nenhum servidor é pontual\" é uma afirmação mais forte, que não corresponde à negação."),
    me(RLM, "Negação de proposições", CESPE, 2024,
       "A negação da proposição \"João estuda e Maria trabalha\" é",
       ["João não estuda ou Maria não trabalha.", "João não estuda e Maria não trabalha.",
        "João estuda ou Maria trabalha.", "Se João estuda, então Maria trabalha.", "João não estuda e Maria trabalha."],
       "Pela lei de De Morgan, ~(p ∧ q) ≡ ~p ∨ ~q: nega-se cada parte e troca-se \"e\" por \"ou\"."),
    me(RLM, "Equivalências lógicas", FGV, 2024,
       "Uma proposição logicamente equivalente a \"Se chove, então a rua fica molhada\" é",
       ["Se a rua não fica molhada, então não chove.", "Se a rua fica molhada, então chove.",
        "Se não chove, então a rua não fica molhada.", "Chove e a rua não fica molhada.",
        "Chove ou a rua fica molhada."],
       "A contrapositiva ~q → ~p é equivalente a p → q. A recíproca (q → p) e a inversa (~p → ~q) não são equivalentes. \"Chove e a rua não fica molhada\" é a negação da condicional."),
    me(RLM, "Negação de proposições", CESPE, 2025,
       "A negação da proposição \"Se estudo, então sou aprovado\" é",
       ["Estudo e não sou aprovado.", "Se não estudo, então não sou aprovado.", "Não estudo ou sou aprovado.",
        "Não estudo e não sou aprovado.", "Se sou aprovado, então estudo."],
       "A condicional p → q só é falsa quando p é verdadeira e q é falsa. Por isso sua negação é p ∧ ~q."),
    me(RLM, "Porcentagem", FGV, 2023,
       "Um produto teve o preço aumentado em 20% e, em seguida, reduzido em 20%. Em relação ao preço inicial, o preço final",
       ["diminuiu 4%.", "permaneceu o mesmo.", "aumentou 4%.", "diminuiu 2%.", "diminuiu 20%."],
       "Aplicam-se os fatores em sequência: 1,20 × 0,80 = 0,96. O preço final é 96% do inicial, uma redução de 4%. Os 20% da redução incidem sobre um valor já aumentado."),
    me(RLM, "Análise combinatória", CESPE, 2024,
       "A quantidade de anagramas da palavra PROVA é",
       ["120.", "24.", "60.", "720.", "25."],
       "PROVA tem 5 letras distintas, então o número de anagramas é 5! = 5 × 4 × 3 × 2 × 1 = 120."),
    me(RLM, "Análise combinatória", FGV, 2025,
       "O número de comissões de 3 pessoas que podem ser formadas a partir de um grupo de 6 pessoas é",
       ["20.", "18.", "60.", "120.", "216."],
       "Em comissão a ordem não importa: C(6,3) = 6! / (3! × 3!) = 20. O valor 120 seria o arranjo A(6,3), em que a ordem importa."),
    me(RLM, "Probabilidade", CESPE, 2023,
       "Ao lançar dois dados comuns, não viciados, a probabilidade de a soma dos resultados ser igual a 7 é",
       ["1/6.", "1/12.", "7/36.", "1/36.", "5/36."],
       "Há 36 resultados possíveis e 6 somam 7: (1,6), (2,5), (3,4), (4,3), (5,2) e (6,1). Logo, 6/36 = 1/6."),
    ce(RLM, "Proposições e conectivos", CESPE, 2024,
       "A proposição \"Se 2 + 2 = 5, então a Lua é feita de queijo\" é verdadeira.",
       True,
       "A condicional só é falsa quando o antecedente é verdadeiro e o consequente é falso. Como \"2 + 2 = 5\" é falso, a condicional é verdadeira, qualquer que seja o consequente."),
    ce(RLM, "Proposições e conectivos", CESPE, 2023,
       "A disjunção \"p ou q\" é falsa somente quando as proposições p e q são ambas falsas.",
       True,
       "Na disjunção inclusiva basta uma parte verdadeira para o todo ser verdadeiro, então ela só é falsa quando as duas partes são falsas."),
    ce(RLM, "Negação de proposições", CESPE, 2025,
       "A negação da proposição \"Algum candidato foi aprovado\" é \"Algum candidato não foi aprovado\".",
       False,
       "A negação de \"algum é\" é \"nenhum é\": \"Nenhum candidato foi aprovado\". As proposições \"algum foi\" e \"algum não foi\" podem ser verdadeiras ao mesmo tempo."),
    ce(RLM, "Porcentagem", CESPE, 2024,
       "Um produto que custava R$ 200,00 e passou a custar R$ 250,00 sofreu aumento de 25%.",
       True,
       "O aumento foi de R$ 50,00 sobre R$ 200,00: 50 / 200 = 0,25, ou seja, 25%. A base da porcentagem é sempre o valor inicial."),

    # ------------------------------------------------------------------
    # Certo/Errado em disciplinas que ja existiam
    # ------------------------------------------------------------------
    ce(PORT, "Crase", CESPE, 2024,
       "Na frase \"Refiro-me a esta proposta\", o acento grave seria obrigatório antes do pronome demonstrativo \"esta\".",
       False,
       "Não ocorre crase antes de \"esta\", \"essa\" e \"este\", porque esses pronomes não admitem artigo. Há crase apenas com \"aquele(s)\", \"aquela(s)\" e \"aquilo\" (\"refiro-me àquela proposta\")."),
    ce(PORT, "Concordância verbal", CESPE, 2023,
       "Na frase \"Havia muitos candidatos na sala\", o verbo \"haver\" está corretamente empregado no singular, por ser impessoal.",
       True,
       "No sentido de existir, \"haver\" é impessoal e fica na 3ª pessoa do singular. Com \"existir\", a concordância seria no plural: \"Existiam muitos candidatos\"."),
    ce(PORT, "Pontuação", CESPE, 2025,
       "Na frase \"Os servidores, que chegaram atrasados, foram advertidos\", a retirada das vírgulas alteraria o sentido do período.",
       True,
       "Com vírgulas, a oração é explicativa: todos os servidores chegaram atrasados e foram advertidos. Sem vírgulas, ela vira restritiva: só os que chegaram atrasados foram advertidos."),
    ce(DADM, "Princípios da Administração Pública", CESPE, 2024,
       "O princípio da publicidade admite exceções, como nos casos em que o sigilo seja imprescindível à segurança da sociedade e do Estado.",
       True,
       "A publicidade é a regra, mas a própria CF (art. 5º, XXXIII) e a Lei de Acesso à Informação preveem sigilo quando imprescindível à segurança da sociedade e do Estado, além da proteção à intimidade."),
    ce(DADM, "Organização administrativa", CESPE, 2023,
       "A autarquia é pessoa jurídica de direito privado, criada por lei, para executar atividades típicas da Administração Pública.",
       False,
       "A autarquia é pessoa jurídica de direito público, criada por lei específica (art. 37, XIX, da CF). Empresas públicas e sociedades de economia mista é que têm personalidade de direito privado."),
    ce(DADM, "Anulação e revogação", CESPE, 2025,
       "A anulação de ato administrativo ilegal produz, em regra, efeitos retroativos (ex tunc).",
       True,
       "A anulação atinge o ato desde a origem, por vício de legalidade, e pode ser feita pela Administração ou pelo Judiciário. A revogação, por conveniência e oportunidade, produz efeitos ex nunc e só cabe à Administração (Súmula 473 do STF)."),
]


LOTE_3 = [
    # ------------------------------------------------------------------
    # Lingua Portuguesa
    # ------------------------------------------------------------------
    me(PORT, "Colocação pronominal", FGV, 2024,
       "Assinale a frase em que a colocação do pronome oblíquo átono está de acordo com a norma-padrão.",
       ["Não me informaram o resultado.", "Não informaram-me o resultado.", "Me informaram o resultado ontem.",
        "Tinha informado-me o resultado.", "Informarei-lhe o resultado amanhã."],
       "Palavras negativas atraem o pronome (próclise): \"não me informaram\". A norma-padrão não inicia período com pronome oblíquo átono, não admite ênclise ao particípio e, no futuro do presente, pede mesóclise (\"informar-lhe-ei\") ou próclise."),
    me(PORT, "Regência verbal", CESPE, 2023,
       "Assinale a frase em que a regência verbal está de acordo com a norma-padrão.",
       ["Os servidores assistiram à palestra do diretor.", "Prefiro trabalhar do que estudar.",
        "Aspiro o cargo de analista.", "Obedeça o regulamento interno.", "Esqueci do prazo de entrega."],
       "No sentido de ver, \"assistir\" pede a preposição \"a\" (assistir à palestra). \"Preferir\" rege \"a\" (preferir algo a algo); \"aspirar\", no sentido de desejar, e \"obedecer\" pedem \"a\"; \"esquecer\" sem pronome pede objeto direto (\"esqueci o prazo\")."),
    me(PORT, "Concordância nominal", FGV, 2025,
       "Assinale a frase em que a concordância nominal está correta.",
       ["É proibida a entrada de pessoas estranhas.", "É proibido a entrada de pessoas estranhas.",
        "Seguem anexo os documentos solicitados.", "Ela mesmo resolveu o problema.",
        "Havia bastante pessoas na fila."],
       "Com o artigo (\"a entrada\"), o predicativo concorda: \"é proibida\". Sem artigo, ficaria \"é proibido entrada\". \"Anexo\" e \"mesmo\" são adjetivos e concordam (\"anexos\", \"mesma\"); \"bastante\" como adjetivo vai ao plural (\"bastantes pessoas\")."),
    me(PORT, "Ortografia", CESPE, 2024,
       "Assinale a frase em que a forma do \"porquê\" está empregada corretamente.",
       ["Não sei por que ele faltou à reunião.", "Por quê você faltou à reunião?",
        "Ele faltou por que estava doente.", "Ninguém entendeu o por quê da decisão.", "Você faltou porquê?"],
       "\"Por que\" separado e sem acento aparece em perguntas diretas e indiretas (\"não sei por que\"). \"Porque\" junto indica causa; \"por quê\" vai no fim da frase; \"porquê\" com acento é substantivo (\"o porquê\")."),
    me(PORT, "Semântica", FGV, 2023,
       "No período \"Estudou durante meses; contudo, não foi aprovado\", o conectivo \"contudo\" estabelece relação de",
       ["oposição.", "causa.", "conclusão.", "condição.", "finalidade."],
       "\"Contudo\" é conjunção adversativa, como \"mas\", \"porém\" e \"entretanto\": introduz uma ideia que contraria a expectativa criada pela anterior."),
    me(PORT, "Acentuação gráfica", CESPE, 2025,
       "Assinale a palavra grafada de acordo com o Acordo Ortográfico vigente.",
       ["ideia", "heróico", "vôo", "assembléia", "pára (verbo parar)"],
       "O Acordo eliminou o acento dos ditongos abertos \"ei\" e \"oi\" em paroxítonas (ideia, heroico, assembleia), o circunflexo de \"oo\" (voo) e o acento diferencial de \"para\" (verbo)."),

    # ------------------------------------------------------------------
    # Direito Administrativo
    # ------------------------------------------------------------------
    me(DADM, "Atos administrativos", CESPE, 2024,
       "O atributo do ato administrativo que permite à Administração executar suas próprias decisões, sem necessidade de autorização prévia do Poder Judiciário, é a",
       ["autoexecutoriedade.", "imperatividade.", "tipicidade.", "presunção de legitimidade.", "motivação."],
       "Autoexecutoriedade é executar diretamente a decisão. Imperatividade é impor obrigações independentemente da concordância do particular; presunção de legitimidade é a presunção de que o ato é válido até prova em contrário; tipicidade é a correspondência com figuras previstas em lei. Motivação não é atributo, e sim requisito de forma."),
    me(DADM, "Poderes administrativos", FGV, 2023,
       "O poder conferido à Administração para condicionar e restringir o uso e o gozo de bens, atividades e direitos individuais em benefício do interesse público é o poder",
       ["de polícia.", "hierárquico.", "disciplinar.", "regulamentar.", "vinculado."],
       "Poder de polícia é limitar a liberdade individual em favor do interesse coletivo (fiscalização sanitária, de trânsito, de obras). O hierárquico organiza a estrutura interna; o disciplinar pune servidores e quem tem vínculo especial; o regulamentar edita normas para a fiel execução das leis."),
    me(DADM, "Licitações", CESPE, 2025,
       "NÃO é modalidade de licitação prevista na Lei nº 14.133/2021:",
       ["tomada de preços.", "pregão.", "concorrência.", "leilão.", "diálogo competitivo."],
       "A Lei 14.133/2021 prevê pregão, concorrência, concurso, leilão e diálogo competitivo. Tomada de preços e convite eram modalidades da antiga Lei 8.666/1993 e não existem na lei nova."),
    me(DADM, "Responsabilidade civil do Estado", FGV, 2024,
       "Nos termos do art. 37, § 6º, da Constituição Federal, as pessoas jurídicas de direito público respondem pelos danos que seus agentes, nessa qualidade, causarem a terceiros, de forma",
       ["objetiva, assegurado o direito de regresso contra o agente nos casos de dolo ou culpa.",
        "subjetiva, dependendo sempre da prova de culpa do agente.",
        "objetiva, sem possibilidade de regresso contra o agente.",
        "subjetiva, com regresso automático contra o agente.",
        "solidária com o agente, que responde diretamente perante a vítima."],
       "A responsabilidade do Estado é objetiva (teoria do risco administrativo): a vítima prova o dano e o nexo, sem precisar provar culpa. O Estado pode depois cobrar do agente, em ação de regresso, se ele agiu com dolo ou culpa."),
    me(DADM, "Improbidade administrativa", CESPE, 2024,
       "Após as alterações promovidas pela Lei nº 14.230/2021, a configuração de ato de improbidade administrativa exige",
       ["dolo do agente.", "culpa grave do agente.", "apenas a ilegalidade do ato.",
        "culpa, em qualquer de suas modalidades.", "prejuízo ao erário, em todos os casos."],
       "A Lei 14.230/2021 eliminou a improbidade culposa: todos os tipos exigem dolo, isto é, vontade livre e consciente de alcançar o resultado ilícito. Nem todo ato de improbidade depende de dano ao erário (há os de enriquecimento ilícito e os que atentam contra princípios)."),
    ce(DADM, "Anulação e revogação", CESPE, 2023,
       "O Poder Judiciário, no exercício da função jurisdicional, pode revogar ato administrativo por razões de conveniência e oportunidade.",
       False,
       "A revogação é privativa da Administração que praticou o ato, por ser juízo de mérito. No controle jurisdicional, o Judiciário só anula atos ilegais."),
    ce(DADM, "Servidores públicos", CESPE, 2024,
       "O servidor público estável só perderá o cargo em virtude de sentença judicial transitada em julgado, mediante processo administrativo em que lhe seja assegurada ampla defesa ou mediante procedimento de avaliação periódica de desempenho, na forma de lei complementar.",
       True,
       "É a redação do art. 41, § 1º, da CF. Há ainda a hipótese de excesso de despesa com pessoal (art. 169, § 4º)."),
    ce(DADM, "Organização administrativa", CESPE, 2025,
       "As empresas públicas e as sociedades de economia mista integram a administração pública indireta.",
       True,
       "A administração indireta é formada por autarquias, fundações públicas, empresas públicas e sociedades de economia mista."),
    ce(DADM, "Processo administrativo", CESPE, 2023,
       "Na esfera federal, o direito da Administração de anular os atos administrativos de que decorram efeitos favoráveis para os destinatários decai em cinco anos, contados da data em que foram praticados, salvo comprovada má-fé.",
       True,
       "Art. 54 da Lei 9.784/1999. Com má-fé comprovada, não há o limite de cinco anos."),

    # ------------------------------------------------------------------
    # Direito Constitucional
    # ------------------------------------------------------------------
    me(DCON, "Remédios constitucionais", CESPE, 2024,
       "De acordo com a Constituição Federal, o mandado de segurança coletivo pode ser impetrado por",
       ["partido político com representação no Congresso Nacional.", "qualquer cidadão.",
        "qualquer pessoa jurídica de direito privado.",
        "associação legalmente constituída e em funcionamento há pelo menos seis meses.",
        "Ministério Público, exclusivamente."],
       "Art. 5º, LXX: partido político com representação no Congresso Nacional e organização sindical, entidade de classe ou associação legalmente constituída e em funcionamento há pelo menos um ano, em defesa de seus membros ou associados."),
    me(DCON, "Poder Legislativo", FGV, 2025,
       "A iniciativa popular de projeto de lei federal exige a apresentação à Câmara dos Deputados de projeto subscrito por, no mínimo,",
       ["um por cento do eleitorado nacional, distribuído por pelo menos cinco Estados, com não menos de três décimos por cento dos eleitores de cada um deles.",
        "cinco por cento do eleitorado nacional, distribuído por pelo menos nove Estados.",
        "um por cento do eleitorado de cada Estado da Federação.",
        "cem mil eleitores, independentemente da distribuição por Estados.",
        "dez por cento do eleitorado nacional, sem exigência de distribuição."],
       "Art. 61, § 2º, da CF: 1% do eleitorado nacional, distribuído por pelo menos cinco Estados, com não menos de 0,3% dos eleitores de cada um deles."),
    me(DCON, "Poder Legislativo", CESPE, 2023,
       "Os deputados federais são eleitos pelo sistema",
       ["proporcional.", "majoritário absoluto.", "majoritário simples.", "distrital puro.", "de lista fechada sem votação nominal."],
       "Art. 45 da CF: a Câmara compõe-se de representantes do povo eleitos pelo sistema proporcional em cada Estado, Território e no DF. Os senadores são eleitos pelo princípio majoritário."),
    ce(DCON, "Direitos fundamentais", CESPE, 2024,
       "É livre a manifestação do pensamento, sendo vedado o anonimato.",
       True,
       "Art. 5º, IV, da CF. A vedação ao anonimato permite responsabilizar quem abusa da liberdade de expressão."),
    ce(DCON, "Direitos fundamentais", CESPE, 2025,
       "A lei penal não retroagirá, salvo para beneficiar o réu.",
       True,
       "Art. 5º, XL, da CF: a lei penal mais benéfica retroage, inclusive para alcançar fatos já julgados."),
    ce(DCON, "Remédios constitucionais", CESPE, 2023,
       "O habeas corpus é o remédio adequado para proteger o direito líquido e certo à obtenção de certidões em repartições públicas.",
       False,
       "O habeas corpus protege a liberdade de locomoção. O direito de certidão (art. 5º, XXXIV, b) é protegido por mandado de segurança."),

    # ------------------------------------------------------------------
    # Nocoes de Informatica
    # ------------------------------------------------------------------
    me(INFO, "Planilhas eletrônicas", FGV, 2024,
       "Em uma planilha, a célula A1 contém o valor 6. O resultado da fórmula =SE(A1>=7;\"Aprovado\";\"Reprovado\") é",
       ["Reprovado", "Aprovado", "7", "6", "#VALOR!"],
       "A função SE testa a condição (6 >= 7 é falso) e devolve o terceiro argumento, \"Reprovado\". Com A1 igual ou maior que 7, devolveria \"Aprovado\"."),
    me(INFO, "Arquivos e pastas", CESPE, 2023,
       "A extensão de arquivo padrão das planilhas criadas nas versões atuais do Microsoft Excel é",
       [".xlsx", ".docx", ".pptx", ".pdf", ".txt"],
       ".xlsx é a planilha do Excel; .docx é documento do Word; .pptx, apresentação do PowerPoint; .pdf e .txt são formatos de documento e texto simples."),
    me(INFO, "Redes e internet", FGV, 2025,
       "No endereço de correio eletrônico fulano@orgao.gov.br, o trecho após o símbolo @ identifica",
       ["o domínio do provedor ou da organização responsável pela caixa postal.", "o nome do usuário.",
        "o protocolo de envio utilizado.", "o endereço IP do computador do usuário.", "a senha de acesso criptografada."],
       "Antes do @ fica o nome da caixa postal (usuário); depois, o domínio que hospeda o serviço de e-mail. Protocolo e endereço IP não aparecem no endereço."),
    ce(INFO, "Redes e internet", CESPE, 2024,
       "A computação em nuvem permite acessar arquivos armazenados remotamente a partir de diferentes dispositivos conectados à internet.",
       True,
       "Na nuvem os dados ficam em servidores do provedor e podem ser acessados de qualquer dispositivo com conexão e credenciais."),
    ce(INFO, "Segurança da informação", CESPE, 2025,
       "O worm é um programa malicioso que depende da execução de um arquivo hospedeiro para se propagar.",
       False,
       "Quem depende de arquivo hospedeiro é o vírus. O worm se propaga sozinho, explorando falhas da rede e enviando cópias de si mesmo."),
    ce(INFO, "Hardware e sistemas operacionais", CESPE, 2023,
       "No Windows, o atalho Ctrl + C copia o item selecionado para a área de transferência.",
       True,
       "Ctrl + C copia, Ctrl + X recorta e Ctrl + V cola o conteúdo da área de transferência."),

    # ------------------------------------------------------------------
    # Raciocinio Logico
    # ------------------------------------------------------------------
    me(RLM, "Porcentagem", FGV, 2024,
       "Um capital de R$ 1.000,00 foi aplicado a juros simples de 2% ao mês durante 5 meses. O valor dos juros obtidos é",
       ["R$ 100,00.", "R$ 104,08.", "R$ 50,00.", "R$ 200,00.", "R$ 110,00."],
       "Juros simples: J = C × i × t = 1.000 × 0,02 × 5 = R$ 100,00. O valor de R$ 104,08 seria o de juros compostos no mesmo período."),
    me(RLM, "Sequências", CESPE, 2025,
       "Na sequência 2, 6, 18, 54, ..., o próximo termo é",
       ["162.", "108.", "72.", "216.", "150."],
       "Cada termo é o anterior multiplicado por 3 (progressão geométrica de razão 3): 54 × 3 = 162."),
    me(RLM, "Proposições e conectivos", FGV, 2023,
       "A tabela-verdade de uma proposição composta formada por três proposições simples distintas tem",
       ["8 linhas.", "3 linhas.", "6 linhas.", "9 linhas.", "16 linhas."],
       "O número de linhas é 2 elevado ao número de proposições simples: 2³ = 8."),
    ce(RLM, "Equivalências lógicas", CESPE, 2024,
       "A proposição \"Se Pedro é médico, então Pedro é formado\" é equivalente a \"Pedro não é médico ou Pedro é formado\".",
       True,
       "A condicional p → q equivale a ~p ∨ q: ela só é falsa quando p é verdadeira e q é falsa, exatamente como a disjunção ~p ∨ q."),
    ce(RLM, "Análise combinatória", CESPE, 2023,
       "Em um grupo de 5 pessoas, há 10 maneiras distintas de escolher um presidente e um vice-presidente, que devem ser pessoas diferentes.",
       False,
       "A ordem importa (presidente ≠ vice), então é arranjo: A(5,2) = 5 × 4 = 20. O valor 10 seria a combinação C(5,2), que ignora os cargos."),
    ce(RLM, "Porcentagem", CESPE, 2025,
       "Se 30% de um valor correspondem a 60, então esse valor é 200.",
       True,
       "0,30 × V = 60, logo V = 60 / 0,30 = 200."),
]

LOTE_4 = [
    # ------------------------------------------------------------------
    # Conhecimentos Bancarios
    # ------------------------------------------------------------------
    me(BANC, "Sistema Financeiro Nacional", CESPE, 2024,
       "No Sistema Financeiro Nacional, o órgão normativo máximo, responsável por fixar as diretrizes das políticas monetária, creditícia e cambial, é o",
       ["Conselho Monetário Nacional.", "Banco Central do Brasil.", "Comissão de Valores Mobiliários.",
        "Superintendência de Seguros Privados.", "Tesouro Nacional."],
       "O CMN é o órgão normativo máximo do SFN (Lei 4.595/1964). O Banco Central executa e fiscaliza as normas do CMN; a CVM regula o mercado de valores mobiliários; a Susep, o de seguros."),
    me(BANC, "Sistema Financeiro Nacional", FGV, 2023,
       "Integra o Conselho Monetário Nacional:",
       ["o Presidente do Banco Central do Brasil.", "o Presidente da Comissão de Valores Mobiliários.",
        "o Presidente do Banco do Brasil.", "o Presidente da Federação Brasileira de Bancos.",
        "o Ministro-Chefe da Casa Civil."],
       "O CMN é composto pelo Ministro da Fazenda, que o preside, pelo Ministro do Planejamento e Orçamento e pelo Presidente do Banco Central do Brasil. Dirigentes de bancos, da CVM ou de associações do setor não integram o Conselho."),
    me(BANC, "Garantias do sistema financeiro", CESPE, 2025,
       "O Fundo Garantidor de Créditos (FGC) garante os depósitos e investimentos cobertos até o limite, por CPF ou CNPJ e por instituição (ou conglomerado), de",
       ["R$ 250 mil.", "R$ 100 mil.", "R$ 70 mil.", "R$ 500 mil.", "R$ 1 milhão."],
       "O limite é de R$ 250 mil por pessoa e por instituição ou conglomerado. Há ainda um teto global de R$ 1 milhão por pessoa, a cada período de quatro anos, somando todas as instituições."),
    me(BANC, "Política monetária", FGV, 2024,
       "O órgão do Banco Central responsável por definir a meta da taxa Selic é o",
       ["Comitê de Política Monetária (Copom).", "Conselho Monetário Nacional (CMN).",
        "Conselho de Controle de Atividades Financeiras (Coaf).", "Tesouro Nacional.", "Conselho de Recursos do SFN."],
       "O Copom, formado pela diretoria do Banco Central, fixa a meta da Selic em reuniões periódicas. O CMN define a meta de inflação que o Copom persegue."),
    me(BANC, "Mercado de capitais", CESPE, 2023,
       "A autarquia responsável por regular e fiscalizar o mercado de valores mobiliários, como ações e debêntures, é a",
       ["Comissão de Valores Mobiliários.", "Superintendência de Seguros Privados.",
        "Superintendência Nacional de Previdência Complementar.", "Caixa Econômica Federal.", "Receita Federal."],
       "A CVM (Lei 6.385/1976) disciplina e fiscaliza o mercado de valores mobiliários. Susep e Previc cuidam de seguros e de previdência complementar fechada."),
    me(BANC, "Prevenção à lavagem de dinheiro", FGV, 2025,
       "As três fases clássicas do processo de lavagem de dinheiro são",
       ["colocação, ocultação e integração.", "captação, aplicação e resgate.",
        "emissão, circulação e liquidação.", "ocultação, fracionamento e saque.", "depósito, transferência e consumo."],
       "Na colocação, o dinheiro ilícito entra no sistema; na ocultação (ou estratificação), movimentações dificultam rastrear a origem; na integração, os recursos voltam à economia com aparência lícita."),
    me(BANC, "Política monetária", CESPE, 2024,
       "O instrumento de política monetária pelo qual o Banco Central exige que os bancos mantenham parte dos depósitos recolhida junto a ele é o",
       ["recolhimento compulsório.", "redesconto.", "open market.", "câmbio flutuante.", "superávit primário."],
       "O compulsório retira parte dos depósitos da capacidade de empréstimo dos bancos. Redesconto é o empréstimo do BC aos bancos; open market é a compra e venda de títulos públicos."),
    ce(BANC, "Sistema Financeiro Nacional", CESPE, 2024,
       "O Banco Central do Brasil é autarquia de natureza especial, e sua autonomia está prevista em lei complementar.",
       True,
       "A Lei Complementar 179/2021 definiu o BC como autarquia de natureza especial, sem vinculação a ministério e com mandatos fixos para presidente e diretores."),
    ce(BANC, "Sistema Financeiro Nacional", CESPE, 2023,
       "O Conselho Monetário Nacional é presidido pelo presidente do Banco Central do Brasil.",
       False,
       "O CMN é presidido pelo Ministro da Fazenda. O presidente do Banco Central é um dos seus integrantes."),
    ce(BANC, "Garantias do sistema financeiro", CESPE, 2025,
       "O Fundo Garantidor de Créditos é entidade pública mantida com recursos do Tesouro Nacional.",
       False,
       "O FGC é entidade privada, sem fins lucrativos, mantida por contribuições das instituições financeiras associadas."),
    ce(BANC, "Política monetária", CESPE, 2024,
       "O aumento da alíquota do recolhimento compulsório tende a reduzir a quantidade de recursos que os bancos têm disponível para conceder empréstimos.",
       True,
       "Com mais recursos retidos no Banco Central, sobra menos para emprestar: é uma medida contracionista."),
    ce(BANC, "Garantias do sistema financeiro", CESPE, 2023,
       "Os depósitos em caderneta de poupança estão entre os créditos cobertos pela garantia do FGC, observados os limites.",
       True,
       "Poupança, depósitos à vista e a prazo (como CDB), letras de câmbio e LCI/LCA estão entre os créditos garantidos pelo FGC."),
    ce(BANC, "Produtos e serviços bancários", CESPE, 2025,
       "O Pix é um arranjo de pagamentos instituído pelo Banco Central do Brasil.",
       True,
       "O Pix foi criado e é gerido pelo Banco Central, que define suas regras e opera a infraestrutura de liquidação."),

    # ------------------------------------------------------------------
    # Matematica Financeira
    # ------------------------------------------------------------------
    me(MFIN, "Juros compostos", FGV, 2024,
       "Um capital de R$ 1.000,00 foi aplicado a juros compostos de 10% ao mês. Após 2 meses, o montante é",
       ["R$ 1.210,00.", "R$ 1.200,00.", "R$ 1.100,00.", "R$ 1.221,00.", "R$ 1.020,00."],
       "M = C × (1 + i)^n = 1.000 × 1,1² = 1.000 × 1,21 = R$ 1.210,00. Os R$ 1.200,00 seriam o montante em juros simples."),
    me(MFIN, "Juros simples", CESPE, 2023,
       "Aplicado a juros simples de 5% ao mês por 4 meses, um capital rendeu R$ 400,00 de juros. Esse capital era de",
       ["R$ 2.000,00.", "R$ 1.600,00.", "R$ 8.000,00.", "R$ 2.500,00.", "R$ 400,00."],
       "J = C × i × t → 400 = C × 0,05 × 4 → C = 400 / 0,20 = R$ 2.000,00."),
    me(MFIN, "Taxas equivalentes", FGV, 2025,
       "No regime de juros compostos, a taxa anual equivalente a 1% ao mês é de, aproximadamente,",
       ["12,68%.", "12%.", "12,5%.", "13,2%.", "11,36%."],
       "(1,01)^12 ≈ 1,1268, ou seja, cerca de 12,68% ao ano. Os 12% seriam a taxa proporcional, que só vale em juros simples."),
    me(MFIN, "Taxas equivalentes", CESPE, 2024,
       "Uma taxa nominal de 12% ao ano, com capitalização mensal, corresponde a uma taxa efetiva mensal de",
       ["1%.", "12%.", "0,5%.", "1,2%.", "0,95%."],
       "Na taxa nominal, a taxa do período de capitalização é a proporcional: 12% / 12 = 1% ao mês."),
    me(MFIN, "Descontos", FGV, 2023,
       "Um título de R$ 5.000,00 foi descontado 2 meses antes do vencimento, com desconto comercial simples à taxa de 3% ao mês. O valor do desconto é",
       ["R$ 300,00.", "R$ 150,00.", "R$ 600,00.", "R$ 283,02.", "R$ 4.700,00."],
       "No desconto comercial (por fora), D = N × i × t = 5.000 × 0,03 × 2 = R$ 300,00. O valor recebido é R$ 4.700,00."),
    me(MFIN, "Juros simples", CESPE, 2025,
       "O montante de R$ 2.000,00 aplicados a juros simples de 2% ao mês, durante 6 meses, é",
       ["R$ 2.240,00.", "R$ 2.252,32.", "R$ 2.120,00.", "R$ 2.400,00.", "R$ 2.024,00."],
       "J = 2.000 × 0,02 × 6 = R$ 240,00; M = 2.000 + 240 = R$ 2.240,00. O valor de R$ 2.252,32 seria em juros compostos."),
    ce(MFIN, "Juros simples", CESPE, 2024,
       "No regime de juros simples, os juros de cada período são calculados sempre sobre o capital inicial.",
       True,
       "Essa é a característica dos juros simples: não há juros sobre juros, e o crescimento do montante é linear."),
    ce(MFIN, "Juros compostos", CESPE, 2023,
       "Para prazos superiores a um período, a mesma taxa rende menos juros no regime composto do que no regime simples.",
       False,
       "É o contrário: acima de um período, os juros compostos rendem mais, porque incidem também sobre os juros já acumulados."),
    ce(MFIN, "Taxas equivalentes", CESPE, 2025,
       "Em juros simples, a taxa de 2% ao mês é proporcional à taxa de 24% ao ano.",
       True,
       "Em juros simples, taxas proporcionais são equivalentes: 2% × 12 = 24%."),
    ce(MFIN, "Juros compostos", CESPE, 2024,
       "Em juros compostos, uma taxa de 21% em dois meses corresponde a 10% ao mês.",
       True,
       "1,10 × 1,10 = 1,21, ou seja, 21% no bimestre."),
]


LOTE_5 = [
    # ------------------------------------------------------------------
    # Etica no Servico Publico (Decreto 1.171/1994 e Decreto 6.029/2007)
    # ------------------------------------------------------------------
    me(ETICA, "Código de Ética (Decreto 1.171/1994)", CESPE, 2024,
       "Segundo o Código de Ética Profissional do Servidor Público Civil do Poder Executivo Federal, a pena aplicável ao servidor pela Comissão de Ética é a de",
       ["censura.", "advertência.", "suspensão.", "demissão.", "multa."],
       "Decreto 1.171/1994, Capítulo II, XXII: a pena aplicável pela Comissão de Ética é a censura, fundamentada no parecer assinado por todos os integrantes, com ciência do faltoso. Advertência, suspensão e demissão são penas disciplinares da Lei 8.112/1990, aplicadas em outro processo."),
    me(ETICA, "Código de Ética (Decreto 1.171/1994)", FGV, 2023,
       "Para fins de apuração do comprometimento ético, o Código de Ética do servidor do Executivo federal considera servidor público",
       ["todo aquele que, por força de lei, contrato ou qualquer ato jurídico, preste serviços de natureza permanente, temporária ou excepcional, ainda que sem retribuição financeira.",
        "apenas o ocupante de cargo de provimento efetivo.",
        "apenas o servidor estável, após o estágio probatório.",
        "somente quem recebe remuneração dos cofres públicos.",
        "apenas o ocupante de cargo em comissão ou função de confiança."],
       "Decreto 1.171/1994, XXIV: o conceito é amplo e alcança quem presta serviço ao Estado, ainda que transitoriamente e sem retribuição financeira, desde que ligado a órgão ou entidade pública."),
    ce(ETICA, "Código de Ética (Decreto 1.171/1994)", CESPE, 2025,
       "Salvo nos casos de segurança nacional, investigações policiais ou interesse superior do Estado, a publicidade de qualquer ato administrativo constitui requisito de eficácia e moralidade, e sua omissão configura comprometimento ético contra o bem comum.",
       True,
       "É a regra deontológica VII do Decreto 1.171/1994. As exceções dependem de processo previamente declarado sigiloso, nos termos da lei."),
    ce(ETICA, "Código de Ética (Decreto 1.171/1994)", CESPE, 2024,
       "Deixar o cidadão à espera de solução que compete ao seu setor, permitindo a formação de longas filas, é atitude contra a ética e causa grave dano moral aos usuários dos serviços públicos.",
       True,
       "Regra deontológica X: além de atitude antiética e ato de desumanidade, o atraso injustificado é principalmente grave dano moral aos usuários."),
    ce(ETICA, "Código de Ética (Decreto 1.171/1994)", CESPE, 2023,
       "O servidor pode omitir a verdade quando ela for contrária aos interesses da própria Administração Pública.",
       False,
       "Regra deontológica VIII: o servidor não pode omitir nem falsear a verdade, ainda que contrária aos interesses da própria pessoa interessada ou da Administração."),
    ce(ETICA, "Código de Ética (Decreto 1.171/1994)", CESPE, 2025,
       "É vedado ao servidor público usar do cargo ou função, facilidades, amizades, tempo, posição e influências para obter qualquer favorecimento, para si ou para outrem.",
       True,
       "Está entre as vedações do Capítulo I, Seção III, XV, alínea a, do Decreto 1.171/1994."),
    ce(ETICA, "Comissões de Ética", CESPE, 2024,
       "A pena de censura aplicada pela Comissão de Ética deve constar de parecer fundamentado, assinado por todos os seus integrantes, com ciência do faltoso.",
       True,
       "Decreto 1.171/1994, XXII. A censura é a única penalidade do Código de Ética."),
    ce(ETICA, "Comissões de Ética", CESPE, 2023,
       "A Comissão de Ética pode aplicar ao servidor a pena de demissão quando a falta ética for grave.",
       False,
       "A Comissão de Ética só aplica censura. A demissão é penalidade disciplinar da Lei 8.112/1990, aplicada em processo administrativo disciplinar."),

    # ------------------------------------------------------------------
    # Lingua Portuguesa
    # ------------------------------------------------------------------
    ce(PORT, "Crase", CESPE, 2024,
       "Na frase \"Fui à Curitiba para a prova\", o uso do acento grave está de acordo com a norma-padrão.",
       False,
       "Curitiba não admite artigo (\"venho de Curitiba\", e não \"da Curitiba\"), então não há crase: \"Fui a Curitiba\". Compare com \"Fui à Bahia\" (\"venho da Bahia\")."),
    ce(PORT, "Concordância verbal", CESPE, 2025,
       "A frase \"Fazem dez anos que ele estuda para concursos\" está correta, pois o verbo concorda com \"dez anos\".",
       False,
       "O verbo \"fazer\" indicando tempo decorrido é impessoal e fica no singular: \"Faz dez anos\"."),
    ce(PORT, "Regência verbal", CESPE, 2023,
       "No sentido de acarretar, o verbo \"implicar\" é transitivo direto, como em \"A mudança implica novos custos\".",
       True,
       "Nesse sentido, a norma-padrão pede objeto direto; a construção \"implica em\" é considerada desvio."),
    ce(PORT, "Ortografia", CESPE, 2024,
       "\"A fim de\" indica finalidade, enquanto \"afim\" significa semelhante ou que tem afinidade.",
       True,
       "Ex.: \"Estudou a fim de passar\" (finalidade); \"disciplinas afins\" (semelhantes)."),
    ce(PORT, "Semântica", CESPE, 2025,
       "Em \"Embora estivesse cansado, continuou estudando\", a conjunção \"embora\" introduz ideia de concessão.",
       True,
       "Concessão é um fato que poderia impedir o outro, mas não impede. Outras conjunções concessivas: ainda que, mesmo que, conquanto."),
    me(PORT, "Pontuação", FGV, 2024,
       "Assinale a frase corretamente pontuada.",
       ["Os candidatos aprovados serão convocados em março.", "Os candidatos aprovados, serão convocados em março.",
        "Os candidatos, aprovados serão convocados em março.", "Os candidatos aprovados serão, convocados em março.",
        "Os candidatos aprovados serão convocados, em março."],
       "Não se separa por vírgula o sujeito (\"os candidatos aprovados\") do verbo, nem o verbo de seus complementos. A última opção separa, sem motivo, uma circunstância de tempo breve e no fim da oração."),
    me(PORT, "Ortografia", CESPE, 2023,
       "Assinale a frase em que o termo destacado está empregado corretamente.",
       ["Ele se comportou MAL durante a entrevista.", "Ele é um MAL candidato.", "Ele falou MAU da banca examinadora.",
        "Há MAL entendidos no edital.", "Ele tem um MAU hábito de chegar MAU humorado."],
       "\"Mal\" é advérbio (oposto de bem): comportou-se mal, falou mal. \"Mau\" é adjetivo (oposto de bom): mau candidato, mau hábito. \"Mal-entendidos\" tem hífen e \"mal-humorado\" também."),
    me(PORT, "Ortografia", FGV, 2025,
       "Assinale a frase em que \"há\" e \"a\" estão empregados corretamente.",
       ["Estudo para concursos há dois anos e farei a prova daqui a um mês.",
        "Estudo para concursos a dois anos e farei a prova daqui há um mês.",
        "Estudo para concursos há dois anos e farei a prova daqui há um mês.",
        "Estudo para concursos a dois anos e farei a prova daqui a um mês.",
        "Estudo para concursos à dois anos e farei a prova daqui à um mês."],
       "\"Há\" (verbo haver) indica tempo passado: há dois anos. \"A\" (preposição) indica tempo futuro ou distância: daqui a um mês."),
]

LOTE_6 = [
    # ------------------------------------------------------------------
    # Economia (micro e macro, como no Bacen)
    # ------------------------------------------------------------------
    ce(ECON, "Elasticidade", CESPE, 2025,
       "Se a demanda por determinado bem é elástica em relação ao preço, um aumento do preço reduz a receita total do vendedor.",
       True,
       "Com demanda elástica (|E| > 1), a quantidade cai proporcionalmente mais do que o preço sobe, e a receita total (preço × quantidade) diminui. Com demanda inelástica ocorre o contrário."),
    ce(ECON, "Elasticidade", CESPE, 2026,
       "Bens que possuem muitos substitutos próximos tendem a apresentar demanda inelástica em relação ao preço.",
       False,
       "É o contrário: quanto mais substitutos próximos, mais fácil trocar de bem quando o preço sobe, e mais elástica é a demanda."),
    me(ECON, "Estruturas de mercado", CESPE, 2025,
       "Em concorrência perfeita, no equilíbrio de longo prazo, a firma representativa",
       ["obtém lucro econômico nulo, produzindo onde o preço é igual ao custo marginal e ao custo médio mínimo.",
        "obtém lucro econômico positivo, garantido pelas barreiras à entrada.",
        "cobra preço acima do custo marginal, por ter poder de mercado.",
        "diferencia seu produto para fidelizar consumidores.",
        "fixa o preço de mercado, por ser a maior ofertante."],
       "Sem barreiras à entrada, lucros extraordinários atraem novas firmas até que desapareçam. A firma é tomadora de preço e produz onde P = CMg = CMe mínimo."),
    ce(ECON, "Estruturas de mercado", CESPE, 2024,
       "O monopolista maximiza o lucro produzindo a quantidade em que a receita marginal se iguala ao custo marginal e cobra, por essa quantidade, preço superior ao custo marginal.",
       True,
       "A condição RMg = CMg vale para qualquer firma. No monopólio, a demanda é negativamente inclinada, a receita marginal fica abaixo do preço e, por isso, P > CMg no ponto ótimo."),
    ce(ECON, "Falhas de mercado", CESPE, 2025,
       "Bens públicos puros caracterizam-se pela rivalidade no consumo e pela possibilidade de exclusão de quem não paga.",
       False,
       "Bens públicos puros são não rivais (o consumo de um não reduz o do outro) e não excludentes (não é possível impedir o uso de quem não paga), o que gera o problema do carona."),
    me(ECON, "Contas nacionais", CESPE, 2024,
       "Pela ótica da despesa, o produto interno bruto corresponde à soma de",
       ["consumo das famílias, investimento, gastos do governo e exportações, menos as importações.",
        "salários, lucros, juros e aluguéis pagos na economia.",
        "valor bruto da produção de todos os setores, sem descontar o consumo intermediário.",
        "consumo das famílias, investimento e importações, menos as exportações.",
        "renda nacional bruta e renda enviada ao exterior."],
       "PIB = C + I + G + (X − M). A soma de salários, lucros, juros e aluguéis é a ótica da renda; o valor da produção menos o consumo intermediário é a ótica do produto."),
    me(ECON, "Modelo IS-LM", CESPE, 2026,
       "No modelo IS-LM, uma política fiscal expansionista, com a política monetária inalterada, tende a",
       ["deslocar a curva IS para a direita, elevando a renda e a taxa de juros.",
        "deslocar a curva IS para a direita, elevando a renda e reduzindo a taxa de juros.",
        "deslocar a curva LM para a direita, reduzindo a taxa de juros.",
        "deslocar a curva IS para a esquerda, reduzindo a renda.",
        "não alterar a renda nem a taxa de juros."],
       "O aumento de gastos ou a redução de tributos desloca a IS para a direita. Com a LM parada, o novo equilíbrio tem renda e juros maiores; a alta dos juros reduz parte do investimento privado (efeito deslocamento)."),
    ce(ECON, "Política monetária", CESPE, 2025,
       "No regime de metas para a inflação adotado no Brasil, o índice de preços de referência é o IGP-M.",
       False,
       "O índice de referência é o IPCA, calculado pelo IBGE. O IGP-M, da FGV, é usado em contratos como os de aluguel, mas não no regime de metas."),
    ce(ECON, "Política monetária", CESPE, 2026,
       "No sistema de meta contínua para a inflação, a meta é considerada descumprida quando a inflação acumulada em doze meses permanece fora do intervalo de tolerância por seis meses consecutivos.",
       True,
       "Regra do Decreto 12.079/2024, em vigor desde 2025: a aferição deixou de ser só no ano-calendário e passou a ser mensal, sobre o IPCA acumulado em doze meses."),
    ce(ECON, "Balanço de pagamentos", CESPE, 2024,
       "As remessas de lucros e dividendos de empresas estrangeiras instaladas no país para suas matrizes são registradas na conta de renda primária do balanço de pagamentos.",
       True,
       "Lucros, dividendos e juros são rendimentos de fatores de produção e ficam na renda primária, dentro das transações correntes. A entrada do investimento em si fica na conta financeira."),

    # ------------------------------------------------------------------
    # Conhecimentos Bancarios
    # ------------------------------------------------------------------
    ce(BANC, "Política monetária", CESPE, 2025,
       "A venda de títulos públicos pelo Banco Central no mercado aberto reduz a liquidez da economia.",
       True,
       "Ao vender títulos, o Banco Central recebe reservas dos bancos e retira moeda de circulação. A compra de títulos tem o efeito oposto e injeta liquidez."),
    ce(BANC, "Política monetária", CESPE, 2024,
       "O Comitê de Política Monetária (Copom) reúne-se ordinariamente oito vezes por ano para definir a meta da taxa Selic.",
       True,
       "Desde 2017 o calendário do Copom prevê oito reuniões ordinárias por ano, de dois dias cada. Podem ser convocadas reuniões extraordinárias."),
    ce(BANC, "Sistema de Pagamentos Brasileiro", CESPE, 2025,
       "O Pix é um arranjo de pagamentos instituído pelo Banco Central do Brasil que liquida as transações em tempo real, inclusive em fins de semana e feriados.",
       True,
       "O Pix funciona 24 horas por dia, todos os dias, com liquidação em tempo real. O Banco Central é o instituidor do arranjo e opera a infraestrutura de liquidação."),
    me(BANC, "Sistema de Pagamentos Brasileiro", CESPE, 2026,
       "O sistema operado pelo Banco Central que realiza a liquidação bruta em tempo real das transferências de fundos entre as instituições financeiras é o",
       ["Sistema de Transferência de Reservas (STR).", "Sistema Financeiro da Habitação (SFH).",
        "Fundo Garantidor de Créditos (FGC).", "Conselho de Controle de Atividades Financeiras (Coaf).",
        "Cadastro de Clientes do Sistema Financeiro (CCS)."],
       "O STR é o núcleo do SPB: liquida, uma a uma e em tempo real, as transferências entre contas de reservas dos bancos no Banco Central. As demais opções não são sistemas de liquidação."),
    ce(BANC, "Sistema Financeiro Nacional", CESPE, 2025,
       "Pela Lei Complementar 179/2021, o presidente e os diretores do Banco Central têm mandatos fixos de quatro anos, não coincidentes com o mandato do Presidente da República.",
       True,
       "A lei da autonomia do Banco Central fixou mandatos de quatro anos, escalonados; o do presidente começa no terceiro ano do mandato do Presidente da República."),
    ce(BANC, "Garantias do sistema financeiro", CESPE, 2024,
       "Depósitos em caderneta de poupança e aplicações em CDB são cobertos pelo Fundo Garantidor de Créditos, ao passo que as cotas de fundos de investimento não contam com essa garantia.",
       True,
       "O FGC cobre depósitos e títulos de emissão das instituições associadas (poupança, CDB, LCI, LCA, entre outros). Fundos de investimento não são obrigação do banco e ficam fora da cobertura."),
    ce(BANC, "Prevenção à lavagem de dinheiro", CESPE, 2026,
       "O Conselho de Controle de Atividades Financeiras (Coaf), unidade de inteligência financeira do Brasil, está vinculado administrativamente ao Banco Central do Brasil.",
       True,
       "A Lei 13.974/2020 vinculou o Coaf administrativamente ao Banco Central, com autonomia técnica e operacional."),

    # ------------------------------------------------------------------
    # Direito Administrativo
    # ------------------------------------------------------------------
    ce(DADM, "Anulação e revogação", CESPE, 2025,
       "A revogação de ato administrativo produz efeitos retroativos (ex tunc) e pode ser feita pelo Poder Judiciário no exercício da função jurisdicional.",
       False,
       "A revogação retira ato válido por conveniência e oportunidade, com efeitos ex nunc, e é privativa da Administração que o editou. O Judiciário, ao julgar, só anula atos ilegais."),
    ce(DADM, "Poderes administrativos", CESPE, 2026,
       "Por ser atividade típica de Estado, o poder de polícia não pode ter nenhuma de suas fases delegada a pessoa jurídica de direito privado.",
       False,
       "O STF (Tema 532) admitiu a delegação a estatais de direito privado, de capital majoritariamente público, que prestem serviço público em regime não concorrencial. Só a fase de legislação (ordem de polícia) é indelegável."),
    me(DADM, "Licitações", CESPE, 2025,
       "São modalidades de licitação previstas na Lei 14.133/2021:",
       ["pregão, concorrência, concurso, leilão e diálogo competitivo.",
        "convite, tomada de preços, concorrência, concurso e leilão.",
        "pregão, convite, concorrência, leilão e credenciamento.",
        "concorrência, tomada de preços, pregão e dispensa.",
        "concurso, leilão, inexigibilidade e diálogo competitivo."],
       "Art. 28 da Lei 14.133/2021. Convite e tomada de preços deixaram de existir; credenciamento é procedimento auxiliar; dispensa e inexigibilidade são contratação direta, não modalidades."),
    ce(DADM, "Responsabilidade civil do Estado", CESPE, 2024,
       "As pessoas jurídicas de direito público respondem objetivamente pelos danos que seus agentes, nessa qualidade, causarem a terceiros, assegurado o direito de regresso contra o responsável nos casos de dolo ou culpa.",
       True,
       "Art. 37, § 6º, da CF. A vítima não precisa provar culpa do Estado; a culpa ou o dolo do agente só importam na ação de regresso."),
    ce(DADM, "Servidores públicos", CESPE, 2025,
       "São estáveis após três anos de efetivo exercício os servidores nomeados para cargo de provimento efetivo em virtude de concurso público.",
       True,
       "Art. 41 da CF, com a redação da EC 19/1998. A aquisição da estabilidade depende também de avaliação especial de desempenho por comissão."),

    # ------------------------------------------------------------------
    # Estatistica
    # ------------------------------------------------------------------
    me(EST, "Medidas de tendência central", CESPE, 2025,
       "Para o conjunto de dados {2, 4, 4, 5, 10}, a média, a mediana e a moda são, respectivamente,",
       ["5, 4 e 4.", "4, 4 e 5.", "5, 5 e 4.", "4, 5 e 4.", "5, 4 e 10."],
       "Média = (2 + 4 + 4 + 5 + 10) / 5 = 25 / 5 = 5. Mediana: valor central dos dados ordenados, 4. Moda: valor mais frequente, 4."),
    ce(EST, "Medidas de dispersão", CESPE, 2026,
       "Somar uma mesma constante a todos os valores de um conjunto de dados altera a média, mas não altera a variância.",
       True,
       "A média é deslocada pela constante, mas os desvios em relação à nova média continuam os mesmos, e a variância (média dos quadrados dos desvios) não muda."),
    ce(EST, "Probabilidade", CESPE, 2024,
       "No lançamento de duas moedas honestas, a probabilidade de se obter pelo menos uma cara é igual a 3/4.",
       True,
       "O complemento é sair coroa nas duas: 1/2 × 1/2 = 1/4. Logo, P(pelo menos uma cara) = 1 − 1/4 = 3/4."),
]

LOTE_7 = [
    # ------------------------------------------------------------------
    # Lingua Portuguesa (estilo Cebraspe)
    # ------------------------------------------------------------------
    ce(PORT, "Vozes verbais", CESPE, 2025,
       "Na frase \"O edital foi publicado pelo órgão ontem\", a forma verbal está na voz passiva analítica, e sua transposição para a voz ativa resulta em \"O órgão publicou o edital ontem\".",
       True,
       "Voz passiva analítica: verbo ser + particípio, com agente da passiva (\"pelo órgão\"). Na ativa, o agente vira sujeito e o sujeito paciente vira objeto direto, mantendo o tempo verbal (pretérito perfeito)."),
    ce(PORT, "Vozes verbais", CESPE, 2026,
       "Em \"Vendem-se apostilas usadas\", o verbo deveria estar no singular, pois o sujeito da oração é indeterminado.",
       False,
       "É voz passiva sintética: \"se\" é pronome apassivador e \"apostilas usadas\" é o sujeito, com o qual o verbo concorda. Equivale a \"Apostilas usadas são vendidas\"."),
    ce(PORT, "Orações subordinadas", CESPE, 2025,
       "Em \"Embora estivesse cansado, o candidato terminou a prova\", a oração introduzida por \"embora\" expressa ideia de concessão.",
       True,
       "\"Embora\" introduz oração subordinada adverbial concessiva: o fato (o cansaço) poderia impedir a ação principal, mas não impede."),
    ce(PORT, "Orações subordinadas", CESPE, 2024,
       "Na frase \"Os candidatos que estudaram foram aprovados\", se a oração \"que estudaram\" fosse isolada por vírgulas, o período passaria a indicar que todos os candidatos estudaram.",
       True,
       "Sem vírgulas, a oração adjetiva é restritiva: só os candidatos que estudaram foram aprovados. Entre vírgulas, vira explicativa e atribui a característica a todos os candidatos."),
    me(PORT, "Coesão referencial", CESPE, 2025,
       "No período \"O servidor apresentou o relatório ao diretor, que o aprovou sem ressalvas\", o pronome \"o\" em \"o aprovou\" retoma",
       ["o relatório.", "o servidor.", "o diretor.", "a apresentação.", "as ressalvas."],
       "\"Que\" retoma \"o diretor\" (sujeito de \"aprovou\"), e o pronome oblíquo \"o\" é o objeto direto, retomando \"o relatório\", aquilo que foi aprovado."),
    ce(PORT, "Classes de palavras", CESPE, 2026,
       "Em \"Ela chegou meio cansada\", a palavra \"meio\" é advérbio e, por isso, permanece invariável; seria incorreto escrever \"Ela chegou meia cansada\".",
       True,
       "Como advérbio (equivale a \"um pouco\"), \"meio\" não varia. Varia apenas como numeral ou adjetivo: \"meia hora\", \"meia porção\"."),
    ce(PORT, "Classes de palavras", CESPE, 2025,
       "Em \"Este é o motivo por que desisti\", a grafia separada de \"por que\" está correta, pois a expressão equivale a \"pelo qual\".",
       True,
       "\"Por que\" separado é preposição + pronome relativo (\"pelo qual\") ou aparece em perguntas. \"Porque\" junto é conjunção explicativa ou causal."),
    me(PORT, "Tipologia textual", CESPE, 2024,
       "O texto predominantemente dissertativo-argumentativo caracteriza-se por",
       ["defender um ponto de vista com argumentos, para convencer o leitor.",
        "relatar fatos em sequência temporal, com personagens e enredo.",
        "caracterizar seres, objetos e ambientes por meio de detalhes sensoriais.",
        "orientar o leitor, passo a passo, a executar um procedimento.",
        "reproduzir diálogos entre personagens, sem a voz do autor."],
       "A argumentação tem tese e argumentos. Relato com personagens é narração; caracterização é descrição; instruções passo a passo formam o texto injuntivo."),
    ce(PORT, "Reescrita de frases", CESPE, 2025,
       "A reescrita de \"Caso o candidato se atrase, não poderá entrar na sala\" como \"Se o candidato se atrasar, não poderá entrar na sala\" preserva o sentido e a correção gramatical do período.",
       True,
       "\"Caso\" e \"se\" são conjunções condicionais. A troca exige ajustar o verbo: \"caso\" pede presente do subjuntivo (atrase); \"se\", futuro do subjuntivo (atrasar)."),
    ce(PORT, "Semântica", CESPE, 2026,
       "No período \"O resultado foi ratificado pela banca\", a substituição de \"ratificado\" por \"retificado\" manteria o sentido original.",
       False,
       "São parônimos: ratificar é confirmar; retificar é corrigir. A troca altera o sentido: o resultado deixaria de ser confirmado e passaria a ser corrigido."),

    # ------------------------------------------------------------------
    # Raciocinio Logico
    # ------------------------------------------------------------------
    ce(RLM, "Lógica de argumentação", CESPE, 2025,
       "O argumento \"Todo servidor é concursado. Paulo é concursado. Logo, Paulo é servidor\" é válido.",
       False,
       "É a falácia da afirmação do consequente: ser concursado é condição necessária para ser servidor, não suficiente. Paulo pode ser concursado sem ser servidor."),
    ce(RLM, "Proposições e conectivos", CESPE, 2024,
       "A proposição condicional \"p → q\" é falsa somente quando p é verdadeira e q é falsa.",
       True,
       "Na tabela-verdade da condicional, há um único caso falso: antecedente verdadeiro e consequente falso (V → F). Nos outros três, a condicional é verdadeira."),
    me(RLM, "Conjuntos", CESPE, 2025,
       "Em uma turma de 40 alunos, 25 estudam Português, 20 estudam Matemática e 8 estudam as duas disciplinas. O número de alunos que não estudam nenhuma das duas é",
       ["3.", "0.", "5.", "8.", "11."],
       "União = 25 + 20 − 8 = 37 alunos estudam ao menos uma disciplina. Logo, 40 − 37 = 3 não estudam nenhuma."),
    ce(RLM, "Probabilidade", CESPE, 2026,
       "De uma urna com 3 bolas brancas e 2 pretas, retiram-se duas bolas, sem reposição. A probabilidade de ambas serem brancas é igual a 3/10.",
       True,
       "P = 3/5 × 2/4 = 6/20 = 3/10. Sem reposição, a segunda retirada tem uma bola branca e uma bola a menos na urna."),
    me(RLM, "Análise combinatória", CESPE, 2024,
       "O número de maneiras distintas de 4 pessoas se sentarem em torno de uma mesa circular é",
       ["6.", "24.", "12.", "4.", "16."],
       "Permutação circular: (n − 1)! = 3! = 6. Rotações da mesma disposição não contam como arranjos diferentes."),
    ce(RLM, "Equivalências lógicas", CESPE, 2025,
       "A proposição \"Se não estudo, então não passo\" é logicamente equivalente a \"Se passo, então estudo\".",
       True,
       "É a contrapositiva: ~E → ~P equivale a P → E (inverte-se a ordem e negam-se as duas proposições)."),

    # ------------------------------------------------------------------
    # Conhecimentos Bancarios
    # ------------------------------------------------------------------
    ce(BANC, "Mercado de câmbio", CESPE, 2025,
       "No regime de câmbio flutuante adotado pelo Brasil, o Banco Central pode intervir no mercado, por meio de leilões de moeda estrangeira ou de swaps cambiais, para reduzir a volatilidade da taxa de câmbio.",
       True,
       "O câmbio é flutuante, mas não livre de intervenção: o Banco Central atua para suavizar oscilações excessivas, sem fixar um patamar para a taxa."),
    ce(BANC, "Produtos e serviços bancários", CESPE, 2026,
       "O certificado de depósito bancário (CDB) é título de renda fixa emitido por bancos para captar recursos e pode ter remuneração prefixada ou pós-fixada.",
       True,
       "No CDB o investidor empresta ao banco. A remuneração pode ser prefixada, pós-fixada (atrelada ao CDI, por exemplo) ou híbrida, com parte prefixada e parte indexada."),
    me(BANC, "Sistema Financeiro Nacional", CESPE, 2024,
       "A autarquia responsável por fiscalizar e supervisionar as entidades fechadas de previdência complementar (fundos de pensão) é a",
       ["Superintendência Nacional de Previdência Complementar (Previc).",
        "Superintendência de Seguros Privados (Susep).",
        "Comissão de Valores Mobiliários (CVM).",
        "Agência Nacional de Saúde Suplementar (ANS).",
        "Secretaria do Tesouro Nacional."],
       "A Previc supervisiona os fundos de pensão. A Susep cuida de seguros, capitalização e previdência aberta; a CVM, do mercado de valores mobiliários."),
    ce(BANC, "Prevenção à lavagem de dinheiro", CESPE, 2025,
       "As instituições financeiras devem comunicar ao Coaf as operações com indícios de lavagem de dinheiro, sem dar ciência dessa comunicação ao cliente envolvido.",
       True,
       "Lei 9.613/1998, art. 11: a comunicação é obrigatória e sigilosa; avisar o cliente frustraria a apuração."),
    ce(BANC, "Política monetária", CESPE, 2026,
       "O redesconto é a operação pela qual o Banco Central concede assistência financeira de liquidez às instituições financeiras.",
       True,
       "O Banco Central atua como emprestador de última instância. Encarecer o redesconto desestimula esses empréstimos e reduz a liquidez; baratear tem o efeito oposto."),

    # ------------------------------------------------------------------
    # Economia
    # ------------------------------------------------------------------
    ce(ECON, "Inflação", CESPE, 2025,
       "A inflação de custos ocorre quando o aumento generalizado dos preços decorre do excesso de demanda agregada em relação à capacidade produtiva da economia.",
       False,
       "Esse é o conceito de inflação de demanda. A inflação de custos vem do lado da oferta: alta de salários, matérias-primas, energia ou câmbio que as empresas repassam aos preços."),
    ce(ECON, "Política fiscal", CESPE, 2024,
       "Há superávit primário quando as receitas do governo superam as despesas, excluídos os juros da dívida pública.",
       True,
       "O resultado primário desconsidera os juros. Incluídos os juros, tem-se o resultado nominal, que no Brasil costuma ser deficitário mesmo com superávit primário."),
    me(ECON, "Desemprego", CESPE, 2026,
       "O trabalhador que perde o emprego porque sua função foi extinta pela adoção de uma nova tecnologia no setor em que atuava está em situação de desemprego",
       ["estrutural.", "friccional.", "cíclico.", "sazonal.", "voluntário."],
       "O desemprego estrutural decorre de mudanças na estrutura produtiva que tornam certas qualificações obsoletas. O friccional é a transição entre empregos; o cíclico acompanha as recessões; o sazonal, épocas do ano."),
    ce(ECON, "Moeda", CESPE, 2025,
       "A moeda desempenha as funções de meio de troca, unidade de conta e reserva de valor.",
       True,
       "São as três funções clássicas: facilita as trocas, serve de medida comum de valor e permite transferir poder de compra no tempo (função prejudicada por inflação alta)."),
]


LOTES = {"2": LOTE_2, "3": LOTE_3, "4": LOTE_4, "5": LOTE_5, "6": LOTE_6, "7": LOTE_7}


def sql(texto):
    return "'" + texto.replace("'", "''") + "'"


def rodape_novo(questoes):
    """A partir do lote 6, com o catalogo de concursos (V31+) no banco:
    o cargo de exemplo recebe so as disciplinas usadas no lote (e nao todas as
    do banco), e o total de topicos so e recalculado onde ja era contado
    (o catalogo deixa NULL de proposito ate ler o conteudo do edital)."""
    discs = ", ".join(sql(d) for d in sorted({q["disc"] for q in questoes}))
    return [
        "-- O cargo do concurso de exemplo passa a cobrar as disciplinas deste lote.",
        f"""INSERT INTO cargo_disciplina (cargo_id, disciplina_id, total_topicos, ordem)
SELECT cc.id, d.id, 0,
       (SELECT COALESCE(MAX(x.ordem), 0) FROM cargo_disciplina x WHERE x.cargo_id = cc.id)
         + ROW_NUMBER() OVER (PARTITION BY cc.id ORDER BY d.nome)
FROM concurso_cargo cc
CROSS JOIN disciplina d
WHERE cc.nome = {sql(CARGO_EXEMPLO)}
  AND d.nome IN ({discs})
  AND NOT EXISTS (SELECT 1 FROM cargo_disciplina x WHERE x.cargo_id = cc.id AND x.disciplina_id = d.id);""",
        "",
        "-- Recalcula o total de topicos so onde ele ja era contado (NULL = catalogo, fica NULL).",
        """UPDATE cargo_disciplina cd
SET total_topicos = (SELECT COUNT(*) FROM assunto a WHERE a.disciplina_id = cd.disciplina_id)
WHERE cd.total_topicos IS NOT NULL;""",
        "",
    ]


def gerar(lote):
    QUESTOES = LOTES[lote]
    DISCIPLINAS = DISCIPLINAS_POR_LOTE[lote]
    linhas = [
        f"-- Lote {lote} de questoes AUTORAIS, gerado por scripts/gerar_questoes.py.",
        "-- Nao edite a mao: altere o script e gere uma nova versao (V16, V17...).",
        "--",
        "-- Questoes escritas no estilo das bancas, nao copiadas de provas (o texto",
        "-- das provas e protegido por direito autoral das bancas).",
        "",
        "-- Disciplinas novas",
    ]
    for d in DISCIPLINAS:
        linhas.append(f"INSERT INTO disciplina (nome) SELECT {sql(d)}\n"
                      f"WHERE NOT EXISTS (SELECT 1 FROM disciplina WHERE nome = {sql(d)});")

    linhas += ["", "-- Assuntos (so cria os que ainda nao existem)"]
    vistos = []
    for q in QUESTOES:
        chave = (q["disc"], q["assunto"])
        if chave in vistos:
            continue
        vistos.append(chave)
        linhas.append(
            f"INSERT INTO assunto (disciplina_id, nome)\n"
            f"SELECT d.id, {sql(q['assunto'])} FROM disciplina d WHERE d.nome = {sql(q['disc'])}\n"
            f"  AND NOT EXISTS (SELECT 1 FROM assunto a\n"
            f"                  WHERE lower(a.nome) = lower({sql(q['assunto'])}) AND a.disciplina_id = d.id);")

    linhas += ["", "-- Questoes"]
    contador_me = 0
    for q in QUESTOES:
        if q["tipo"] == "CERTO_ERRADO":
            alts = [("Certo", q["gabarito"]), ("Errado", not q["gabarito"])]
        else:
            # Posicao da correta em rodizio (A, B, C...) para o gabarito ficar
            # equilibrado; as erradas sao embaralhadas de forma deterministica.
            erradas = [(t, False) for t in q["alternativas"][1:]]
            semente = int(hashlib.sha256(q["enunciado"].encode()).hexdigest(), 16)
            random.Random(semente).shuffle(erradas)
            pos = contador_me % len(q["alternativas"])
            contador_me += 1
            alts = erradas[:pos] + [(q["alternativas"][0], True)] + erradas[pos:]
        valores = ",\n".join(
            f"        ({sql(t)}, {'TRUE' if c else 'FALSE'}, {i})" for i, (t, c) in enumerate(alts, 1))
        linhas.append(f"""WITH nova AS (
    INSERT INTO questao (enunciado, disciplina_id, banca_id, ano, assunto, assunto_id, explicacao, tipo)
    SELECT {sql(q['enunciado'])},
           d.id, b.id, {q['ano']}, {sql(q['assunto'])}, a.id,
           {sql(q['explicacao'])},
           {sql(q['tipo'])}
    FROM disciplina d
    JOIN banca b ON b.nome = {sql(q['banca'])}
    JOIN assunto a ON a.disciplina_id = d.id AND lower(a.nome) = lower({sql(q['assunto'])})
    WHERE d.nome = {sql(q['disc'])}
    RETURNING id
)
INSERT INTO alternativa (questao_id, texto, correta, ordem)
SELECT nova.id, v.texto, v.correta, v.ordem
FROM nova, (VALUES
{valores}
) AS v(texto, correta, ordem);
""")

    if int(lote) >= 6:
        return "\n".join(linhas + rodape_novo(QUESTOES))

    linhas += [
        "-- O cargo do concurso de exemplo passa a cobrar tambem as disciplinas novas,",
        "-- para que o objetivo de estudo reflita o conteudo disponivel.",
        f"""INSERT INTO cargo_disciplina (cargo_id, disciplina_id, total_topicos, ordem)
SELECT cc.id, d.id, 0,
       (SELECT COALESCE(MAX(x.ordem), 0) FROM cargo_disciplina x WHERE x.cargo_id = cc.id)
         + ROW_NUMBER() OVER (PARTITION BY cc.id ORDER BY d.nome)
FROM concurso_cargo cc
CROSS JOIN disciplina d
WHERE cc.nome = {sql(CARGO_EXEMPLO)}
  AND NOT EXISTS (SELECT 1 FROM cargo_disciplina x WHERE x.cargo_id = cc.id AND x.disciplina_id = d.id);""",
        "",
        "-- Recalcula o total de topicos de cada disciplina do conteudo programatico.",
        """UPDATE cargo_disciplina cd
SET total_topicos = (SELECT COUNT(*) FROM assunto a WHERE a.disciplina_id = cd.disciplina_id);""",
        "",
    ]
    return "\n".join(linhas)


if __name__ == "__main__":
    if len(sys.argv) != 3 or sys.argv[1] not in LOTES:
        print(f"uso: python scripts/gerar_questoes.py <lote: {', '.join(LOTES)}> <arquivo de saida>")
        sys.exit(1)
    lote, destino = sys.argv[1], sys.argv[2]
    with open(destino, "w", encoding="utf-8", newline="\n") as f:
        f.write(gerar(lote))
    qs = LOTES[lote]
    me_ = sum(q["tipo"] == "MULTIPLA_ESCOLHA" for q in qs)
    print(f"{destino}: {len(qs)} questoes ({me_} multipla escolha, {len(qs) - me_} certo/errado)")

"""
Gera uma migration Flyway com um lote de questoes AUTORAIS.

Uso:
    python scripts/gerar_questoes.py <lote> <arquivo de saida>
    python scripts/gerar_questoes.py 3 src/main/resources/db/migration/V18__lote_questoes_3.sql

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
}

# Cargo do concurso de exemplo que passa a cobrar tambem as disciplinas novas.
CARGO_EXEMPLO = "Analista Administrativo"

CESPE, FGV = "CESPE/CEBRASPE", "FGV"
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

LOTES = {"2": LOTE_2, "3": LOTE_3}


def sql(texto):
    return "'" + texto.replace("'", "''") + "'"


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

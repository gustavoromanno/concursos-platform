// Frontend em JS puro, servido pelo proprio Spring Boot.
// Sem build, sem framework: abre em http://localhost:8080

const API = '';
let token = localStorage.getItem('token');
let modoRegistro = false;
let simuladoAtual = null;
let cronometro = null;
let disciplinas = [];   // catalogo carregado uma vez apos o login
let marcados = new Set();  // ids das questoes marcadas pelo usuario
let anotadas = new Set();  // ids das questoes com anotacao do usuario
let concursosFiltro = [];  // concursos (com cargos) do filtro de questoes
let atualizacaoImportacoes = null;  // timer de atualizacao da tela de importacao
let importacaoEmRevisao = null;
let cadernos = [];         // cadernos do usuario, para o menu de salvar
let perfilUsuario = null;  // nome e email de quem esta logado

// ---------- tema claro / noturno ----------

// Icones Lucide (licenca ISC), inline para nao depender de CDN.
const SVG = corpo => `<svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${corpo}</svg>`;
const ICONE_LUA = SVG('<path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"/>');
const ICONE_SOL = SVG('<circle cx="12" cy="12" r="4"/><path d="M12 2v2"/><path d="M12 20v2"/><path d="m4.93 4.93 1.41 1.41"/><path d="m17.66 17.66 1.41 1.41"/><path d="M2 12h2"/><path d="M20 12h2"/><path d="m6.34 17.66-1.41 1.41"/><path d="m19.07 4.93-1.41 1.41"/>');

function temaAtual() {
    return document.documentElement.getAttribute('data-tema') === 'escuro' ? 'escuro' : 'claro';
}

function aplicarTema(tema) {
    if (tema === 'escuro') {
        document.documentElement.setAttribute('data-tema', 'escuro');
    } else {
        document.documentElement.removeAttribute('data-tema');
    }
    localStorage.setItem('tema', tema);
    // O ícone mostra para onde o clique leva, não o estado atual.
    const botao = document.querySelector('#btn-tema');
    if (botao) botao.innerHTML = tema === 'escuro' ? ICONE_SOL : ICONE_LUA;
}

document.querySelector('#btn-tema').onclick = () => {
    aplicarTema(temaAtual() === 'escuro' ? 'claro' : 'escuro');
    // Redesenha o painel: os gráficos SVG pegam cor no momento em que são criados.
    if (!document.querySelector('#tela-dashboard').classList.contains('hidden')) {
        carregarDashboard();
    }
};

aplicarTema(temaAtual());

// ---------- infraestrutura ----------

async function api(caminho, opcoes = {}) {
    const ehFormulario = opcoes.body instanceof FormData;
    const resp = await fetch(API + caminho, {
        ...opcoes,
        headers: {
            ...(ehFormulario ? {} : { 'Content-Type': 'application/json' }),
            ...(token ? { Authorization: 'Bearer ' + token } : {}),
            ...(opcoes.headers || {})
        }
    });

    // 401 = nao identificado. Com token, a sessao expirou; sem token, e o
    // login que falhou. 403 = identificado, mas sem permissao: nao desloga.
    if (resp.status === 401) {
        if (token) {
            sair();
            throw new Error('Sessão expirada. Entre novamente.');
        }
        throw new Error('E-mail ou senha incorretos.');
    }
    if (resp.status === 403) {
        throw new Error('Você não tem permissão para esta ação.');
    }

    if (!resp.ok) {
        const corpo = await resp.json().catch(() => ({}));
        // O Spring devolve a mensagem em "message" ou "detail" conforme o caso.
        throw new Error(corpo.message || corpo.detail || mensagemPadrao(resp.status));
    }

    return resp.status === 204 ? null : resp.json();
}

function mensagemPadrao(status) {
    if (status === 409) return 'Esse e-mail já está cadastrado.';
    if (status === 400) return 'Dados inválidos.';
    if (status === 404) return 'Não encontrado.';
    return 'Erro na requisição (HTTP ' + status + ')';
}

const $ = sel => document.querySelector(sel);
const criar = html => {
    const div = document.createElement('div');
    div.innerHTML = html.trim();
    return div.firstElementChild;
};
const escapar = txt => String(txt ?? '').replace(/[&<>"]/g, c =>
    ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));

// ---------- autenticacao ----------

$('#btn-alternar').onclick = () => {
    modoRegistro = !modoRegistro;
    $('#campos-registro').classList.toggle('hidden', !modoRegistro);
    $('#extras-registro').classList.toggle('hidden', !modoRegistro);
    $('#login-senha').autocomplete = modoRegistro ? 'new-password' : 'current-password';
    $('#btn-entrar').textContent = modoRegistro ? 'Criar conta' : 'Entrar';
    $('#alternar-texto').textContent = modoRegistro ? 'Já tem conta?' : 'Não tem conta?';
    $('#btn-alternar').textContent = modoRegistro ? 'Fazer login' : 'Criar conta';
    $('#login-erro').textContent = '';
};

$('#btn-entrar').onclick = async () => {
    const botao = $('#btn-entrar');
    if (botao.disabled) return;   // evita cadastro duplicado por duplo clique

    const email = $('#login-email').value.trim().toLowerCase();
    const senha = $('#login-senha').value;
    const erro = $('#login-erro');
    erro.textContent = '';

    const textoOriginal = botao.textContent;
    botao.disabled = true;
    botao.textContent = modoRegistro ? 'Criando conta…' : 'Entrando…';

    try {
        if (modoRegistro) {
            const nome = $('#reg-nome').value.trim();
            if (!nome) throw new Error('Informe seu nome');
            if (senha.length < 6) throw new Error('A senha precisa ter pelo menos 6 caracteres');
            await api('/auth/registrar', {
                method: 'POST',
                body: JSON.stringify({ nome, email, senha, aceitaMarketing: $('#reg-marketing').checked })
            });
        }

        const dados = await api('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, senha })
        });

        token = dados.token;
        localStorage.setItem('token', token);
        await entrarNoApp();
    } catch (e) {
        erro.textContent = e.message;
    } finally {
        botao.disabled = false;
        botao.textContent = textoOriginal;
    }
};

// Enter no formulario de login/cadastro envia.
['#login-email', '#login-senha', '#reg-nome'].forEach(sel => {
    $(sel).addEventListener('keydown', e => { if (e.key === 'Enter') $('#btn-entrar').click(); });
});

function sair() {
    token = null;
    localStorage.removeItem('token');
    pararCronometro();
    simuladoAtual = null;
    perfilUsuario = null;
    $('#usuario-topo').textContent = '';
    $('#topo').classList.add('hidden');
    document.querySelectorAll('main section').forEach(s => s.classList.add('hidden'));
    $('#tela-login').classList.remove('hidden');
}

$('#sair').onclick = sair;

async function entrarNoApp() {
    $('#tela-login').classList.add('hidden');
    $('#topo').classList.remove('hidden');
    await carregarCatalogo();
    atualizarSeloRevisao();
    abrir('inicio');   // pagina inicial da plataforma
}

// ---------- catalogo (disciplinas e assuntos) ----------

// Carregado uma vez e reaproveitado por todos os selects da interface.
async function carregarCatalogo() {
    try {
        disciplinas = await api('/disciplinas');
    } catch {
        disciplinas = [];
    }

    const preencher = (sel, rotuloVazio) => {
        const el = $(sel);
        if (!el) return;
        el.innerHTML = rotuloVazio ? `<option value="">${rotuloVazio}</option>` : '';
        disciplinas.forEach(d =>
            el.appendChild(criar(`<option value="${d.id}">${escapar(d.nome)}</option>`)));
    };

    await carregarEstadoPessoal();

    // Órgãos alimentam o filtro da listagem.
    try {
        const orgaos = await api('/orgaos');
        const sel = $('#f-orgao');
        if (sel) {
            sel.innerHTML = '<option value="">Órgão</option>';
            orgaos.forEach(o => sel.appendChild(
                criar(`<option value="${o.id}">${escapar(o.sigla || o.nome)}</option>`)));
        }
    } catch { /* filtro opcional */ }

    preencher('#f-disciplina', 'Disciplina');

    // Bancas e anos do painel de filtros de questões.
    try {
        const bancas = await api('/bancas');
        const selBanca = $('#f-banca');
        selBanca.innerHTML = '<option value="">Banca</option>';
        bancas.forEach(b => selBanca.appendChild(
            criar(`<option value="${b.id}">${escapar(b.nome)}</option>`)));
        // A mesma lista alimenta a banca do simulado.
        const selBancaSimulado = $('#s-banca');
        selBancaSimulado.innerHTML = '<option value="">Todas</option>';
        bancas.forEach(b => selBancaSimulado.appendChild(
            criar(`<option value="${b.id}">${escapar(b.nome)}</option>`)));
    } catch { /* filtro opcional */ }
    // Concursos e cargos para o filtro (ex.: "Bacen 2013" > "Analista").
    try {
        concursosFiltro = await api('/concursos');
        const selConcurso = $('#f-concurso');
        selConcurso.innerHTML = '<option value="">Concurso</option>';
        concursosFiltro.forEach(c => selConcurso.appendChild(
            criar(`<option value="${c.id}">${escapar(c.nome)}</option>`)));
    } catch {
        concursosFiltro = [];
    }
    atualizarCargosFiltro();

    const selAno = $('#f-ano');
    selAno.innerHTML = '<option value="">Ano</option>';
    for (let ano = new Date().getFullYear(); ano >= 2010; ano--) {
        selAno.appendChild(criar(`<option value="${ano}">${ano}</option>`));
    }
    preencher('#s-disciplina', 'Todas');
    preencher('#vf-disciplina', 'Todas');
    preencher('#v-disciplina', null);

    atualizarAssuntosFiltro();
    atualizarAssuntos('#v-disciplina', '#v-assunto', 'Geral da disciplina');
}

// Marcadores e cadernos do usuario: carregados uma vez para os cards ja
// nascerem com o estado certo.
async function carregarEstadoPessoal() {
    try {
        perfilUsuario = await api('/perfil');
        atualizarUsuarioTopo(perfilUsuario.nome);
        document.querySelectorAll('.so-admin').forEach(el =>
            el.classList.toggle('hidden', perfilUsuario.papel !== 'ADMIN'));
    } catch {
        perfilUsuario = null;
    }
    try {
        marcados = new Set(await api('/marcadores/ids'));
    } catch {
        marcados = new Set();
    }
    try {
        anotadas = new Set(await api('/anotacoes/ids'));
    } catch {
        anotadas = new Set();
    }
    try {
        cadernos = await api('/cadernos');
    } catch {
        cadernos = [];
    }
}

// Filtro em cascata: trocar a disciplina recarrega a lista de assuntos.
function atualizarAssuntos(selDisciplina, selAssunto, rotuloVazio) {
    const idDisciplina = $(selDisciplina)?.value;
    const alvo = $(selAssunto);
    if (!alvo) return;

    alvo.innerHTML = `<option value="">${rotuloVazio}</option>`;
    const disciplina = disciplinas.find(d => String(d.id) === String(idDisciplina));
    if (!disciplina) return;

    // Achata a arvore: assunto pai e subassuntos indentados.
    const achatar = (lista, nivel = 0) => {
        lista.forEach(a => {
            const prefixo = nivel ? '— '.repeat(nivel) : '';
            alvo.appendChild(criar(`<option value="${a.id}">${prefixo}${escapar(a.nome)}</option>`));
            if (a.subassuntos?.length) achatar(a.subassuntos, nivel + 1);
        });
    };
    achatar(disciplina.assuntos || []);
}

// No painel de questões, "Assunto" só habilita depois de escolher a disciplina.
function atualizarAssuntosFiltro() {
    atualizarAssuntos('#f-disciplina', '#f-assunto-id', 'Assunto');
    $('#f-assunto-id').disabled = !$('#f-disciplina').value;
}

$('#f-disciplina').onchange = atualizarAssuntosFiltro;


// "Cargo" so habilita depois de escolher o concurso, com os cargos dele.
function atualizarCargosFiltro() {
    const sel = $('#f-cargo');
    const concurso = concursosFiltro.find(c => String(c.id) === $('#f-concurso').value);
    sel.innerHTML = '<option value="">Cargo</option>';
    (concurso?.cargos || []).forEach(c => sel.appendChild(
        criar(`<option value="${c.id}">${escapar(c.nome)}</option>`)));
    sel.disabled = !concurso || !(concurso.cargos || []).length;
}

$('#f-concurso').onchange = atualizarCargosFiltro;
$('#v-disciplina').onchange = () => atualizarAssuntos('#v-disciplina', '#v-assunto', 'Geral da disciplina');

// ---------- navegacao ----------

document.querySelectorAll('#topo nav button').forEach(btn => {
    btn.title = btn.textContent.trim();
    btn.onclick = () => abrir(btn.dataset.tela);
});

function abrir(tela) {
    document.querySelectorAll('main section').forEach(s => s.classList.add('hidden'));
    document.querySelectorAll('#topo nav button').forEach(b =>
        b.classList.toggle('ativo', b.dataset.tela === tela));
    $('#tela-' + tela).classList.remove('hidden');

    if (tela === 'questoes') carregarQuestoes(0);
    if (tela === 'inicio') carregarInicio();
    if (tela === 'revisao') carregarRevisao();
    if (tela === 'erradas') carregarErradas();
    if (tela === 'cadernos') carregarCadernos();
    if (tela === 'marcadores') carregarMarcadores();
    if (tela === 'concursos') carregarConcursos();
    if (tela === 'videoaulas') carregarVideoaulas();
    if (tela === 'dashboard') carregarDashboard();
    if (tela === 'conta') carregarConta();
    if (tela === 'importacoes') carregarImportacoes();
}

// ---------- minha conta ----------

function atualizarUsuarioTopo(nome) {
    $('#usuario-topo').textContent = nome;
    const partes = (nome || '').trim().split(/\s+/).filter(Boolean);
    const iniciais = partes.length > 1
        ? partes[0][0] + partes[partes.length - 1][0]
        : (partes[0] || '?').slice(0, 2);
    $('#avatar-topo').textContent = iniciais.toUpperCase();
}

function carregarConta() {
    $('#conta-marketing').checked = !!perfilUsuario?.aceitaMarketing;
    $('#msg-marketing').textContent = '';
    $('#conta-nome').value = perfilUsuario?.nome || '';
    $('#conta-email').value = perfilUsuario?.email || '';
    ['#msg-nome', '#msg-senha'].forEach(sel => { $(sel).textContent = ''; });
    ['#conta-senha-atual', '#conta-senha-nova', '#conta-senha-confirma'].forEach(sel => { $(sel).value = ''; });
}

$('#btn-conta').onclick = () => abrir('conta');

$('#btn-salvar-nome').onclick = async () => {
    const msg = $('#msg-nome');
    const nome = $('#conta-nome').value.trim();
    if (!nome) {
        msg.textContent = 'Informe um nome.';
        return;
    }
    try {
        perfilUsuario = await api('/perfil', { method: 'PUT', body: JSON.stringify({ nome }) });
        atualizarUsuarioTopo(perfilUsuario.nome);
        msg.textContent = 'Nome atualizado.';
    } catch (e) {
        msg.textContent = e.message;
    }
};

$('#conta-marketing').onchange = async () => {
    const caixa = $('#conta-marketing');
    const msg = $('#msg-marketing');
    try {
        perfilUsuario = await api('/perfil/marketing', {
            method: 'PUT', body: JSON.stringify({ aceita: caixa.checked })
        });
        msg.textContent = caixa.checked ? 'Você vai receber nossas novidades.' : 'Você não vai mais receber e-mails promocionais.';
    } catch (e) {
        caixa.checked = !caixa.checked;
        msg.textContent = e.message;
    }
};

// Baixa o CSV com o token (um link simples nao enviaria a autenticacao).
$('#btn-exportar-contatos').onclick = async () => {
    const msg = $('#msg-exportar');
    msg.textContent = 'Gerando…';
    try {
        const resp = await fetch(API + '/admin/usuarios/contatos-marketing.csv', {
            headers: { Authorization: 'Bearer ' + token }
        });
        if (!resp.ok) throw new Error(resp.status === 403 ? 'Você não tem permissão para esta ação.' : 'Não foi possível gerar a lista.');
        const url = URL.createObjectURL(await resp.blob());
        const link = document.createElement('a');
        link.href = url;
        link.download = 'contatos-marketing.csv';
        link.click();
        URL.revokeObjectURL(url);
        msg.textContent = '';
    } catch (e) {
        msg.textContent = e.message;
    }
};

$('#btn-salvar-senha').onclick = async () => {
    const msg = $('#msg-senha');
    const senhaAtual = $('#conta-senha-atual').value;
    const novaSenha = $('#conta-senha-nova').value;
    if (novaSenha.length < 6) {
        msg.textContent = 'A nova senha deve ter pelo menos 6 caracteres.';
        return;
    }
    if (novaSenha !== $('#conta-senha-confirma').value) {
        msg.textContent = 'A confirmação não confere com a nova senha.';
        return;
    }
    try {
        await api('/perfil/senha', { method: 'PUT', body: JSON.stringify({ senhaAtual, novaSenha }) });
        ['#conta-senha-atual', '#conta-senha-nova', '#conta-senha-confirma'].forEach(sel => { $(sel).value = ''; });
        msg.textContent = 'Senha alterada.';
    } catch (e) {
        msg.textContent = e.message;
    }
};

// ---------- questoes ----------

function cardQuestao(questao, aoResponder, opcoes = {}) {
    const card = criar(`
        <div class="card">
            <div class="cabecalho-questao">
                <div class="meta">${escapar(questao.disciplina)} · ${escapar(questao.banca)}${questao.orgao ? ' · ' + escapar(questao.orgao) : ''} · ${questao.ano}${questao.assunto ? ' · ' + escapar(questao.assunto) : ''}${questao.tipo === 'CERTO_ERRADO' ? ' <span class="selo-tipo">C/E</span>' : ''}${questao.origem === 'IA' ? ' <span class="selo-tipo selo-inedita" title="Criada por IA a partir de uma prova antiga e revisada antes de publicar">INÉDITA</span>' : ''}</div>
                <div class="acoes-questao"></div>
            </div>
            <div class="enunciado">${escapar(questao.enunciado)}</div>
            <div class="alternativas"></div>
            <div class="area-feedback"></div>
        </div>
    `);

    // A barra de acoes nao aparece durante o simulado (opcoes.semAcoes).
    if (!opcoes.semAcoes) {
        montarAcoes(card.querySelector('.acoes-questao'), questao, opcoes);
    }

    const alternativas = card.querySelector('.alternativas');

    // Certo/Errado ganha dois botões lado a lado, no formato do CESPE,
    // em vez da lista A) B) C) da múltipla escolha.
    if (questao.tipo === 'CERTO_ERRADO') {
        alternativas.classList.add('certo-errado');
        questao.alternativas.forEach(alt => {
            const botao = criar(`<button class="alternativa botao-ce">${escapar(alt.texto)}</button>`);
            botao.onclick = () => aoResponder(questao, alt, card);
            alternativas.appendChild(botao);
        });
    } else {
        questao.alternativas.forEach((alt, i) => {
            const botao = criar(`<button class="alternativa">${String.fromCharCode(65 + i)}) ${escapar(alt.texto)}</button>`);
            botao.onclick = () => aoResponder(questao, alt, card);
            alternativas.appendChild(botao);
        });
    }

    return card;
}

// Marcar (favoritar) e salvar em caderno.
function montarAcoes(alvo, questao, opcoes) {
    const marcar = criar(`<button class="acao" title="Marcar questão">${marcados.has(questao.id) ? '★' : '☆'}</button>`);
    marcar.classList.toggle('ativa', marcados.has(questao.id));
    marcar.onclick = async () => {
        try {
            const r = await api('/marcadores', {
                method: 'POST',
                body: JSON.stringify({ questaoId: questao.id })
            });
            if (r.marcada) marcados.add(questao.id); else marcados.delete(questao.id);
            marcar.textContent = r.marcada ? '★' : '☆';
            marcar.classList.toggle('ativa', r.marcada);
        } catch (e) {
            alert(e.message);
        }
    };
    alvo.appendChild(marcar);

    const salvar = criar('<button class="acao" title="Salvar em caderno">+ caderno</button>');
    salvar.onclick = () => abrirMenuCadernos(salvar, questao);
    alvo.appendChild(salvar);

    const anotar = criar('<button class="acao" title="Minha anotação (só você vê)">✎ anotação</button>');
    anotar.classList.toggle('ativa', anotadas.has(questao.id));
    anotar.onclick = () => alternarAnotacao(anotar, questao);
    alvo.appendChild(anotar);

    // Dentro de um caderno aberto, oferece remover em vez de adicionar.
    if (opcoes.cadernoId) {
        const remover = criar('<button class="acao" title="Remover do caderno">remover</button>');
        remover.onclick = async () => {
            await api(`/cadernos/${opcoes.cadernoId}/questoes/${questao.id}`, { method: 'DELETE' });
            abrirCaderno(opcoes.cadernoId);
        };
        alvo.appendChild(remover);
    }
}

// Painel de anotacao pessoal, aberto sob a questao. Carrega o texto so ao abrir.
async function alternarAnotacao(botao, questao) {
    const card = botao.closest('.card');
    const aberto = card.querySelector('.painel-anotacao');
    if (aberto) {
        aberto.remove();
        return;
    }

    const painel = criar(`
        <div class="painel-anotacao">
            <label class="rotulo">Minha anotação <span class="sub">— visível só para você</span>
                <textarea rows="3" maxlength="5000" placeholder="Ex.: pegadinha da banca, artigo de lei, macete…"></textarea>
            </label>
            <div class="acoes-anotacao">
                <button class="primario salvar-anotacao">Salvar</button>
                <button class="link apagar-anotacao">Apagar</button>
                <span class="sub status-anotacao"></span>
            </div>
        </div>
    `);
    card.querySelector('.enunciado').after(painel);

    const campo = painel.querySelector('textarea');
    const status = painel.querySelector('.status-anotacao');
    const apagar = painel.querySelector('.apagar-anotacao');

    const atualizarBotoes = temAnotacao => {
        botao.classList.toggle('ativa', temAnotacao);
        apagar.classList.toggle('hidden', !temAnotacao);
        if (temAnotacao) anotadas.add(questao.id); else anotadas.delete(questao.id);
    };

    try {
        const atual = await api(`/questoes/${questao.id}/anotacao`);
        campo.value = atual ? atual.texto : '';
        atualizarBotoes(!!atual);
    } catch (e) {
        status.textContent = e.message;
    }
    campo.focus();

    const gravar = async texto => {
        status.textContent = 'Salvando…';
        try {
            const r = await api(`/questoes/${questao.id}/anotacao`, {
                method: 'PUT',
                body: JSON.stringify({ texto })
            });
            campo.value = r ? r.texto : '';
            atualizarBotoes(!!r);
            status.textContent = r ? 'Anotação salva.' : 'Anotação apagada.';
        } catch (e) {
            status.textContent = e.message;
        }
    };

    painel.querySelector('.salvar-anotacao').onclick = () => gravar(campo.value);
    apagar.onclick = () => gravar('');
}

// Menu suspenso simples com os cadernos do usuario.
function abrirMenuCadernos(botao, questao) {
    document.querySelectorAll('.menu-cadernos').forEach(m => m.remove());

    const menu = criar('<div class="menu-cadernos"></div>');
    if (!cadernos.length) {
        menu.appendChild(criar('<div class="sub" style="margin:0">Crie um caderno primeiro.</div>'));
    }

    cadernos.forEach(c => {
        const item = criar(`<button class="item-menu">${escapar(c.nome)}</button>`);
        item.onclick = async () => {
            try {
                await api(`/cadernos/${c.id}/questoes`, {
                    method: 'POST',
                    body: JSON.stringify({ questaoId: questao.id })
                });
                item.textContent = 'Salvo em ' + c.nome;
                setTimeout(() => menu.remove(), 900);
            } catch (e) {
                item.textContent = e.message;
            }
        };
        menu.appendChild(item);
    });

    botao.parentElement.appendChild(menu);
    setTimeout(() => {
        document.addEventListener('click', function fechar(ev) {
            if (!menu.contains(ev.target) && ev.target !== botao) {
                menu.remove();
                document.removeEventListener('click', fechar);
            }
        });
    }, 0);
}

function travarAlternativas(card) {
    card.querySelectorAll('.alternativa').forEach(b => b.disabled = true);
}

async function responderDireto(questao, alternativa, card) {
    travarAlternativas(card);

    const resultado = await api(`/questoes/${questao.id}/responder`, {
        method: 'POST',
        body: JSON.stringify({ alternativaId: alternativa.id })
    });

    questao.alternativas.forEach((alt, i) => {
        const botao = card.querySelectorAll('.alternativa')[i];
        if (alt.id === resultado.alternativaCorretaId) botao.classList.add('certa');
        if (alt.id === alternativa.id) {
            botao.classList.add('escolhida');
            if (!resultado.correta) botao.classList.add('errada');
        }
    });

    const area = card.querySelector('.area-feedback');
    area.appendChild(criar(`
        <div class="feedback ${resultado.correta ? 'ok' : 'nok'}">
            <strong>${resultado.correta ? 'Acertou.' : 'Errou.'}</strong>
            ${resultado.explicacao ? ' ' + escapar(resultado.explicacao) : ''}
        </div>
    `));

    // Depois de responder, libera estatística coletiva e comentários.
    mostrarEstatisticasQuestao(questao.id, area);
    montarComentarios(questao.id, area);

    // Errou: oferece videoaula do assunto daquela questao.
    if (!resultado.correta) {
        try {
            const videos = await api('/videoaulas/questao/' + questao.id);
            if (videos.length) {
                const bloco = criar('<div class="sugestao-estudo"><strong>Estude esse assunto:</strong></div>');
                videos.slice(0, 3).forEach(v => {
                    const link = criar(`<button class="link bloco-link">▶ ${escapar(v.titulo)}</button>`);
                    link.onclick = () => abrirVideo(v);
                    bloco.appendChild(link);
                });
                area.appendChild(bloco);
            }
        } catch { /* sem video cadastrado: segue sem sugestao */ }
    }
}

// Valor do chip ativo em cada grupo (um por grupo; clicar de novo desmarca).
function chipAtivo(grupo) {
    return document.querySelector(`.chip.ativo[data-grupo="${grupo}"]`)?.dataset.valor || '';
}

document.querySelectorAll('.chip[data-grupo]').forEach(chip => {
    chip.onclick = () => {
        const jaAtivo = chip.classList.contains('ativo');
        document.querySelectorAll(`.chip[data-grupo="${chip.dataset.grupo}"]`)
            .forEach(c => c.classList.remove('ativo'));
        if (!jaAtivo) chip.classList.add('ativo');
    };
});

const POR_PAGINA = 20;

// pagina começa em 0 (convenção do Spring). Ordenar por id deixa a paginação
// estável: sem ORDER BY, o banco pode repetir ou pular questões entre páginas.
async function carregarQuestoes(pagina = 0) {
    const alvo = $('#lista-questoes');
    const resumo = $('#resumo-questoes');
    const paginacao = $('#paginacao-questoes');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';
    resumo.textContent = '';
    paginacao.innerHTML = '';

    const params = new URLSearchParams({ size: POR_PAGINA, page: pagina, sort: 'id' });
    const filtros = {
        palavraChave: $('#f-palavra').value.trim(),
        disciplinaId: $('#f-disciplina').value,
        assuntoId: $('#f-assunto-id').value,
        bancaId: $('#f-banca').value,
        orgaoId: $('#f-orgao').value,
        ano: $('#f-ano').value,
        concursoId: $('#f-concurso').value,
        cargoId: $('#f-cargo').value,
        origem: chipAtivo('origem'),
        tipo: chipAtivo('tipo'),
        comComentarios: chipAtivo('comentarios'),
        comAnotacoes: chipAtivo('anotacoes'),
        dificuldade: chipAtivo('dificuldade'),
        situacao: chipAtivo('situacao')
    };
    Object.entries(filtros).forEach(([chave, valor]) => { if (valor) params.set(chave, valor); });

    try {
        const resultado = await api('/questoes?' + params);
        alvo.innerHTML = '';
        if (!resultado.content.length) {
            alvo.appendChild(criar('<div class="vazio">Nenhuma questão encontrada com esses filtros.</div>'));
            return;
        }
        // A API serializa paginas "VIA_DTO": os metadados vem dentro de "page"
        // (ver ConcursosPlatformApplication). O fallback cobre o formato antigo.
        const meta = resultado.page ?? resultado;
        const total = meta.totalElements;
        const inicio = meta.number * POR_PAGINA + 1;
        const fim = inicio + resultado.content.length - 1;
        resumo.textContent = total === 1
            ? '1 questão encontrada.'
            : `${total} questões encontradas${meta.totalPages > 1 ? ` — exibindo ${inicio} a ${fim}` : ''}.`;
        resultado.content.forEach(q => alvo.appendChild(cardQuestao(q, responderDireto)));
        montarPaginacao(paginacao, meta);
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

// Anterior / numeros / Proxima. Mostra ate 5 numeros em volta da pagina atual.
// Recebe os metadados da pagina ({ number, totalPages, ... }).
function montarPaginacao(alvo, meta) {
    alvo.innerHTML = '';
    const totalPaginas = meta.totalPages;
    if (totalPaginas <= 1) return;

    const atual = meta.number;
    const botao = (rotulo, pagina, { ativo = false, desabilitado = false } = {}) => {
        const b = criar(`<button class="pagina${ativo ? ' ativa' : ''}">${rotulo}</button>`);
        b.disabled = desabilitado || ativo;
        if (!b.disabled) b.onclick = () => {
            carregarQuestoes(pagina);
            $('#resumo-questoes').scrollIntoView({ behavior: 'smooth', block: 'start' });
        };
        return b;
    };

    alvo.appendChild(botao('‹ Anterior', atual - 1, { desabilitado: atual === 0 }));
    const inicio = Math.max(0, Math.min(atual - 2, totalPaginas - 5));
    const fim = Math.min(totalPaginas, inicio + 5);
    for (let p = inicio; p < fim; p++) {
        alvo.appendChild(botao(String(p + 1), p, { ativo: p === atual }));
    }
    alvo.appendChild(botao('Próxima ›', atual + 1, { desabilitado: atual >= totalPaginas - 1 }));
}

$('#f-palavra').onkeydown = e => { if (e.key === 'Enter') carregarQuestoes(0); };

$('#btn-limpar-filtros').onclick = () => {
    $('#f-palavra').value = '';
    ['#f-disciplina', '#f-banca', '#f-orgao', '#f-ano', '#f-concurso'].forEach(sel => { $(sel).value = ''; });
    atualizarAssuntosFiltro();
    atualizarCargosFiltro();
    document.querySelectorAll('.chip.ativo').forEach(c => c.classList.remove('ativo'));
    carregarQuestoes();
};

// O simulado sorteia por disciplina e banca: leva as duas escolhidas no filtro.
$('#btn-gerar-simulado').onclick = () => {
    $('#s-disciplina').value = $('#f-disciplina').value;
    $('#s-banca').value = $('#f-banca').value;
    abrir('simulado');
};

// Arrow function: onclick passaria o evento como "pagina".
$('#btn-filtrar').onclick = () => carregarQuestoes(0);

async function carregarErradas() {
    const alvo = $('#lista-erradas');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        const questoes = await api('/questoes/erradas');
        alvo.innerHTML = '';
        if (!questoes.length) {
            alvo.appendChild(criar('<div class="vazio">Nada para revisar. Você não tem questões erradas pendentes.</div>'));
            return;
        }
        questoes.forEach(q => alvo.appendChild(cardQuestao(q, responderDireto)));
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

// ---------- simulado ----------

$('#btn-iniciar-simulado').onclick = async () => {
    $('#simulado-erro').textContent = '';
    const disciplinaId = $('#s-disciplina').value;
    const bancaId = $('#s-banca').value;
    try {
        simuladoAtual = await api('/simulados', {
            method: 'POST',
            body: JSON.stringify({
                quantidade: Number($('#s-quantidade').value),
                duracaoMinutos: Number($('#s-duracao').value),
                disciplinaId: disciplinaId ? Number(disciplinaId) : null,
                bancaId: bancaId ? Number(bancaId) : null
            })
        });
        mostrarSimulado();
    } catch (e) {
        $('#simulado-erro').textContent = e.message;
    }
};

function mostrarSimulado() {
    $('#simulado-config').classList.add('hidden');
    $('#simulado-resultado').classList.add('hidden');
    $('#simulado-andamento').classList.remove('hidden');

    const alvo = $('#lista-simulado');
    alvo.innerHTML = '';

    simuladoAtual.questoes.forEach(q => {
        alvo.appendChild(cardQuestao(q, async (questao, alternativa, card) => {
            travarAlternativas(card);
            card.querySelectorAll('.alternativa').forEach((b, i) => {
                if (questao.alternativas[i].id === alternativa.id) b.classList.add('escolhida');
            });
            try {
                simuladoAtual = await api(`/simulados/${simuladoAtual.id}/questoes/${questao.id}/responder`, {
                    method: 'POST',
                    body: JSON.stringify({ alternativaId: alternativa.id })
                });
                atualizarProgresso();
            } catch (e) {
                card.querySelector('.area-feedback').appendChild(
                    criar(`<div class="feedback nok">${escapar(e.message)}</div>`));
            }
        }, { semAcoes: true }));
    });

    atualizarProgresso();
    iniciarCronometro(simuladoAtual.segundosRestantes);
}

function atualizarProgresso() {
    $('#progresso-simulado').textContent =
        `${simuladoAtual.respondidas} de ${simuladoAtual.totalQuestoes} respondidas`;
}

function iniciarCronometro(segundos) {
    pararCronometro();
    let restantes = segundos;

    const desenhar = () => {
        const min = String(Math.floor(restantes / 60)).padStart(2, '0');
        const seg = String(restantes % 60).padStart(2, '0');
        const el = $('#cronometro');
        el.textContent = `${min}:${seg}`;
        el.classList.toggle('acabando', restantes <= 60);
    };

    desenhar();
    cronometro = setInterval(() => {
        restantes--;
        if (restantes <= 0) {
            desenhar();
            pararCronometro();
            finalizarSimulado();
            return;
        }
        desenhar();
    }, 1000);
}

function pararCronometro() {
    if (cronometro) clearInterval(cronometro);
    cronometro = null;
}

$('#btn-finalizar').onclick = finalizarSimulado;

async function finalizarSimulado() {
    pararCronometro();
    try {
        const r = await api(`/simulados/${simuladoAtual.id}/finalizar`, { method: 'POST' });

        $('#simulado-andamento').classList.add('hidden');
        const alvo = $('#simulado-resultado');
        alvo.classList.remove('hidden');
        alvo.innerHTML = '';

        alvo.appendChild(criar(`
            <div class="card">
                <h2>Resultado do simulado</h2>
                <div class="numeros">
                    <div class="numero"><strong>${r.percentualAcerto}%</strong><span>aproveitamento</span></div>
                    <div class="numero"><strong>${r.acertos}</strong><span>acertos</span></div>
                    <div class="numero"><strong>${r.erros}</strong><span>erros</span></div>
                    <div class="numero"><strong>${r.naoRespondidas}</strong><span>em branco</span></div>
                    <div class="numero"><strong>${r.minutosGastos}min</strong><span>tempo gasto</span></div>
                </div>
            </div>
        `));

        const porDisciplina = criar('<div class="card"><h2>Por disciplina</h2></div>');
        r.porDisciplina.forEach(d => porDisciplina.appendChild(linhaBarra(d.disciplina, d.acertos, d.respondidas, d.percentualAcerto)));
        alvo.appendChild(porDisciplina);

        const novo = criar('<button class="primario">Fazer outro simulado</button>');
        novo.onclick = () => {
            simuladoAtual = null;
            alvo.classList.add('hidden');
            $('#simulado-config').classList.remove('hidden');
        };
        alvo.appendChild(novo);
    } catch (e) {
        $('#simulado-erro').textContent = e.message;
    }
}

// ---------- videoaulas ----------

function cardVideo(v) {
    const card = criar(`
        <div class="card-video">
            <img src="${escapar(v.thumbnail)}" alt="">
            <div class="info-video">
                <strong>${escapar(v.titulo)}</strong>
                <span class="sub">${escapar(v.disciplina)}${v.assunto ? ' · ' + escapar(v.assunto) : ''}${v.canal ? ' · ' + escapar(v.canal) : ''}${v.duracaoMinutos ? ' · ' + v.duracaoMinutos + 'min' : ''}</span>
            </div>
        </div>
    `);
    card.onclick = () => abrirVideo(v);
    return card;
}

// Abre o player do YouTube em sobreposicao, sem sair da pagina.
function abrirVideo(v) {
    $('#player').innerHTML = `<iframe width="100%" height="100%"
        src="https://www.youtube.com/embed/${escapar(v.youtubeId)}?autoplay=1"
        frameborder="0" allow="accelerometer; autoplay; encrypted-media; picture-in-picture"
        allowfullscreen></iframe>`;
    $('#titulo-video').textContent = v.titulo;
    $('#modal-video').classList.remove('hidden');
}

function fecharVideo() {
    $('#player').innerHTML = '';   // remove o iframe para parar o audio
    $('#modal-video').classList.add('hidden');
}

$('#fechar-video').onclick = fecharVideo;
$('#modal-video').onclick = e => { if (e.target.id === 'modal-video') fecharVideo(); };
document.addEventListener('keydown', e => { if (e.key === 'Escape') fecharVideo(); });

$('#btn-abrir-cadastro').onclick = () => $('#form-video').classList.toggle('hidden');

$('#btn-salvar-video').onclick = async () => {
    $('#video-erro').textContent = '';
    try {
        await api('/videoaulas', {
            method: 'POST',
            body: JSON.stringify({
                titulo: $('#v-titulo').value.trim(),
                urlOuId: $('#v-url').value.trim(),
                canal: $('#v-canal').value.trim() || null,
                duracaoMinutos: $('#v-duracao').value ? Number($('#v-duracao').value) : null,
                disciplinaId: Number($('#v-disciplina').value),
                assuntoId: $('#v-assunto').value ? Number($('#v-assunto').value) : null
            })
        });
        ['#v-titulo', '#v-url', '#v-canal', '#v-duracao'].forEach(s => $(s).value = '');
        $('#form-video').classList.add('hidden');
        carregarVideoaulas();
    } catch (e) {
        $('#video-erro').textContent = e.message;
    }
};

$('#vf-disciplina').onchange = carregarVideoaulas;

async function carregarVideoaulas() {
    const sugestoes = $('#sugestoes-video');
    const lista = $('#lista-videos');
    sugestoes.innerHTML = '';
    lista.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        const recomendados = await api('/videoaulas/sugestoes?limite=6');
        if (recomendados.length) {
            recomendados.forEach(v => sugestoes.appendChild(cardVideo(v)));
        } else {
            sugestoes.appendChild(criar('<div class="vazio">Responda algumas questões e cadastre videoaulas para receber recomendações.</div>'));
        }

        const params = new URLSearchParams();
        const disciplinaId = $('#vf-disciplina').value;
        if (disciplinaId) params.set('disciplinaId', disciplinaId);

        const videos = await api('/videoaulas?' + params);
        lista.innerHTML = '';
        if (!videos.length) {
            lista.appendChild(criar('<div class="vazio">Nenhuma videoaula cadastrada ainda.</div>'));
            return;
        }
        videos.forEach(v => lista.appendChild(cardVideo(v)));
    } catch (e) {
        lista.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}


// ---------- cadernos ----------

$('#btn-novo-caderno').onclick = () => $('#form-caderno').classList.toggle('hidden');

$('#btn-salvar-caderno').onclick = async () => {
    $('#caderno-erro').textContent = '';
    try {
        await api('/cadernos', {
            method: 'POST',
            body: JSON.stringify({ nome: $('#c-nome').value.trim() })
        });
        $('#c-nome').value = '';
        $('#form-caderno').classList.add('hidden');
        await carregarCadernos();
    } catch (e) {
        $('#caderno-erro').textContent = e.message;
    }
};

async function carregarCadernos() {
    const alvo = $('#lista-cadernos');
    $('#caderno-aberto').classList.add('hidden');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        cadernos = await api('/cadernos');
        alvo.innerHTML = '';
        if (!cadernos.length) {
            alvo.appendChild(criar('<div class="vazio">Nenhum caderno ainda. Crie um e vá salvando questões nele.</div>'));
            return;
        }

        cadernos.forEach(c => {
            const linha = criar(`
                <div class="linha-caderno">
                    <button class="link nome-caderno">${escapar(c.nome)}</button>
                    <span class="sub">${c.totalQuestoes} ${c.totalQuestoes === 1 ? 'questão' : 'questões'}</span>
                    <button class="acao excluir">excluir</button>
                </div>
            `);
            linha.querySelector('.nome-caderno').onclick = () => abrirCaderno(c.id);
            linha.querySelector('.excluir').onclick = async () => {
                if (!confirm(`Excluir o caderno "${c.nome}"?`)) return;
                await api('/cadernos/' + c.id, { method: 'DELETE' });
                carregarCadernos();
            };
            alvo.appendChild(linha);
        });
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

async function abrirCaderno(id) {
    const alvo = $('#caderno-aberto');
    alvo.classList.remove('hidden');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        const caderno = await api('/cadernos/' + id);
        alvo.innerHTML = '';
        alvo.appendChild(criar(`<div class="card"><h2>${escapar(caderno.nome)}</h2><p class="sub">${caderno.totalQuestoes} ${caderno.totalQuestoes === 1 ? 'questão' : 'questões'}</p></div>`));

        if (!caderno.questoes.length) {
            alvo.appendChild(criar('<div class="vazio">Caderno vazio. Use "+ caderno" em qualquer questão para adicionar.</div>'));
            return;
        }
        caderno.questoes.forEach(q =>
            alvo.appendChild(cardQuestao(q, responderDireto, { cadernoId: id })));
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

// ---------- marcadores ----------

async function carregarMarcadores() {
    const alvo = $('#lista-marcadores');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        const questoes = await api('/marcadores');
        marcados = new Set(questoes.map(q => q.id));
        alvo.innerHTML = '';
        if (!questoes.length) {
            alvo.appendChild(criar('<div class="vazio">Nenhuma questão marcada. Use a estrela em qualquer questão.</div>'));
            return;
        }
        questoes.forEach(q => alvo.appendChild(cardQuestao(q, responderDireto)));
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}






// ---------- objetivo de estudo ----------

const ROTULOS_ETAPA = { CONCLUIDO: 'Concluído', EM_ANDAMENTO: 'Em andamento', PREVISTO: 'Previsto' };

// Cartão "Seu objetivo atual". Retorna null quando o usuário ainda não escolheu.
async function cartaoObjetivo() {
    let o;
    try {
        o = await api('/objetivo');
    } catch {
        return null;
    }
    if (!o) return null;   // 204: sem objetivo definido

    const p = o.progresso;
    const card = criar(`
        <div class="card card-objetivo">
            <div class="topo-objetivo">
                <div>
                    <span class="rotulo">SEU OBJETIVO ATUAL</span>
                    <strong class="nome-objetivo">${escapar(o.concurso)}</strong>
                    <span class="sub">${[o.orgao, o.banca, o.ano].filter(Boolean).map(escapar).join(' · ')}</span>
                    ${o.cargo ? `<span class="sub">Cargo alvo: <strong>${escapar(o.cargo)}</strong>${o.nivel ? ' · ' + escapar(o.nivel) : ''}${o.vagas ? ' · ' + o.vagas + ' vagas' : ''}</span>` : ''}
                </div>
                <div class="progresso-objetivo">
                    <div class="topo">
                        <span class="sub">Progresso no conteúdo</span>
                        <span>${p.percentual}%</span>
                    </div>
                    <div class="barra"><div style="width:${p.percentual}%"></div></div>
                    <span class="sub">${p.topicosIniciados} de ${p.totalTopicos} tópicos iniciados</span>
                    ${o.proximaEtapa ? `<span class="sub proxima-etapa">${escapar(o.proximaEtapa.nome)}${
                        o.proximaEtapa.diasRestantes != null
                            ? (o.proximaEtapa.diasRestantes >= 0
                                ? ` em ${o.proximaEtapa.diasRestantes} dia${o.proximaEtapa.diasRestantes === 1 ? '' : 's'}`
                                : ' — data já passou')
                            : ''}</span>` : ''}
                </div>
            </div>
            <div class="contadores-objetivo">
                <span class="contador vaicair">${p.vaiCair} Vai cair</span>
                <span class="contador revisar">${p.revisar} Revisar</span>
                <span class="contador atencao">${p.atencao} Atenção</span>
                <span class="contador dominado">${p.dominado} Dominado</span>
            </div>
            <div class="acoes-objetivo">
                <button class="primario continuar">Continuar estudando →</button>
                <button class="secundario ver-concurso">Ver concurso</button>
                <button class="link trocar-objetivo">Trocar objetivo</button>
            </div>
        </div>
    `);

    card.querySelector('.continuar').onclick = () => abrir('questoes');
    card.querySelector('.ver-concurso').onclick = () => { abrir('concursos'); abrirConcurso(o.concursoId); };
    card.querySelector('.trocar-objetivo').onclick = () => abrir('concursos');
    return card;
}

// Define o concurso/cargo alvo a partir da aba Concursos.
async function definirObjetivo(concursoId, cargoId) {
    try {
        await api('/objetivo', {
            method: 'PUT',
            body: JSON.stringify({ concursoId, cargoId: cargoId || null })
        });
        abrir('inicio');
    } catch (e) {
        alert(e.message);
    }
}

// ---------- concursos e provas ----------

const SITUACOES = {
    PREVISTO: 'Previsto',
    INSCRICOES_ABERTAS: 'Inscrições abertas',
    EM_ANDAMENTO: 'Em andamento',
    ENCERRADO: 'Encerrado'
};

$('#btn-novo-concurso').onclick = async () => {
    const form = $('#form-concurso');
    form.classList.toggle('hidden');
    if (!form.classList.contains('hidden')) {
        // Preenche as bancas só ao abrir o formulário.
        try {
            const bancas = await api('/bancas');
            const sel = $('#co-banca');
            sel.innerHTML = '<option value="">Não informada</option>';
            bancas.forEach(b => sel.appendChild(criar(`<option value="${b.id}">${escapar(b.nome)}</option>`)));
        } catch { /* segue sem a lista */ }
    }
};

$('#btn-salvar-concurso').onclick = async () => {
    $('#concurso-erro').textContent = '';
    try {
        await api('/concursos', {
            method: 'POST',
            body: JSON.stringify({
                nome: $('#co-nome').value.trim(),
                orgao: $('#co-orgao').value.trim() || null,
                ano: Number($('#co-ano').value),
                bancaId: $('#co-banca').value ? Number($('#co-banca').value) : null,
                situacao: $('#co-situacao').value,
                vagas: $('#co-vagas').value ? Number($('#co-vagas').value) : null
            })
        });
        ['#co-nome', '#co-orgao', '#co-vagas'].forEach(x => $(x).value = '');
        $('#form-concurso').classList.add('hidden');
        carregarConcursos();
    } catch (e) {
        $('#concurso-erro').textContent = e.message;
    }
};

async function carregarConcursos() {
    const alvo = $('#lista-concursos');
    $('#prova-aberta').classList.add('hidden');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        const concursos = await api('/concursos');
        alvo.innerHTML = '';
        if (!concursos.length) {
            alvo.appendChild(criar('<div class="vazio">Nenhum concurso cadastrado ainda.</div>'));
            return;
        }

        concursos.forEach(c => {
            const totalVagas = c.cargos.reduce((s, cg) => s + (cg.vagas || 0), 0) || c.vagas;
            const linha = criar(`
                <div class="linha-concurso">
                    <div class="info-concurso">
                        <strong>${escapar(c.nome)}</strong>
                        <span class="sub">${[c.orgao, c.banca, c.ano].filter(Boolean).map(escapar).join(' · ')}${totalVagas ? ' · ' + totalVagas + ' vagas' : ''}${c.cargos.length ? ' · ' + c.cargos.length + (c.cargos.length === 1 ? ' cargo' : ' cargos') : ''}</span>
                    </div>
                    <span class="etiqueta-situacao ${c.situacao.toLowerCase()}">${SITUACOES[c.situacao] || c.situacao}</span>
                    <button class="acao detalhar">ver detalhes</button>
                </div>
            `);
            linha.querySelector('.detalhar').onclick = () => abrirConcurso(c.id);
            alvo.appendChild(linha);
        });
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

// Página do concurso: cronograma, cargos, conteúdo programático e provas.
async function abrirConcurso(id) {
    const alvo = $('#prova-aberta');
    alvo.classList.remove('hidden');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        const c = await api('/concursos/' + id);
        alvo.innerHTML = '';

        const totalVagas = c.cargos.reduce((s, cg) => s + (cg.vagas || 0), 0) || c.vagas;

        const cabecalho = criar(`
            <div class="card cabecalho-concurso">
                <span class="etiqueta-situacao ${c.situacao.toLowerCase()}">${SITUACOES[c.situacao] || c.situacao}</span>
                <h2>${escapar(c.nome)}</h2>
                <p class="sub">${[c.orgao, c.banca, c.ano].filter(Boolean).map(escapar).join(' · ')}</p>
                <div class="fichas-concurso">
                    ${ficha('Vagas', totalVagas ?? '—')}
                    ${ficha('Cargos', c.cargos.length || '—')}
                    ${ficha('Inscrições até', c.inscricoesAte ? new Date(c.inscricoesAte + 'T00:00:00').toLocaleDateString('pt-BR') : '—')}
                    ${ficha('Taxa', c.taxa ? 'R$ ' + Number(c.taxa).toFixed(2).replace('.', ',') : '—')}
                </div>
            </div>
        `);
        const voltar = criar('<button class="link">← Voltar para a lista</button>');
        voltar.onclick = () => alvo.classList.add('hidden');
        cabecalho.appendChild(voltar);
        alvo.appendChild(cabecalho);

        // Cronograma
        if (c.etapas.length) {
            const card = criar('<div class="card"><h2 class="titulo-icone"><span class="icone-titulo">◷</span> Cronograma</h2></div>');
            const trilha = criar('<div class="trilha-etapas"></div>');
            c.etapas.forEach(e => trilha.appendChild(criar(`
                <div class="etapa ${e.status.toLowerCase()}">
                    <span class="marca-etapa"></span>
                    <div>
                        <strong>${escapar(e.nome)}</strong>
                        <span class="sub">${e.dataPrevista ? new Date(e.dataPrevista + 'T00:00:00').toLocaleDateString('pt-BR') : 'sem data'} · ${ROTULOS_ETAPA[e.status] || e.status}${
                            e.diasRestantes != null && e.status !== 'CONCLUIDO'
                                ? (e.diasRestantes >= 0 ? ` · faltam ${e.diasRestantes} dias` : ' · data passou')
                                : ''}</span>
                    </div>
                </div>
            `)));
            card.appendChild(trilha);
            alvo.appendChild(card);
        }

        // Cargos e conteúdo programático
        if (c.cargos.length) {
            const card = criar('<div class="card"><h2 class="titulo-icone"><span class="icone-titulo">▤</span> Cargos e conteúdo programático</h2></div>');
            c.cargos.forEach(cg => {
                const bloco = criar(`
                    <div class="bloco-cargo">
                        <div class="topo-cargo">
                            <div>
                                <strong>${escapar(cg.nome)}</strong>
                                <span class="sub">${[cg.nivel, cg.vagas ? cg.vagas + ' vagas' : null,
                                    cg.cadastroReserva ? cg.cadastroReserva + ' CR' : null,
                                    cg.salario ? 'R$ ' + Number(cg.salario).toLocaleString('pt-BR', { minimumFractionDigits: 2 }) : null]
                                    .filter(Boolean).map(escapar).join(' · ')}</span>
                            </div>
                            <button class="acao definir">definir como objetivo</button>
                        </div>
                        <div class="conteudo-cargo"></div>
                    </div>
                `);
                bloco.querySelector('.definir').onclick = () => definirObjetivo(c.id, cg.id);

                const lista = bloco.querySelector('.conteudo-cargo');
                if (cg.conteudo.length) {
                    cg.conteudo.forEach(d => lista.appendChild(criar(
                        `<span class="chip-disciplina">${escapar(d.disciplina)}${d.totalTopicos ? ` <span class="qtd">${d.totalTopicos}</span>` : ''}</span>`
                    )));
                } else {
                    lista.appendChild(criar('<span class="sub">Conteúdo programático não cadastrado.</span>'));
                }
                card.appendChild(bloco);
            });
            alvo.appendChild(card);
        }

        // Provas
        if (c.provas.length) {
            const card = criar('<div class="card"><h2 class="titulo-icone"><span class="icone-titulo">✎</span> Provas anteriores</h2></div>');
            c.provas.forEach(p => {
                const item = criar(`
                    <button class="item-prova">
                        <span>${escapar(p.cargo)}${p.nivel ? ' · ' + escapar(p.nivel) : ''}</span>
                        <span class="sub">${p.totalQuestoes} ${p.totalQuestoes === 1 ? 'questão' : 'questões'} →</span>
                    </button>
                `);
                item.onclick = () => abrirProva(p.id);
                card.appendChild(item);
            });
            alvo.appendChild(card);
        }

        if (c.observacoes) {
            alvo.appendChild(criar(`<div class="card"><h2>Observações</h2><p class="sub">${escapar(c.observacoes)}</p></div>`));
        }
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

function ficha(rotulo, valor) {
    return `<div class="ficha"><span class="rotulo">${rotulo}</span><strong>${valor}</strong></div>`;
}

async function abrirProva(id) {
    const alvo = $('#prova-aberta');
    alvo.classList.remove('hidden');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        const prova = await api('/provas/' + id);
        alvo.innerHTML = '';

        const cabecalho = criar(`
            <div class="card">
                <h2>${escapar(prova.cargo)}</h2>
                <p class="sub">${[prova.concurso, prova.orgao, prova.banca, prova.ano].filter(Boolean).map(escapar).join(' · ')} · ${prova.totalQuestoes} questões</p>
            </div>
        `);
        const voltar = criar('<button class="link">← Voltar</button>');
        voltar.onclick = () => carregarConcursos();
        cabecalho.appendChild(voltar);
        alvo.appendChild(cabecalho);

        prova.questoes.forEach(item => {
            const card = cardQuestao(item.questao, responderDireto);
            card.querySelector('.cabecalho-questao').prepend(
                criar(`<span class="numero-prova">${item.numero}</span>`));
            alvo.appendChild(card);
        });
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

// ---------- estatísticas coletivas e comentários ----------

// Mostrado depois que o usuário responde: como os outros se saíram.
async function mostrarEstatisticasQuestao(questaoId, area) {
    try {
        const e = await api(`/questoes/${questaoId}/estatisticas`);
        if (e.totalRespostas === 0) return;

        const bloco = criar(`
            <div class="painel-comunidade">
                <div class="topo-comunidade">
                    <span class="rotulo">COMO A COMUNIDADE FOI</span>
                    <span class="etiqueta-dificuldade">${escapar(e.dificuldade)}</span>
                </div>
                <p class="sub">${e.percentualAcerto}% de acerto em ${e.totalRespostas} ${e.totalRespostas === 1 ? 'resposta' : 'respostas'}.</p>
            </div>
        `);

        e.distribuicao.forEach(f => bloco.appendChild(criar(`
            <div class="fatia-alternativa">
                <span class="letra ${f.correta ? 'certa' : ''}">${f.letra}</span>
                <span class="mini-barra"><span class="${f.correta ? 'ok' : 'nok'}" style="width:${f.percentual}%"></span></span>
                <span class="pct">${f.percentual}%</span>
            </div>
        `)));

        area.appendChild(bloco);
    } catch { /* estatística é complemento: falhar aqui não atrapalha a resposta */ }
}

// Caixa de comentários, carregada sob demanda.
async function montarComentarios(questaoId, area) {
    const painel = criar(`
        <div class="painel-comentarios">
            <button class="link abrir-comentarios">Ver comentários</button>
            <div class="conteudo-comentarios hidden"></div>
        </div>
    `);
    const botao = painel.querySelector('.abrir-comentarios');
    const conteudo = painel.querySelector('.conteudo-comentarios');
    let carregado = false;

    botao.onclick = async () => {
        conteudo.classList.toggle('hidden');
        botao.textContent = conteudo.classList.contains('hidden') ? 'Ver comentários' : 'Ocultar comentários';
        if (carregado) return;
        carregado = true;
        await recarregarComentarios(questaoId, conteudo);
    };

    area.appendChild(painel);
}

async function recarregarComentarios(questaoId, conteudo) {
    conteudo.innerHTML = '<div class="sub">Carregando…</div>';
    try {
        const lista = await api(`/questoes/${questaoId}/comentarios`);
        conteudo.innerHTML = '';

        const form = criar(`
            <div class="form-comentario">
                <textarea placeholder="Escreva um comentário sobre esta questão…" rows="3"></textarea>
                <button class="acao enviar">Comentar</button>
            </div>
        `);
        const campo = form.querySelector('textarea');
        form.querySelector('.enviar').onclick = async () => {
            const texto = campo.value.trim();
            if (!texto) return;
            try {
                await api(`/questoes/${questaoId}/comentarios`, {
                    method: 'POST',
                    body: JSON.stringify({ texto })
                });
                campo.value = '';
                recarregarComentarios(questaoId, conteudo);
            } catch (e) { alert(e.message); }
        };
        conteudo.appendChild(form);

        if (!lista.length) {
            conteudo.appendChild(criar('<div class="sub">Nenhum comentário ainda. Seja o primeiro.</div>'));
            return;
        }

        lista.forEach(c => {
            const item = criar(`
                <div class="comentario">
                    <div class="topo">
                        <strong>${escapar(c.autor)}</strong>
                        <span class="sub">${new Date(c.criadoEm).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })}</span>
                    </div>
                    <p>${escapar(c.texto)}</p>
                </div>
            `);
            if (c.meu) {
                const apagar = criar('<button class="acao excluir">apagar</button>');
                apagar.onclick = async () => {
                    await api('/comentarios/' + c.id, { method: 'DELETE' });
                    recarregarComentarios(questaoId, conteudo);
                };
                item.querySelector('.topo').appendChild(apagar);
            }
            conteudo.appendChild(item);
        });
    } catch (e) {
        conteudo.innerHTML = `<div class="sub">${escapar(e.message)}</div>`;
    }
}

// ---------- página inicial ----------

// Números da plataforma. Todos vêm de endpoints que já existem: pegamos o
// total de questões do cabeçalho da paginação e contamos o catálogo.
async function numerosDaPlataforma() {
    const [pagina, bancas, disciplinas] = await Promise.all([
        api('/questoes?size=1'),
        api('/bancas').catch(() => []),
        api('/disciplinas').catch(() => [])
    ]);

    // Com a serialização VIA_DTO os metadados vêm dentro de "page";
    // o fallback cobre o formato antigo.
    const totalQuestoes = pagina.page?.totalElements ?? pagina.totalElements ?? 0;

    const contarAssuntos = lista =>
        lista.reduce((soma, a) => soma + 1 + contarAssuntos(a.subassuntos || []), 0);
    const totalAssuntos = disciplinas.reduce((s, d) => s + contarAssuntos(d.assuntos || []), 0);

    return {
        questoes: totalQuestoes,
        bancas: bancas.length,
        disciplinas: disciplinas.length,
        assuntos: totalAssuntos
    };
}

// Uma questão de exemplo, sorteada entre as primeiras — sem gabarito.
async function questaoDestaque() {
    try {
        const pagina = await api('/questoes?size=10');
        const lista = pagina.content || [];
        if (!lista.length) return null;
        return lista[Math.floor(Math.random() * lista.length)];
    } catch {
        return null;
    }
}

function recorte(texto, limite) {
    const t = String(texto || '');
    return t.length > limite ? t.slice(0, limite).trimEnd() + '…' : t;
}

async function carregarInicio() {
    const alvo = $('#painel-inicio');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        const nome = primeiroNome(perfilUsuario?.nome);
        const [numeros, destaque, eng] = await Promise.all([
            numerosDaPlataforma(),
            questaoDestaque(),
            api('/engajamento').catch(() => null)
        ]);

        alvo.innerHTML = '';

        const hero = criar(`
            <div class="hero">
                <div class="selos-hero">
                    <span class="selo-hero">${numeros.questoes.toLocaleString('pt-BR')} questões disponíveis</span>
                    ${eng ? `<span class="selo-hero dourado">${eng.ofensivaAtual} ${eng.ofensivaAtual === 1 ? 'dia' : 'dias'} de ofensiva</span>` : ''}
                </div>
                <h1 class="titulo-hero">${saudacao()}, <span class="destaque-nome">${escapar(nome)}.</span></h1>
                <p class="sub-hero">Continue de onde parou. Suas questões, simulados e cadernos estão esperando.</p>
                <div class="acoes-hero">
                    <button id="ir-questoes" class="primario">Resolver questões</button>
                    <button id="ir-simulado" class="secundario">Criar simulado</button>
                </div>
            </div>
        `);
        const objetivo = await cartaoObjetivo();
        if (objetivo) alvo.appendChild(objetivo);

        alvo.appendChild(hero);

        hero.querySelector('#ir-questoes').onclick = () => abrir('questoes');
        hero.querySelector('#ir-simulado').onclick = () => abrir('simulado');

        // Prévia de questão: mostra o formato sem revelar resposta.
        if (destaque) {
            const previa = criar(`
                <div class="previa-questao">
                    <div class="barra-janela">
                        <span class="bolinha"></span><span class="bolinha"></span><span class="bolinha"></span>
                        <span class="rotulo-janela">QUESTÃO #${destaque.id}</span>
                    </div>
                    <div class="corpo-previa">
                        <span class="trilha-previa">${escapar(destaque.disciplina)}${destaque.assunto ? ' · ' + escapar(destaque.assunto) : ''}</span>
                        <p class="enunciado-previa">(${escapar(destaque.banca)} — ${destaque.ano}) ${escapar(recorte(destaque.enunciado, 180))}</p>
                        <div class="alternativas-previa"></div>
                        <button class="link ver-questao">Resolver esta questão →</button>
                    </div>
                </div>
            `);
            const lista = previa.querySelector('.alternativas-previa');
            destaque.alternativas.slice(0, 3).forEach((alt, i) => lista.appendChild(criar(
                `<div class="alternativa-previa"><span class="marcador"></span>${String.fromCharCode(65 + i)}) ${escapar(recorte(alt.texto, 70))}</div>`
            )));
            previa.querySelector('.ver-questao').onclick = () => abrir('questoes');
            alvo.appendChild(previa);
        }

        // Números da plataforma
        const numerosBloco = criar('<div class="numeros-hero"></div>');
        [
            { valor: numeros.questoes.toLocaleString('pt-BR'), rotulo: 'Questões' },
            { valor: numeros.bancas, rotulo: 'Bancas' },
            { valor: numeros.disciplinas, rotulo: 'Disciplinas' },
            { valor: numeros.assuntos, rotulo: 'Assuntos' }
        ].forEach(n => numerosBloco.appendChild(criar(`
            <div class="numero-hero">
                <strong>${n.valor}</strong>
                <span>${n.rotulo}</span>
            </div>
        `)));
        alvo.appendChild(numerosBloco);

        // Atalhos para as áreas principais
        const atalhos = criar('<div class="grade-atalhos"></div>');
        [
            { tela: 'revisao', icone: '↻', titulo: 'Revisão de hoje', texto: 'Questões que voltam no intervalo certo.' },
            { tela: 'erradas', icone: '✕', titulo: 'Revisar erradas', texto: 'O que você errou na última tentativa.' },
            { tela: 'cadernos', icone: '▤', titulo: 'Seus cadernos', texto: 'Listas que você montou para estudar.' },
            { tela: 'videoaulas', icone: '▶', titulo: 'Videoaulas', texto: 'Recomendadas pelos seus pontos fracos.' },
            { tela: 'concursos', icone: '◷', titulo: 'Concursos e provas', texto: 'Resolva provas inteiras, na ordem original.' },
            { tela: 'dashboard', icone: '◈', titulo: 'Seu desempenho', texto: 'Acertos, evolução e pontos fracos.' }
        ].forEach(a => {
            const card = criar(`
                <button class="card-atalho">
                    <span class="icone-atalho">${a.icone}</span>
                    <strong>${a.titulo}</strong>
                    <span class="sub">${a.texto}</span>
                </button>
            `);
            card.onclick = () => abrir(a.tela);
            atalhos.appendChild(card);
        });
        alvo.appendChild(atalhos);

        // --- Tudo que você precisa ---
        const recursos = criar(`
            <div class="secao-inicio">
                <h2 class="titulo-secao">Tudo que você precisa para estudar melhor</h2>
                <p class="sub centralizado">Ferramentas pensadas para quem estuda para concurso.</p>
                <div class="grade-recursos"></div>
            </div>
        `);
        const gradeRecursos = recursos.querySelector('.grade-recursos');
        [
            { icone: '◈', titulo: 'Análise de desempenho', texto: 'Acertos por disciplina, por assunto e ao longo do tempo.' },
            { icone: '▤', titulo: 'Banco de questões', texto: 'Filtros combináveis por disciplina, assunto, banca e ano.' },
            { icone: '◷', titulo: 'Simulado cronometrado', texto: 'Monte a prova, controle o tempo e veja o resultado no fim.' },
            { icone: '↻', titulo: 'Revisão espaçada', texto: 'Cada questão volta no intervalo certo para você fixar.' },
            { icone: '▶', titulo: 'Videoaulas dirigidas', texto: 'Recomendadas a partir dos assuntos em que você mais erra.' },
            { icone: '★', titulo: 'Cadernos e marcadores', texto: 'Organize as questões do seu jeito e retome quando quiser.' }
        ].forEach(r => gradeRecursos.appendChild(criar(`
            <div class="card-recurso">
                <span class="icone-atalho">${r.icone}</span>
                <strong>${r.titulo}</strong>
                <span class="sub">${r.texto}</span>
            </div>
        `)));
        alvo.appendChild(recursos);

        // --- Como funciona ---
        const passos = criar(`
            <div class="secao-inicio">
                <h2 class="titulo-secao">Como funciona</h2>
                <div class="grade-passos"></div>
            </div>
        `);
        const gradePassos = passos.querySelector('.grade-passos');
        [
            { n: '01', titulo: 'Defina sua meta', texto: 'Escolha quantas questões quer resolver por dia.' },
            { n: '02', titulo: 'Resolva questões', texto: 'Filtre por assunto e receba a correção na hora.' },
            { n: '03', titulo: 'Revise no tempo certo', texto: 'O que você erra volta; o que domina se afasta.' },
            { n: '04', titulo: 'Acompanhe a evolução', texto: 'Veja onde melhorou e o que ainda está fraco.' }
        ].forEach(p => gradePassos.appendChild(criar(`
            <div class="passo">
                <span class="numero-passo">${p.n}</span>
                <strong>${p.titulo}</strong>
                <span class="sub">${p.texto}</span>
            </div>
        `)));
        alvo.appendChild(passos);

        // --- Chamada final ---
        const chamada = criar(`
            <div class="chamada-final">
                <h2>Continue estudando agora.</h2>
                <p>Suas questões, simulados e revisões estão esperando.</p>
                <button class="primario">Resolver questões</button>
            </div>
        `);
        chamada.querySelector('button').onclick = () => abrir('questoes');
        alvo.appendChild(chamada);

        alvo.appendChild(criar(`
            <footer class="rodape-inicio">
                <span>Concursos Platform</span>
                <span class="sub">Projeto pessoal em desenvolvimento · ${new Date().getFullYear()}</span>
            </footer>
        `));
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

// ---------- revisão espaçada ----------

// Selo com a quantidade pendente, no menu.
async function atualizarSeloRevisao() {
    try {
        const r = await api('/revisoes/resumo');
        const selo = $('#selo-revisao');
        selo.textContent = r.paraHoje;
        selo.classList.toggle('hidden', r.paraHoje === 0);
    } catch { /* silencioso: o selo é secundário */ }
}

async function carregarRevisao() {
    const agenda = $('#agenda-revisao');
    const alvo = $('#lista-revisao');
    agenda.innerHTML = '';
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        const [resumo, questoes] = await Promise.all([
            api('/revisoes/resumo'),
            api('/revisoes/hoje')
        ]);

        if (resumo.proximosDias.length) {
            const linha = criar('<div class="agenda-semana"></div>');
            resumo.proximosDias.forEach(d => linha.appendChild(criar(`
                <div class="dia-agenda">
                    <strong>${d.total}</strong>
                    <span>${new Date(d.dia + 'T00:00:00').toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' })}</span>
                </div>
            `)));
            agenda.appendChild(criar('<p class="sub">Próximos dias:</p>'));
            agenda.appendChild(linha);
        }

        alvo.innerHTML = '';
        if (!questoes.length) {
            alvo.appendChild(criar('<div class="vazio">Nada para revisar hoje. Responda questões e elas voltarão no momento certo.</div>'));
            return;
        }
        questoes.forEach(q => alvo.appendChild(cardQuestao(q, async (questao, alt, card) => {
            await responderDireto(questao, alt, card);
            atualizarSeloRevisao();
        })));
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

// ---------- componentes de gráfico (SVG puro, sem biblioteca) ----------

// Rosca. O truque é stroke-dasharray: desenhamos o arco como um traço
// cujo comprimento é a fatia que queremos, e o resto fica vazado.
function rosca(fatias, centroTopo, centroBase, tamanho = 150) {
    const raio = 54;
    const circunferencia = 2 * Math.PI * raio;
    const total = fatias.reduce((s, f) => s + f.valor, 0);

    let offset = 0;
    const arcos = fatias.filter(f => f.valor > 0).map(f => {
        const comprimento = total ? (f.valor / total) * circunferencia : 0;
        const arco = `<circle cx="70" cy="70" r="${raio}" fill="none"
            stroke="${f.cor}" stroke-width="16" stroke-linecap="butt"
            stroke-dasharray="${comprimento} ${circunferencia - comprimento}"
            stroke-dashoffset="${-offset}"
            transform="rotate(-90 70 70)"></circle>`;
        offset += comprimento;
        return arco;
    }).join('');

    const trilho = total === 0
        ? `<circle cx="70" cy="70" r="${raio}" fill="none" stroke="var(--trilho)" stroke-width="16"></circle>`
        : '';

    return `
        <svg viewBox="0 0 140 140" width="${tamanho}" height="${tamanho}" class="rosca">
            ${trilho}${arcos}
            <text x="70" y="66" text-anchor="middle" class="rosca-valor">${centroTopo}</text>
            <text x="70" y="86" text-anchor="middle" class="rosca-rotulo">${centroBase}</text>
        </svg>`;
}

// Anel de progresso pequeno, usado na meta diária.
function anelProgresso(percentual, tamanho = 46) {
    const raio = 20;
    const circunferencia = 2 * Math.PI * raio;
    const preenchido = Math.min(100, percentual) / 100 * circunferencia;
    return `
        <svg viewBox="0 0 50 50" width="${tamanho}" height="${tamanho}">
            <circle cx="25" cy="25" r="${raio}" fill="none" stroke="var(--trilho)" stroke-width="5"></circle>
            <circle cx="25" cy="25" r="${raio}" fill="none" stroke="var(--primario)" stroke-width="5"
                stroke-linecap="round"
                stroke-dasharray="${preenchido} ${circunferencia - preenchido}"
                transform="rotate(-90 25 25)"></circle>
            <text x="25" y="29" text-anchor="middle" class="anel-texto">${Math.round(percentual)}%</text>
        </svg>`;
}

// Gráfico de linha da evolução. Eixo Y é o percentual de acerto (0 a 100).
function graficoLinha(pontos) {
    if (!pontos.length) return '<div class="vazio">Sem dados ainda.</div>';

    const largura = 620, altura = 200, margemX = 34, margemY = 20;
    const areaL = largura - margemX * 2;
    const areaA = altura - margemY * 2;

    const x = i => margemX + (pontos.length === 1 ? areaL / 2 : (i / (pontos.length - 1)) * areaL);
    const y = v => margemY + areaA - (v / 100) * areaA;

    const linha = pontos.map((p, i) => `${x(i)},${y(p.percentualAcerto)}`).join(' ');
    const area = `${margemX},${margemY + areaA} ${linha} ${x(pontos.length - 1)},${margemY + areaA}`;

    const grade = [0, 25, 50, 75, 100].map(v => `
        <line x1="${margemX}" y1="${y(v)}" x2="${largura - margemX}" y2="${y(v)}" class="grade"></line>
        <text x="${margemX - 8}" y="${y(v) + 4}" text-anchor="end" class="eixo">${v}</text>
    `).join('');

    const bolinhas = pontos.map((p, i) => `
        <circle cx="${x(i)}" cy="${y(p.percentualAcerto)}" r="4" class="ponto">
            <title>${new Date(p.dia + 'T00:00:00').toLocaleDateString('pt-BR')}: ${p.percentualAcerto}% (${p.acertos}/${p.respondidas})</title>
        </circle>`).join('');

    // Mostra no máximo 6 rótulos de data para não embolar.
    const passo = Math.max(1, Math.ceil(pontos.length / 6));
    const datas = pontos.map((p, i) => i % passo === 0 || i === pontos.length - 1
        ? `<text x="${x(i)}" y="${altura - 2}" text-anchor="middle" class="eixo">${new Date(p.dia + 'T00:00:00').toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' })}</text>`
        : '').join('');

    return `
        <svg viewBox="0 0 ${largura} ${altura}" class="gr-linha">
            ${grade}
            <polygon points="${area}" class="area-linha"></polygon>
            <polyline points="${linha}" class="traco"></polyline>
            ${bolinhas}${datas}
        </svg>`;
}


// Radar de competências: um eixo por disciplina, distância do centro = % de acerto.
// Precisa de ao menos 3 eixos para formar um polígono legível.
function radar(itens, tamanho = 260) {
    if (itens.length < 3) return null;

    const centro = tamanho / 2;
    const raio = centro - 42;
    const passo = (2 * Math.PI) / itens.length;

    // -90° para o primeiro eixo apontar para cima.
    const ponto = (indice, proporcao) => {
        const angulo = indice * passo - Math.PI / 2;
        return [
            centro + Math.cos(angulo) * raio * proporcao,
            centro + Math.sin(angulo) * raio * proporcao
        ];
    };

    // Teias de fundo em 25%, 50%, 75% e 100%.
    const teias = [0.25, 0.5, 0.75, 1].map(p => {
        const pontos = itens.map((_, i) => ponto(i, p).join(',')).join(' ');
        return `<polygon points="${pontos}" class="teia"></polygon>`;
    }).join('');

    const eixos = itens.map((_, i) => {
        const [x, y] = ponto(i, 1);
        return `<line x1="${centro}" y1="${centro}" x2="${x}" y2="${y}" class="eixo-radar"></line>`;
    }).join('');

    const area = itens.map((it, i) => ponto(i, Math.max(it.valor, 0) / 100).join(',')).join(' ');

    const marcas = itens.map((it, i) => {
        const [x, y] = ponto(i, Math.max(it.valor, 0) / 100);
        return `<circle cx="${x}" cy="${y}" r="3.5" class="marca-radar"><title>${escapar(it.rotulo)}: ${it.valor}%</title></circle>`;
    }).join('');

    const rotulos = itens.map((it, i) => {
        const [x, y] = ponto(i, 1.18);
        const ancora = Math.abs(x - centro) < 12 ? 'middle' : (x > centro ? 'start' : 'end');
        const nome = it.rotulo.length > 16 ? it.rotulo.slice(0, 15) + '…' : it.rotulo;
        return `<text x="${x}" y="${y + 4}" text-anchor="${ancora}" class="rotulo-radar">${escapar(nome)}</text>`;
    }).join('');

    return `<svg viewBox="0 0 ${tamanho} ${tamanho}" class="radar">
        ${teias}${eixos}
        <polygon points="${area}" class="area-radar"></polygon>
        ${marcas}${rotulos}
    </svg>`;
}

// ---------- dashboard ----------

function linhaBarra(rotulo, acertos, total, percentual, sufixo = '') {
    return criar(`
        <div class="linha-disciplina">
            <div class="topo">
                <span>${escapar(rotulo)}${sufixo ? ' <span class="sub">' + escapar(sufixo) + '</span>' : ''}</span>
                <span>${acertos}/${total} · ${percentual}%</span>
            </div>
            <div class="barra"><div style="width:${percentual}%"></div></div>
        </div>
    `);
}

// Bom dia até 12h, boa tarde até 18h, boa noite depois disso.
function saudacao() {
    const h = new Date().getHours();
    if (h < 12) return 'Bom dia';
    if (h < 18) return 'Boa tarde';
    return 'Boa noite';
}

// Primeiro nome, para a saudação não ficar longa demais.
function primeiroNome(nome) {
    return String(nome || '').trim().split(/\s+/)[0] || 'estudante';
}

async function carregarDashboard() {
    const alvo = $('#painel-dashboard');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    try {
        // Busca tudo em paralelo: a tela só depende do conjunto completo.
        const [perfil, eng, stats, assuntos, serie, mapaDias, videos] = await Promise.all([
            perfilUsuario ? Promise.resolve(perfilUsuario) : api('/perfil'),
            api('/engajamento'),
            api('/estatisticas'),
            api('/estatisticas/assuntos'),
            api('/estatisticas/evolucao?dias=30'),
            api('/engajamento/mapa?dias=182'),
            api('/videoaulas/sugestoes?limite=3').catch(() => [])
        ]);

        alvo.innerHTML = '';
        alvo.appendChild(criar(`<h1 class="saudacao">${saudacao()}, ${escapar(primeiroNome(perfil.nome))}!</h1>`));

        const grade = criar('<div class="grade-painel"></div>');
        const principal = criar('<div class="coluna-principal"></div>');
        const lateral = criar('<div class="coluna-lateral"></div>');

        // --- meta diária ---
        const pctMeta = eng.metaDiaria ? (eng.respondidasHoje / eng.metaDiaria) * 100 : 0;
        const faltam = Math.max(0, eng.metaDiaria - eng.respondidasHoje);
        const metaCard = criar(`
            <div class="card card-meta vertical">
                ${anelProgresso(pctMeta, 64)}
                <div class="meta-info">
                    <span class="rotulo">META DIÁRIA</span>
                    <div class="meta-numeros">
                        <strong>${eng.respondidasHoje}</strong> / 
                        <input id="input-meta" type="number" min="1" max="500" value="${eng.metaDiaria}">
                        <span class="sub">questões</span>
                    </div>
                    <span class="sub">${faltam === 0
                        ? 'Meta batida hoje. Bom trabalho.'
                        : `Faltam ${faltam} ${faltam === 1 ? 'questão' : 'questões'} para bater a meta`}</span>
                </div>
                <button id="btn-resolver" class="primario">Resolver →</button>
            </div>
        `);
        lateral.appendChild(metaCard);   // constância fica agrupada na lateral

        // Salvar a meta ao sair do campo evita um botão extra na interface.
        metaCard.querySelector('#input-meta').onchange = async (ev) => {
            try {
                await api('/engajamento/meta', {
                    method: 'PUT',
                    body: JSON.stringify({ questoesPorDia: Number(ev.target.value) })
                });
                carregarDashboard();
            } catch (err) { alert(err.message); }
        };
        metaCard.querySelector('#btn-resolver').onclick = () => abrir('questoes');

        // --- quatro indicadores ---
        const indicadores = [
            { rotulo: 'RESOLVIDAS', valor: stats.totalRespondidas, icone: '📘', cor: 'roxo' },
            { rotulo: 'DISCIPLINAS', valor: stats.porDisciplina.length, icone: '🎓', cor: 'roxo' },
            { rotulo: 'ACERTOS', valor: stats.totalAcertos, icone: '✓', cor: 'verde' },
            { rotulo: 'ERROS', valor: stats.totalErros, icone: '✕', cor: 'vermelho' }
        ];
        const linhaIndicadores = criar('<div class="grade-indicadores"></div>');
        indicadores.forEach(i => linhaIndicadores.appendChild(criar(`
            <div class="card card-indicador">
                <div class="topo-indicador">
                    <span class="rotulo">${i.rotulo}</span>
                    <span class="icone ${i.cor}">${i.icone}</span>
                </div>
                <strong>${i.valor}</strong>
            </div>
        `)));
        principal.appendChild(linhaIndicadores);

        // Objetivo logo abaixo dos números: primeiro o resultado, depois o alvo.
        const cardObjetivo = await cartaoObjetivo();
        if (cardObjetivo) principal.appendChild(cardObjetivo);

        // --- roscas: acertos e disciplinas ---
        const duplaRoscas = criar('<div class="grade-dupla"></div>');

        duplaRoscas.appendChild(criar(`
            <div class="card">
                <h2 class="titulo-icone"><span class="icone-titulo">%</span> Acertos</h2>
                <div class="centro-rosca">
                    ${rosca(
                        [{ valor: stats.totalAcertos, cor: 'var(--acerto)' },
                         { valor: stats.totalErros, cor: 'var(--erro)' }],
                        stats.percentualAcertoGeral + '%', 'ACERTO'
                    )}
                </div>
                <p class="sub centralizado">Sua taxa de acerto em ${stats.totalRespondidas} ${stats.totalRespondidas === 1 ? 'questão respondida' : 'questões respondidas'}.</p>
                <div class="etiquetas">
                    <span class="etiqueta verde">✓ ${stats.totalAcertos} certas</span>
                    <span class="etiqueta vermelha">✕ ${stats.totalErros} erradas</span>
                </div>
            </div>
        `));

        const cardDisciplinas = criar(`
            <div class="card">
                <h2 class="titulo-icone"><span class="icone-titulo">◷</span> Desempenho por disciplina</h2>
                <div class="centro-rosca">
                    ${rosca(
                        stats.porDisciplina.map((d, i) => ({ valor: d.respondidas, cor: paleta(i) })),
                        stats.porDisciplina.length, 'DISCIPLINAS'
                    )}
                </div>
                <div class="lista-disciplinas"></div>
            </div>
        `);
        const listaDisc = cardDisciplinas.querySelector('.lista-disciplinas');
        stats.porDisciplina.forEach((d, i) => listaDisc.appendChild(criar(`
            <div class="item-disciplina">
                <span class="ponto-cor" style="background:${paleta(i)}"></span>
                <span class="nome">${escapar(d.disciplina)}</span>
                <span class="mini-barra"><span style="width:${d.percentualAcerto}%"></span></span>
                <span class="qtd">${d.respondidas}</span>
            </div>
        `)));
        duplaRoscas.appendChild(cardDisciplinas);
        principal.appendChild(duplaRoscas);

        // Radar por assunto quando há variedade; senão, por disciplina.
        const baseRadar = assuntos.length >= 3
            ? assuntos.slice(0, 8).map(a => ({ rotulo: a.assunto, valor: a.percentualAcerto }))
            : stats.porDisciplina.map(d => ({ rotulo: d.disciplina, valor: d.percentualAcerto }));

        const svgRadar = radar(baseRadar);
        if (svgRadar) {
            principal.appendChild(criar(`
                <div class="card">
                    <h2 class="titulo-icone"><span class="icone-titulo">◈</span> Radar de competências</h2>
                    <p class="sub">Taxa de acerto por ${assuntos.length >= 3 ? 'assunto' : 'disciplina'}. Quanto mais para fora, melhor.</p>
                    <div class="centro-rosca">${svgRadar}</div>
                </div>
            `));
        }

        // --- evolução ---
        principal.appendChild(criar(`
            <div class="card">
                <h2 class="titulo-icone"><span class="icone-titulo">↗</span> Sua evolução</h2>
                <p class="sub">Taxa de acerto por dia, nos últimos 30 dias.</p>
                ${graficoLinha(serie)}
            </div>
        `));

        // --- mapa de estudo ---
        const cardMapa = criar(`
            <div class="card">
                <h2 class="titulo-icone"><span class="icone-titulo">▦</span> Mapa de estudo</h2>
                <p class="sub">Últimos 6 meses. Quanto mais escuro, mais questões naquele dia.</p>
            </div>
        `);
        const gradeMapa = criar('<div class="mapa-estudo"></div>');
        if (mapaDias.length) {
            const primeiro = new Date(mapaDias[0].dia + 'T00:00:00').getDay();
            for (let i = 0; i < primeiro; i++) gradeMapa.appendChild(criar('<div class="quadro vazio-quadro"></div>'));
        }
        mapaDias.forEach(d => {
            const data = new Date(d.dia + 'T00:00:00').toLocaleDateString('pt-BR');
            gradeMapa.appendChild(criar(`<div class="quadro nivel-${d.nivel}" title="${data}: ${d.respondidas} ${d.respondidas === 1 ? 'questão' : 'questões'}"></div>`));
        });
        cardMapa.appendChild(gradeMapa);
        cardMapa.appendChild(criar(`
            <div class="legenda-mapa">
                <span class="sub">menos</span>
                <div class="quadro nivel-0"></div><div class="quadro nivel-1"></div>
                <div class="quadro nivel-2"></div><div class="quadro nivel-3"></div>
                <div class="quadro nivel-4"></div>
                <span class="sub">mais</span>
            </div>
        `));
        principal.appendChild(cardMapa);

        // --- lateral: constância ---
        lateral.appendChild(criar(`
            <div class="card card-destaque">
                <span class="rotulo claro">OFENSIVA</span>
                <strong class="numero-grande">${eng.ofensivaAtual}</strong>
                <span class="sub claro">${eng.ofensivaAtual === 1 ? 'dia seguido' : 'dias seguidos'} batendo a meta</span>
                <div class="separador"></div>
                <div class="mini-numeros">
                    <div><strong>${eng.melhorOfensiva}</strong><span>recorde</span></div>
                    <div><strong>${eng.diasEstudadosNoMes}</strong><span>no mês</span></div>
                    <div><strong>${eng.totalDiasComEstudo}</strong><span>no total</span></div>
                </div>
            </div>
        `));

        // --- lateral: pontos fracos ---
        if (assuntos.length) {
            const card = criar('<div class="card"><h2>Pontos fracos</h2><p class="sub">Assuntos em que você mais erra.</p></div>');
            assuntos.slice(0, 5).forEach(a => card.appendChild(criar(`
                <div class="item-fraco">
                    <div class="topo">
                        <span>${escapar(a.assunto)}</span>
                        <span class="${a.percentualAcerto < 60 ? 'ruim' : ''}">${a.percentualAcerto}%</span>
                    </div>
                    <span class="sub">${escapar(a.disciplina)} · ${a.acertos}/${a.respondidas}</span>
                </div>
            `)));
            const ir = criar('<button class="link">Revisar questões erradas →</button>');
            ir.onclick = () => abrir('erradas');
            card.appendChild(ir);
            lateral.appendChild(card);
        }

        // --- lateral: videoaulas recomendadas ---
        if (videos.length) {
            const card = criar('<div class="card"><h2>Recomendado para você</h2><p class="sub">Com base nos seus erros.</p></div>');
            videos.forEach(v => {
                const item = criar(`
                    <div class="item-video">
                        <img src="${escapar(v.thumbnail)}" alt="">
                        <div>
                            <strong>${escapar(v.titulo)}</strong>
                            <span class="sub">${escapar(v.disciplina)}${v.assunto ? ' · ' + escapar(v.assunto) : ''}</span>
                        </div>
                    </div>`);
                item.onclick = () => abrirVideo(v);
                card.appendChild(item);
            });
            lateral.appendChild(card);
        }

        grade.appendChild(principal);
        grade.appendChild(lateral);
        alvo.appendChild(grade);
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

// Cores das fatias, reaproveitadas na legenda.
function paleta(i) {
    // Lê as cores do tema ativo, para as fatias acompanharem claro/noturno.
    const raiz = getComputedStyle(document.documentElement);
    const v = nome => raiz.getPropertyValue(nome).trim();
    const cores = [v('--verde'), v('--verde-2'), v('--dourado'), v('--suave'), v('--mapa-2'), v('--erro')];
    return cores[i % cores.length];
}

// ---------- inicio ----------

if (token) {
    entrarNoApp();
} else {
    $('#tela-login').classList.remove('hidden');
}


// ---------- importacao de provas (admin) ----------

const ROTULO_STATUS_IMPORTACAO = {
    AGUARDANDO: 'Na fila', EXTRAINDO: 'Lendo a prova', GERANDO: 'Gerando inéditas',
    CONCLUIDA: 'Concluída', ERRO: 'Erro'
};

async function carregarImportacoes() {
    const alvo = $('#lista-importacoes');
    try {
        const lista = await api('/admin/importacoes');
        alvo.innerHTML = '';
        if (!lista.length) {
            alvo.appendChild(criar('<div class="vazio">Nenhuma prova importada ainda.</div>'));
        }
        lista.forEach(imp => alvo.appendChild(cardImportacao(imp)));

        // Enquanto houver importacao rodando, atualiza sozinho a cada 5 segundos.
        clearTimeout(atualizacaoImportacoes);
        const rodando = lista.some(i => ['AGUARDANDO', 'EXTRAINDO', 'GERANDO'].includes(i.status));
        if (rodando && !$('#tela-importacoes').classList.contains('hidden')) {
            atualizacaoImportacoes = setTimeout(carregarImportacoes, 5000);
        }
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

function cardImportacao(imp) {
    const rodando = ['AGUARDANDO', 'EXTRAINDO', 'GERANDO'].includes(imp.status);
    const titulo = imp.concurso
        ? `${escapar(imp.concurso)}${imp.cargo ? ' · ' + escapar(imp.cargo) : ''}`
        : escapar(imp.arquivoProva || 'Prova');
    const card = criar(`
        <div class="card card-importacao">
            <div class="topo-importacao">
                <div>
                    <strong>${titulo}</strong>
                    <p class="sub">${escapar(imp.arquivoProva || '')} · ${new Date(imp.criadoEm).toLocaleString('pt-BR')}</p>
                </div>
                <span class="etiqueta-situacao status-${imp.status.toLowerCase()}">${ROTULO_STATUS_IMPORTACAO[imp.status] || imp.status}</span>
            </div>
            ${rodando ? `<div class="barra"><div style="width:${imp.progresso}%"></div></div>
                         <p class="sub">${escapar(imp.etapa || '')}</p>` : ''}
            ${imp.status === 'ERRO' ? `<p class="erro">${escapar(imp.mensagemErro || 'Falhou')}</p>` : ''}
            <div class="numeros-importacao sub">
                ${imp.questoesLidas} questões lidas · ${imp.pendentes} pendentes · ${imp.aprovadas} aprovadas · ${imp.descartadas} descartadas
                · ${(imp.tokensEntrada + imp.tokensSaida).toLocaleString('pt-BR')} tokens
            </div>
            <div class="acoes-importacao"></div>
        </div>
    `);
    const acoes = card.querySelector('.acoes-importacao');
    if (imp.pendentes > 0) {
        const revisar = criar(`<button class="primario">Revisar ${imp.pendentes} pendente${imp.pendentes > 1 ? 's' : ''}</button>`);
        revisar.onclick = () => abrirRevisao(imp);
        acoes.appendChild(revisar);
    }
    if (imp.status === 'ERRO') {
        const reprocessar = criar('<button class="secundario">Reprocessar</button>');
        reprocessar.onclick = async () => {
            try { await api(`/admin/importacoes/${imp.id}/reprocessar`, { method: 'POST' }); }
            catch (e) { alert(e.message); }
            carregarImportacoes();
        };
        acoes.appendChild(reprocessar);
    }
    if (!rodando) {
        const excluir = criar('<button class="link">Excluir importação</button>');
        excluir.onclick = async () => {
            if (!confirm('Excluir a importação? As questões já aprovadas continuam no site.')) return;
            try { await api(`/admin/importacoes/${imp.id}`, { method: 'DELETE' }); }
            catch (e) { alert(e.message); }
            carregarImportacoes();
        };
        acoes.appendChild(excluir);
    }
    return card;
}

$('#btn-importar').onclick = async () => {
    const msg = $('#msg-importar');
    const prova = $('#imp-prova').files[0];
    const gabarito = $('#imp-gabarito').files[0];
    if (!prova || !gabarito) {
        msg.textContent = 'Escolha o PDF da prova e o do gabarito.';
        return;
    }
    const dados = new FormData();
    dados.append('prova', prova);
    dados.append('gabarito', gabarito);
    dados.append('cargo', $('#imp-cargo').value.trim());
    dados.append('ineditas', $('#imp-ineditas').value);

    const botao = $('#btn-importar');
    botao.disabled = true;
    msg.textContent = 'Enviando…';
    try {
        await api('/admin/importacoes', { method: 'POST', body: dados });
        msg.textContent = 'Enviada. O processamento leva alguns minutos; acompanhe abaixo.';
        ['#imp-prova', '#imp-gabarito', '#imp-cargo'].forEach(sel => { $(sel).value = ''; });
        carregarImportacoes();
    } catch (e) {
        msg.textContent = e.message;
    } finally {
        botao.disabled = false;
    }
};

$('#btn-atualizar-importacoes').onclick = carregarImportacoes;

// ---------- fila de revisao ----------

async function abrirRevisao(imp) {
    importacaoEmRevisao = imp;
    $('#painel-revisao').classList.remove('hidden');
    $('#titulo-revisao').textContent = `Revisão · ${imp.concurso || ''}${imp.cargo ? ' · ' + imp.cargo : ''}`;
    await carregarRascunhos();
    $('#painel-revisao').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

async function carregarRascunhos() {
    const alvo = $('#lista-rascunhos');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';
    try {
        const rascunhos = await api(`/admin/importacoes/${importacaoEmRevisao.id}/rascunhos`);
        alvo.innerHTML = '';
        if (!rascunhos.length) {
            alvo.appendChild(criar('<div class="vazio">Nada pendente nesta importação.</div>'));
        }
        rascunhos.forEach(r => alvo.appendChild(cardRascunho(r)));
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

function cardRascunho(r) {
    const letras = 'ABCDE';
    const alternativas = r.alternativas.map((a, i) => `
        <li class="${a.correta ? 'alt-correta' : ''}">${r.tipo === 'CERTO_ERRADO' ? '' : letras[i] + ') '}${escapar(a.texto)}${a.correta ? ' ✓' : ''}</li>`).join('');
    const card = criar(`
        <div class="card card-rascunho">
            <div class="meta">${escapar(r.disciplina)}${r.assunto ? ' · ' + escapar(r.assunto) : ''}
                ${r.tipo === 'CERTO_ERRADO' ? ' <span class="selo-tipo">C/E</span>' : ''}
                ${r.baseNumero ? ` · inspirada na questão ${r.baseNumero} da prova` : ''}</div>
            <div class="enunciado">${escapar(r.enunciado)}</div>
            <ul class="alternativas-rascunho">${alternativas}</ul>
            <p class="explicacao-rascunho"><strong>Explicação:</strong> ${escapar(r.explicacao || '')}</p>
            ${r.baseEnunciado ? `<details class="original-rascunho"><summary>Ver a questão original (privada)</summary>
                <p>${escapar(r.baseEnunciado)}</p></details>` : ''}
            <div class="acoes-importacao">
                <button class="primario aprovar">Aprovar</button>
                <button class="secundario editar">Editar</button>
                <button class="link descartar">Descartar</button>
                <span class="sub status-rascunho"></span>
            </div>
        </div>
    `);
    const status = card.querySelector('.status-rascunho');
    card.querySelector('.aprovar').onclick = async () => {
        try {
            await api(`/admin/rascunhos/${r.id}/aprovar`, { method: 'POST' });
            card.remove();
        } catch (e) { status.textContent = e.message; }
    };
    card.querySelector('.descartar').onclick = async () => {
        try {
            await api(`/admin/rascunhos/${r.id}/descartar`, { method: 'POST' });
            card.remove();
        } catch (e) { status.textContent = e.message; }
    };
    card.querySelector('.editar').onclick = () => card.replaceWith(editorRascunho(r));
    return card;
}

function editorRascunho(r) {
    const editor = criar(`
        <div class="card card-rascunho">
            <label>Enunciado<textarea class="ed-enunciado" rows="4"></textarea></label>
            <div class="ed-alternativas"></div>
            <label>Explicação<textarea class="ed-explicacao" rows="3"></textarea></label>
            <div class="acoes-importacao">
                <button class="primario salvar">Salvar</button>
                <button class="link cancelar">Cancelar</button>
                <span class="sub status-rascunho"></span>
            </div>
        </div>
    `);
    editor.querySelector('.ed-enunciado').value = r.enunciado;
    editor.querySelector('.ed-explicacao').value = r.explicacao || '';
    const caixa = editor.querySelector('.ed-alternativas');
    r.alternativas.forEach((a, i) => {
        const linha = criar(`
            <div class="linha-alternativa">
                <input type="radio" name="correta-${r.id}" ${a.correta ? 'checked' : ''} title="Marcar como correta">
                <input class="ed-texto" ${r.tipo === 'CERTO_ERRADO' ? 'disabled' : ''}>
            </div>`);
        linha.querySelector('.ed-texto').value = a.texto;
        caixa.appendChild(linha);
    });

    editor.querySelector('.cancelar').onclick = () => editor.replaceWith(cardRascunho(r));
    editor.querySelector('.salvar').onclick = async () => {
        const linhas = [...caixa.querySelectorAll('.linha-alternativa')];
        const corpo = {
            tipo: r.tipo, disciplina: r.disciplina, assunto: r.assunto,
            enunciado: editor.querySelector('.ed-enunciado').value,
            explicacao: editor.querySelector('.ed-explicacao').value,
            alternativas: linhas.map(l => ({
                texto: l.querySelector('.ed-texto').value,
                correta: l.querySelector('input[type=radio]').checked
            }))
        };
        try {
            const atualizado = await api(`/admin/rascunhos/${r.id}`, { method: 'PUT', body: JSON.stringify(corpo) });
            editor.replaceWith(cardRascunho(atualizado));
        } catch (e) {
            editor.querySelector('.status-rascunho').textContent = e.message;
        }
    };
    return editor;
}

$('#btn-aprovar-todos').onclick = async () => {
    if (!importacaoEmRevisao) return;
    if (!confirm('Publicar todas as questões pendentes desta importação?')) return;
    try {
        const r = await api(`/admin/importacoes/${importacaoEmRevisao.id}/aprovar-todos`, { method: 'POST' });
        alert(`${r.aprovadas} questão(ões) publicada(s).`);
        await carregarRascunhos();
        carregarImportacoes();
    } catch (e) {
        alert(e.message);
    }
};

$('#btn-fechar-revisao').onclick = () => {
    $('#painel-revisao').classList.add('hidden');
    importacaoEmRevisao = null;
    carregarImportacoes();
};

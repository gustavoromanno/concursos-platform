// Frontend em JS puro, servido pelo proprio Spring Boot.
// Sem build, sem framework: abre em http://localhost:8080

const API = '';
let token = localStorage.getItem('token');
let modoRegistro = false;
let simuladoAtual = null;
let cronometro = null;
let disciplinas = [];   // catalogo carregado uma vez apos o login
let marcados = new Set();  // ids das questoes marcadas pelo usuario
let cadernos = [];         // cadernos do usuario, para o menu de salvar

// ---------- infraestrutura ----------

async function api(caminho, opcoes = {}) {
    const resp = await fetch(API + caminho, {
        ...opcoes,
        headers: {
            'Content-Type': 'application/json',
            ...(token ? { Authorization: 'Bearer ' + token } : {}),
            ...(opcoes.headers || {})
        }
    });

    if (resp.status === 401 || resp.status === 403) {
        sair();
        throw new Error('Sessão expirada. Entre novamente.');
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
    $('#btn-entrar').textContent = modoRegistro ? 'Criar conta' : 'Entrar';
    $('#alternar-texto').textContent = modoRegistro ? 'Já tem conta?' : 'Não tem conta?';
    $('#btn-alternar').textContent = modoRegistro ? 'Fazer login' : 'Criar conta';
    $('#login-erro').textContent = '';
};

$('#btn-entrar').onclick = async () => {
    const email = $('#login-email').value.trim();
    const senha = $('#login-senha').value;
    $('#login-erro').textContent = '';

    try {
        if (modoRegistro) {
            const nome = $('#reg-nome').value.trim();
            if (!nome) throw new Error('Informe seu nome');
            await api('/auth/registrar', {
                method: 'POST',
                body: JSON.stringify({ nome, email, senha })
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
        $('#login-erro').textContent = e.message;
    }
};

function sair() {
    token = null;
    localStorage.removeItem('token');
    pararCronometro();
    simuladoAtual = null;
    $('#topo').classList.add('hidden');
    document.querySelectorAll('main section').forEach(s => s.classList.add('hidden'));
    $('#tela-login').classList.remove('hidden');
}

$('#sair').onclick = sair;

async function entrarNoApp() {
    $('#tela-login').classList.add('hidden');
    $('#topo').classList.remove('hidden');
    await carregarCatalogo();
    abrir('questoes');
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

    preencher('#f-disciplina', 'Todas');
    preencher('#s-disciplina', 'Todas');
    preencher('#vf-disciplina', 'Todas');
    preencher('#v-disciplina', null);

    atualizarAssuntos('#f-disciplina', '#f-assunto-id', 'Todos');
    atualizarAssuntos('#v-disciplina', '#v-assunto', 'Geral da disciplina');
}

// Marcadores e cadernos do usuario: carregados uma vez para os cards ja
// nascerem com o estado certo.
async function carregarEstadoPessoal() {
    try {
        marcados = new Set(await api('/marcadores/ids'));
    } catch {
        marcados = new Set();
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

$('#f-disciplina').onchange = () => atualizarAssuntos('#f-disciplina', '#f-assunto-id', 'Todos');
$('#v-disciplina').onchange = () => atualizarAssuntos('#v-disciplina', '#v-assunto', 'Geral da disciplina');

// ---------- navegacao ----------

document.querySelectorAll('#topo nav button').forEach(btn => {
    btn.onclick = () => abrir(btn.dataset.tela);
});

function abrir(tela) {
    document.querySelectorAll('main section').forEach(s => s.classList.add('hidden'));
    document.querySelectorAll('#topo nav button').forEach(b =>
        b.classList.toggle('ativo', b.dataset.tela === tela));
    $('#tela-' + tela).classList.remove('hidden');

    if (tela === 'questoes') carregarQuestoes();
    if (tela === 'erradas') carregarErradas();
    if (tela === 'cadernos') carregarCadernos();
    if (tela === 'marcadores') carregarMarcadores();
    if (tela === 'videoaulas') carregarVideoaulas();
    if (tela === 'dashboard') carregarDashboard();
}

// ---------- questoes ----------

function cardQuestao(questao, aoResponder, opcoes = {}) {
    const card = criar(`
        <div class="card">
            <div class="cabecalho-questao">
                <div class="meta">${escapar(questao.disciplina)} · ${escapar(questao.banca)} · ${questao.ano}${questao.assunto ? ' · ' + escapar(questao.assunto) : ''}</div>
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
    questao.alternativas.forEach((alt, i) => {
        const botao = criar(`<button class="alternativa">${String.fromCharCode(65 + i)}) ${escapar(alt.texto)}</button>`);
        botao.onclick = () => aoResponder(questao, alt, card);
        alternativas.appendChild(botao);
    });

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

async function carregarQuestoes() {
    const alvo = $('#lista-questoes');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    const params = new URLSearchParams({ size: 20 });
    const disciplinaId = $('#f-disciplina').value;
    const assuntoId = $('#f-assunto-id').value;
    const ano = $('#f-ano').value.trim();
    if (disciplinaId) params.set('disciplinaId', disciplinaId);
    if (assuntoId) params.set('assuntoId', assuntoId);
    if (ano) params.set('ano', ano);

    try {
        const pagina = await api('/questoes?' + params);
        alvo.innerHTML = '';
        if (!pagina.content.length) {
            alvo.appendChild(criar('<div class="vazio">Nenhuma questão encontrada.</div>'));
            return;
        }
        pagina.content.forEach(q => alvo.appendChild(cardQuestao(q, responderDireto)));
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

$('#btn-filtrar').onclick = carregarQuestoes;

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
    try {
        simuladoAtual = await api('/simulados', {
            method: 'POST',
            body: JSON.stringify({
                quantidade: Number($('#s-quantidade').value),
                duracaoMinutos: Number($('#s-duracao').value),
                disciplinaId: disciplinaId ? Number(disciplinaId) : null
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


// ---------- engajamento (ofensiva, meta e mapa de estudo) ----------

async function carregarEngajamento() {
    const alvo = $('#engajamento-dashboard');
    const mapaAlvo = $('#mapa-dashboard');
    alvo.innerHTML = '';
    mapaAlvo.innerHTML = '';

    try {
        const e = await api('/engajamento');

        const card = criar(`
            <div class="card">
                <h2>Constância</h2>
                <div class="numeros">
                    <div class="numero"><strong>${e.ofensivaAtual}</strong><span>${e.ofensivaAtual === 1 ? 'dia seguido' : 'dias seguidos'}</span></div>
                    <div class="numero"><strong>${e.melhorOfensiva}</strong><span>melhor sequência</span></div>
                    <div class="numero"><strong>${e.diasEstudadosNoMes}</strong><span>dias neste mês</span></div>
                    <div class="numero"><strong>${e.totalDiasComEstudo}</strong><span>dias no total</span></div>
                </div>
                <div class="progresso-meta">
                    <div class="topo">
                        <span>${e.metaBatidaHoje ? 'Meta de hoje batida' : 'Meta de hoje'}</span>
                        <span>${e.respondidasHoje}/${e.metaDiaria}</span>
                    </div>
                    <div class="barra"><div class="${e.metaBatidaHoje ? 'completa' : ''}" style="width:${Math.min(100, (e.respondidasHoje / e.metaDiaria) * 100)}%"></div></div>
                    <div class="editor-meta">
                        <label>Meta diária
                            <input id="input-meta" type="number" min="1" max="500" value="${e.metaDiaria}">
                        </label>
                        <button id="btn-salvar-meta" class="acao">salvar</button>
                    </div>
                </div>
            </div>
        `);
        alvo.appendChild(card);

        $('#btn-salvar-meta').onclick = async () => {
            try {
                await api('/engajamento/meta', {
                    method: 'PUT',
                    body: JSON.stringify({ questoesPorDia: Number($('#input-meta').value) })
                });
                carregarEngajamento();
            } catch (err) {
                alert(err.message);
            }
        };

        // Mapa de estudo: uma coluna por semana, uma linha por dia da semana.
        const dias = await api('/engajamento/mapa?dias=182');
        const mapa = criar('<div class="card"><h2>Mapa de estudo</h2><p class="sub">Últimos 6 meses. Quanto mais escuro, mais questões naquele dia.</p></div>');
        const grade = criar('<div class="mapa-estudo"></div>');

        // Preenche o começo para a primeira coluna alinhar com o dia da semana.
        if (dias.length) {
            const primeiro = new Date(dias[0].dia + 'T00:00:00').getDay();
            for (let i = 0; i < primeiro; i++) {
                grade.appendChild(criar('<div class="quadro vazio-quadro"></div>'));
            }
        }

        dias.forEach(d => {
            const data = new Date(d.dia + 'T00:00:00').toLocaleDateString('pt-BR');
            const quadro = criar(`<div class="quadro nivel-${d.nivel}" title="${data}: ${d.respondidas} ${d.respondidas === 1 ? 'questão' : 'questões'}"></div>`);
            grade.appendChild(quadro);
        });

        mapa.appendChild(grade);
        mapa.appendChild(criar(`
            <div class="legenda-mapa">
                <span class="sub">menos</span>
                <div class="quadro nivel-0"></div>
                <div class="quadro nivel-1"></div>
                <div class="quadro nivel-2"></div>
                <div class="quadro nivel-3"></div>
                <div class="quadro nivel-4"></div>
                <span class="sub">mais</span>
            </div>
        `));
        mapaAlvo.appendChild(mapa);
    } catch (e) {
        alvo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
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

async function carregarDashboard() {
    const resumo = $('#resumo-dashboard');
    const disc = $('#disciplinas-dashboard');
    const assuntos = $('#assuntos-dashboard');
    const evolucao = $('#evolucao-dashboard');
    resumo.innerHTML = '<div class="vazio">Carregando…</div>';
    disc.innerHTML = assuntos.innerHTML = evolucao.innerHTML = '';

    carregarEngajamento();

    try {
        const d = await api('/estatisticas');

        resumo.innerHTML = '';
        resumo.appendChild(criar(`
            <div class="card">
                <h2>Desempenho geral</h2>
                <div class="numeros">
                    <div class="numero"><strong>${d.percentualAcertoGeral}%</strong><span>aproveitamento</span></div>
                    <div class="numero"><strong>${d.totalRespondidas}</strong><span>respondidas</span></div>
                    <div class="numero"><strong>${d.totalAcertos}</strong><span>acertos</span></div>
                    <div class="numero"><strong>${d.totalErros}</strong><span>erros</span></div>
                </div>
            </div>
        `));

        if (d.porDisciplina.length) {
            const card = criar('<div class="card"><h2>Por disciplina</h2></div>');
            d.porDisciplina.forEach(x =>
                card.appendChild(linhaBarra(x.disciplina, x.acertos, x.respondidas, x.percentualAcerto)));
            disc.appendChild(card);
        }

        // Pontos fracos: a API ja devolve do pior para o melhor.
        const porAssunto = await api('/estatisticas/assuntos');
        if (porAssunto.length) {
            const card = criar('<div class="card"><h2>Por assunto</h2><p class="sub">Do que você mais erra para o que mais acerta.</p></div>');
            porAssunto.forEach(x =>
                card.appendChild(linhaBarra(x.assunto, x.acertos, x.respondidas, x.percentualAcerto, x.disciplina)));
            assuntos.appendChild(card);
        }

        const serie = await api('/estatisticas/evolucao?dias=30');
        if (serie.length) {
            const card = criar('<div class="card"><h2>Últimos 30 dias</h2></div>');
            serie.forEach(p => card.appendChild(linhaBarra(
                new Date(p.dia + 'T00:00:00').toLocaleDateString('pt-BR'),
                p.acertos, p.respondidas, p.percentualAcerto
            )));
            evolucao.appendChild(card);
        }
    } catch (e) {
        resumo.innerHTML = `<div class="vazio">${escapar(e.message)}</div>`;
    }
}

// ---------- inicio ----------

if (token) {
    entrarNoApp();
} else {
    $('#tela-login').classList.remove('hidden');
}

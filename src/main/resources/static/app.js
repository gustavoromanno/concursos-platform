// Frontend em JS puro, servido pelo proprio Spring Boot.
// Sem build, sem framework: abre em http://localhost:8080

const API = '';                     // mesma origem da aplicacao
let token = localStorage.getItem('token');
let modoRegistro = false;
let simuladoAtual = null;
let cronometro = null;

// ---------- infraestrutura ----------

// Todo acesso a API passa por aqui: injeta o token e trata sessao expirada.
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
        throw new Error(corpo.message || 'Erro na requisição (HTTP ' + resp.status + ')');
    }

    return resp.status === 204 ? null : resp.json();
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
        entrarNoApp();
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

function entrarNoApp() {
    $('#tela-login').classList.add('hidden');
    $('#topo').classList.remove('hidden');
    abrir('questoes');
}

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
    if (tela === 'dashboard') carregarDashboard();
}

// ---------- questoes ----------

// Monta o card de uma questao. `aoResponder` decide o que acontece no clique,
// o que permite reusar o mesmo card na listagem, na revisao e no simulado.
function cardQuestao(questao, aoResponder) {
    const card = criar(`
        <div class="card">
            <div class="meta">${escapar(questao.disciplina)} · ${escapar(questao.banca)} · ${questao.ano}${questao.assunto ? ' · ' + escapar(questao.assunto) : ''}</div>
            <div class="enunciado">${escapar(questao.enunciado)}</div>
            <div class="alternativas"></div>
            <div class="area-feedback"></div>
        </div>
    `);

    const alternativas = card.querySelector('.alternativas');
    questao.alternativas.forEach((alt, i) => {
        const botao = criar(`<button class="alternativa">${String.fromCharCode(65 + i)}) ${escapar(alt.texto)}</button>`);
        botao.onclick = () => aoResponder(questao, alt, card);
        alternativas.appendChild(botao);
    });

    return card;
}

function travarAlternativas(card) {
    card.querySelectorAll('.alternativa').forEach(b => b.disabled = true);
}

// Responder fora do simulado: mostra a correcao na hora.
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

    card.querySelector('.area-feedback').appendChild(criar(`
        <div class="feedback ${resultado.correta ? 'ok' : 'nok'}">
            <strong>${resultado.correta ? 'Acertou.' : 'Errou.'}</strong>
            ${resultado.explicacao ? ' ' + escapar(resultado.explicacao) : ''}
        </div>
    `));
}

async function carregarQuestoes() {
    const alvo = $('#lista-questoes');
    alvo.innerHTML = '<div class="vazio">Carregando…</div>';

    const params = new URLSearchParams({ size: 20 });
    const assunto = $('#f-assunto').value.trim();
    const ano = $('#f-ano').value.trim();
    if (assunto) params.set('assunto', assunto);
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
    try {
        simuladoAtual = await api('/simulados', {
            method: 'POST',
            body: JSON.stringify({
                quantidade: Number($('#s-quantidade').value),
                duracaoMinutos: Number($('#s-duracao').value)
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

    // Durante o simulado nao ha correcao: apenas registra a escolha.
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
        }));
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
            finalizarSimulado();   // tempo esgotado: encerra sozinho
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
        r.porDisciplina.forEach(d => porDisciplina.appendChild(linhaDisciplina(d)));
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

// ---------- dashboard ----------

function linhaDisciplina(d) {
    return criar(`
        <div class="linha-disciplina">
            <div class="topo">
                <span>${escapar(d.disciplina)}</span>
                <span>${d.acertos}/${d.respondidas} · ${d.percentualAcerto}%</span>
            </div>
            <div class="barra"><div style="width:${d.percentualAcerto}%"></div></div>
        </div>
    `);
}

async function carregarDashboard() {
    const resumo = $('#resumo-dashboard');
    const disciplinas = $('#disciplinas-dashboard');
    const evolucao = $('#evolucao-dashboard');
    resumo.innerHTML = '<div class="vazio">Carregando…</div>';
    disciplinas.innerHTML = '';
    evolucao.innerHTML = '';

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
            d.porDisciplina.forEach(x => card.appendChild(linhaDisciplina(x)));
            disciplinas.appendChild(card);
        }

        const serie = await api('/estatisticas/evolucao?dias=30');
        if (serie.length) {
            const card = criar('<div class="card"><h2>Últimos 30 dias</h2></div>');
            serie.forEach(p => card.appendChild(criar(`
                <div class="linha-disciplina">
                    <div class="topo">
                        <span>${new Date(p.dia + 'T00:00:00').toLocaleDateString('pt-BR')}</span>
                        <span>${p.acertos}/${p.respondidas} · ${p.percentualAcerto}%</span>
                    </div>
                    <div class="barra"><div style="width:${p.percentualAcerto}%"></div></div>
                </div>
            `)));
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

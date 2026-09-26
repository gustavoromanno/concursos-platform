/*
 * Verificacao automatica do frontend (roda no CI, antes dos testes Java).
 *
 * Carrega index.html + app.js num navegador simulado (jsdom) e falha se:
 *   1. o app.js lancar erro ao carregar — um erro no meio do arquivo impede
 *      que todo o codigo seguinte rode (botoes param de funcionar);
 *   2. o app.js buscar um elemento $('#id') que nao existe no HTML nem e
 *      criado pelo proprio JS;
 *   3. as interacoes basicas da tela de cadastro nao funcionarem.
 *
 * Uso: npm install --no-save jsdom@24 && node scripts/verificar-frontend.js
 */
const { JSDOM } = require('jsdom');
const fs = require('fs');
const path = require('path');

const pasta = path.join(__dirname, '..', 'src', 'main', 'resources', 'static');
const htmlOriginal = fs.readFileSync(path.join(pasta, 'index.html'), 'utf8');
const js = fs.readFileSync(path.join(pasta, 'app.js'), 'utf8');
const falhas = [];

// 2) IDs usados pelo JS que nao existem em lugar nenhum
const idsUsados = new Set([...js.matchAll(/\$\('#([\w-]+)'\)/g)].map(m => m[1]));
for (const id of idsUsados) {
    const noHtml = htmlOriginal.includes(`id="${id}"`);
    const criadoNoJs = js.includes(`id="${id}"`) || js.includes(`id=\\"${id}\\"`);
    if (!noHtml && !criadoNoJs) falhas.push(`app.js usa $('#${id}'), mas esse elemento nao existe no index.html`);
}

// 1) carregar o script
const html = htmlOriginal.replace(/<script[^>]*src="app\.js"[^>]*><\/script>/, '');
const dom = new JSDOM(html, { runScripts: 'outside-only', url: 'http://localhost/' });
const w = dom.window;
w.fetch = () => Promise.resolve({ ok: true, status: 204, text: () => Promise.resolve(''), json: () => Promise.resolve(null) });
w.scrollTo = () => {};
try {
    w.eval(js);
} catch (e) {
    falhas.push(`app.js lancou erro ao carregar: ${e.message}`);
}

// 3) interacoes da tela de cadastro
if (!falhas.length) {
    const $ = s => w.document.querySelector(s);
    const digitar = (sel, valor) => {
        $(sel).value = valor;
        $(sel).dispatchEvent(new w.Event('input'));
    };
    try {
        $('#btn-alternar').click();   // vai para "Criar conta"
        if ($('#campos-registro').classList.contains('hidden')) falhas.push('"Criar conta" nao mostra os campos de cadastro');

        digitar('#login-senha', 'Senha1!');
        digitar('#reg-confirma-senha', 'Senha1!');
        for (const regra of ['regra-minimo', 'regra-maiuscula', 'regra-numero', 'regra-especial', 'regra-confirma']) {
            if ($('#' + regra) && !$('#' + regra).classList.contains('ok')) {
                falhas.push(`regra de senha "${regra}" nao ficou marcada com uma senha valida`);
            }
        }

        const olho = w.document.querySelector('.btn-olho[data-alvo="login-senha"]');
        if (olho) {
            olho.click();
            if ($('#login-senha').type !== 'text') falhas.push('o botao de olho nao mostra a senha');
        }
    } catch (e) {
        falhas.push(`erro ao simular o cadastro: ${e.message}`);
    }
}

if (falhas.length) {
    console.error('Verificacao do frontend FALHOU:');
    falhas.forEach(f => console.error('  - ' + f));
    process.exit(1);
}
console.log('Frontend OK: app.js carrega sem erros, todos os elementos existem e o cadastro responde.');

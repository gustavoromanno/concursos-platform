/*
 * Service worker: deixa o site instalavel como app e abre a "casca" da
 * interface mesmo sem internet.
 *
 * - Arquivos do site (HTML, JS, CSS, icones): CACHE PRIMEIRO, atualizando em
 *   segundo plano (stale-while-revalidate). A pagina abre na hora com a copia
 *   guardada, mesmo com o servidor lento ou acordando; a versao nova baixada fica
 *   para a proxima abertura. Sem copia guardada (1a visita), vai direto na rede.
 * - Chamadas da API (/auth, /questoes, /pagamentos...) NUNCA passam pelo cache:
 *   dados pessoais e respostas sempre vem do servidor.
 */
const CACHE = 'casca-v15';
const CASCA = ['/', '/index.html', '/app.js', '/style.css', '/manifest.webmanifest',
               '/icones/icone.svg', '/icones/icone-192.png', '/icones/icone-512.png'];

self.addEventListener('install', evento => {
    evento.waitUntil(caches.open(CACHE).then(c => c.addAll(CASCA)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', evento => {
    evento.waitUntil(
        caches.keys()
            .then(chaves => Promise.all(chaves.filter(k => k !== CACHE).map(k => caches.delete(k))))
            .then(() => self.clients.claim())
    );
});

self.addEventListener('fetch', evento => {
    const req = evento.request;
    const url = new URL(req.url);
    const ehCasca = req.method === 'GET' && url.origin === location.origin
        && (CASCA.includes(url.pathname) || url.pathname.startsWith('/icones/'));
    if (!ehCasca) return;   // API e terceiros: direto na rede, sem cache

    evento.respondWith(caches.open(CACHE).then(async cache => {
        const guardada = await cache.match(req);
        const daRede = fetch(req)
            .then(resp => {
                if (resp.ok) cache.put(req, resp.clone());
                return resp;
            })
            .catch(() => guardada || cache.match('/index.html'));
        if (guardada) {
            evento.waitUntil(daRede.catch(() => {}));   // atualiza em segundo plano
            return guardada;
        }
        return daRede;
    }));
});

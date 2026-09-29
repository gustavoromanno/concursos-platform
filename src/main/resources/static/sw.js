/*
 * Service worker: deixa o site instalavel como app e abre a "casca" da
 * interface mesmo sem internet.
 *
 * - Arquivos do site (HTML, JS, CSS, icones): REDE PRIMEIRO. Com internet, sempre
 *   a versao nova (cada deploy chega na hora); sem internet, a ultima guardada.
 * - Chamadas da API (/auth, /questoes, /pagamentos...) NUNCA passam pelo cache:
 *   dados pessoais e respostas sempre vem do servidor.
 */
const CACHE = 'casca-v1';
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

    evento.respondWith(
        fetch(req)
            .then(resp => {
                if (resp.ok) {
                    const copia = resp.clone();
                    caches.open(CACHE).then(c => c.put(req, copia));
                }
                return resp;
            })
            .catch(() => caches.match(req).then(r => r || caches.match('/index.html')))
    );
});

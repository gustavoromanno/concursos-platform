package com.gustavo.concursos.email;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Envia campanhas promocionais em segundo plano, uma pessoa por vez (o Resend
 * limita requisicoes por segundo). Cada e-mail leva o link de descadastro
 * individual e o cabecalho List-Unsubscribe.
 */
@Service
public class CampanhaService {

    private final UsuarioRepository usuarioRepository;
    private final CampanhaEmailRepository campanhaRepository;
    private final EmailCliente email;
    private final TransactionTemplate transacao;
    private final String urlBase;
    private final long pausaMs;
    private final String nomeMarca;
    private final SecureRandom aleatorio = new SecureRandom();

    public CampanhaService(
            UsuarioRepository usuarioRepository,
            CampanhaEmailRepository campanhaRepository,
            EmailCliente email,
            TransactionTemplate transacao,
            @Value("${app.url-base:http://localhost:8080}") String urlBase,
            @Value("${email.pausa-entre-envios-ms:600}") long pausaMs,
            @Value("${app.nome:Romano Concursos}") String nomeMarca
    ) {
        this.nomeMarca = nomeMarca;
        this.usuarioRepository = usuarioRepository;
        this.campanhaRepository = campanhaRepository;
        this.email = email;
        this.transacao = transacao;
        this.urlBase = urlBase.replaceAll("/+$", "");
        this.pausaMs = pausaMs;
    }

    @Async("importacaoExecutor")
    public void enviar(Long campanhaId) {
        CampanhaEmail campanha = campanhaRepository.findById(campanhaId).orElseThrow();
        List<Long> ids = usuarioRepository.findByAceitaMarketingTrueOrderByCriadoEmAsc().stream().map(Usuario::getId).toList();
        int enviados = 0;
        for (Long id : ids) {
            Usuario u = transacao.execute(t -> garantirToken(id));
            if (u == null || !u.isAceitaMarketing()) continue;   // pode ter se descadastrado no meio do envio
            String link = urlBase + "/publico/descadastro?t=" + u.getTokenDescadastro();
            email.enviar(u.getEmail(), campanha.getAssunto(), montarHtml(u.getNome(), campanha.getMensagem(), link, nomeMarca),
                    Map.of("List-Unsubscribe", "<" + link + ">"));
            enviados++;
            int parcial = enviados;
            transacao.executeWithoutResult(t -> campanhaRepository.findById(campanhaId).ifPresent(c -> c.setEnviados(parcial)));
            pausar();
        }
        transacao.executeWithoutResult(t -> campanhaRepository.findById(campanhaId).ifPresent(c -> c.setStatus("CONCLUIDA")));
    }

    public void enviarTeste(Usuario admin, String assunto, String mensagem) {
        email.enviar(admin.getEmail(), "[TESTE] " + assunto,
                montarHtml(admin.getNome(), mensagem, urlBase + "/publico/descadastro?t=exemplo", nomeMarca));
    }

    private Usuario garantirToken(Long id) {
        Usuario u = usuarioRepository.findById(id).orElse(null);
        if (u != null && u.getTokenDescadastro() == null) {
            byte[] bruto = new byte[24];
            aleatorio.nextBytes(bruto);
            u.setTokenDescadastro(Base64.getUrlEncoder().withoutPadding().encodeToString(bruto));
            usuarioRepository.save(u);
        }
        return u;
    }

    // Texto simples do admin vira HTML seguro: escapado e com paragrafos.
    static String montarHtml(String nome, String mensagem, String linkDescadastro, String marca) {
        StringBuilder html = new StringBuilder("<p>Olá, ").append(escapar(primeiroNome(nome))).append("!</p>");
        for (String paragrafo : mensagem.strip().split("\\R\\s*\\R")) {
            html.append("<p>").append(escapar(paragrafo.strip()).replaceAll("\\R", "<br>")).append("</p>");
        }
        html.append("<hr><p style=\"font-size:12px;color:#6B8880\">Você recebe este e-mail porque aceitou receber novidades da ")
            .append(escapar(marca)).append(". <a href=\"").append(linkDescadastro).append("\">Não quero mais receber</a>.</p>");
        return html.toString();
    }

    private static String primeiroNome(String nome) {
        return nome == null || nome.isBlank() ? "" : nome.trim().split("\\s+")[0];
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private void pausar() {
        if (pausaMs <= 0) return;
        try {
            Thread.sleep(pausaMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

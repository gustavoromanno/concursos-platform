package com.gustavo.concursos.email;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gera a versao em texto simples de um e-mail HTML. Mandar as duas versoes
 * (HTML + texto) e uma das boas praticas que os provedores (Gmail, Outlook)
 * esperam de remetentes legitimos; e-mail so com HTML pesa a favor do spam.
 */
public final class TextoSimples {

    private static final Pattern LINK = Pattern.compile("(?is)<a\\s[^>]*href=\"([^\"]*)\"[^>]*>(.*?)</a>");

    private TextoSimples() {
    }

    public static String deHtml(String html) {
        if (html == null) return "";
        String s = html.replaceAll("(?is)<(head|style|script)[^>]*>.*?</\\1>", "");

        // Links: "texto (endereco)"; quando o texto ja e o proprio endereco, so o endereco.
        Matcher m = LINK.matcher(s);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String href = m.group(1);
            String texto = m.group(2).replaceAll("<[^>]+>", "").trim();
            String troca = texto.isEmpty() || texto.equals(href) ? href : texto + ": " + href;
            m.appendReplacement(sb, Matcher.quoteReplacement(troca));
        }
        m.appendTail(sb);
        s = sb.toString();

        s = s.replaceAll("(?i)<br\\s*/?>", "\n")
                .replaceAll("(?i)<li[^>]*>", "\n- ")
                .replaceAll("(?i)</(p|div|tr|h[1-6]|ul|ol|table)>", "\n\n")
                .replaceAll("<[^>]+>", "");
        s = s.replace("&nbsp;", " ").replace("&lt;", "<").replace("&gt;", ">")
                .replace("&quot;", "\"").replace("&#39;", "'").replace("&amp;", "&");

        StringBuilder saida = new StringBuilder();
        for (String linha : s.split("\n", -1)) saida.append(linha.strip()).append('\n');
        return saida.toString().replaceAll("\n{3,}", "\n\n").strip();
    }
}

package com.gustavo.concursos.email;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModeloEmailTest {

    private static final String LINK = "https://romanoconcursos.com.br/?confirmar=abc_123-XYZ";

    private String html() {
        return ModeloEmail.transacional("Romano Concursos", "https://romanoconcursos.com.br/",
                "Confirme seu e-mail — Romano Concursos", "Olá, Ana!",
                "Falta só um passo &amp; pronto.", "Confirmar meu e-mail", LINK,
                "Se você não criou esta conta, ignore este e-mail.");
    }

    @Test
    void htmlCompletoComBotaoDescritivoEEnderecoPorExtenso() {
        String h = html();
        assertThat(h).startsWith("<!DOCTYPE html>").contains("lang=\"pt-BR\"")
                .contains(">Confirmar meu e-mail</a>")
                .contains(">" + LINK + "</a>")
                .contains("(romanoconcursos.com.br)")
                .contains("src=\"https://romanoconcursos.com.br/icones/email-logotipo.png\"")
                .contains("alt=\"Romano Concursos\"")
                .doesNotContainIgnoringCase("clique aqui");
    }

    @Test
    void textoSimplesTemOsLinksEOsTextosSemTags() {
        String t = TextoSimples.deHtml(html());
        assertThat(t)
                .doesNotContain("<").doesNotContain("&amp;")
                .contains("Olá, Ana!")
                .contains("Falta só um passo & pronto.")
                .contains("Confirmar meu e-mail: " + LINK)
                .contains("Se você não criou esta conta, ignore este e-mail.")
                .doesNotContain("Confirme seu e-mail — Romano Concursos\n"); // o <title> do <head> fica de fora
        assertThat(t).doesNotContain("\n\n\n").doesNotStartWith("\n");
    }

    @Test
    void textoSimplesDeHtmlSimplesENulo() {
        assertThat(TextoSimples.deHtml("<p>Um</p><p>Dois<br>Três</p><ul><li>A</li><li>B</li></ul>"))
                .isEqualTo("Um\n\nDois\nTrês\n\n- A\n- B");
        assertThat(TextoSimples.deHtml(null)).isEmpty();
    }
}

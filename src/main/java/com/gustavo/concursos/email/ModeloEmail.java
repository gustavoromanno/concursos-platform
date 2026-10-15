package com.gustavo.concursos.email;

/**
 * Layout comum dos e-mails transacionais (confirmacao de cadastro, nova senha).
 *
 * Pensado para nao cair no spam: documento HTML completo, botao com texto que diz
 * o que acontece (nada de "clique aqui"), o endereco do link escrito por extenso
 * (o mesmo do botao) e um rodape que identifica quem enviou e por que.
 * Os textos recebidos ja devem vir escapados quando tiverem dados do usuario.
 */
public final class ModeloEmail {

    private static final String VERDE = "#1F7A6B";

    private ModeloEmail() {
    }

    public static String transacional(String marca, String urlBase, String titulo, String saudacao,
                                      String introHtml, String textoBotao, String link, String avisoHtml) {
        String base = urlBase.replaceFirst("/+$", "");
        String site = base.replaceFirst("^https?://", "");
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>%1$s</title></head>
                <body style="margin:0;padding:0;background:#F5F8F7;font-family:Arial,Helvetica,sans-serif;color:#1C2B28;">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#F5F8F7;padding:24px 12px;">
                <tr><td align="center">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width:520px;background:#FFFFFF;border:1px solid #D9E3E0;border-radius:8px;">
                <tr><td style="background:%2$s;padding:18px 28px;border-radius:8px 8px 0 0;border-bottom:3px solid #C08F3C;">
                <img src="%10$s/icones/email-logotipo.png" width="260" height="47" alt="%3$s" style="display:block;border:0;outline:none;width:260px;height:auto;max-width:100%%;color:#FFFFFF;font-size:20px;font-weight:bold;">
                </td></tr>
                <tr><td style="padding:24px 28px;font-size:15px;line-height:1.6;">
                <p style="margin:0 0 14px;">%4$s</p>
                <p style="margin:0 0 22px;">%5$s</p>
                <p style="margin:0 0 22px;"><a href="%6$s" style="display:inline-block;background:%2$s;color:#FFFFFF;text-decoration:none;font-weight:bold;padding:12px 22px;border-radius:6px;">%7$s</a></p>
                <p style="margin:0 0 6px;font-size:13px;color:#5B6E69;">Se o botão não funcionar, copie e cole este endereço no navegador:</p>
                <p style="margin:0 0 22px;font-size:13px;word-break:break-all;"><a href="%6$s" style="color:%2$s;">%6$s</a></p>
                <p style="margin:0;font-size:14px;color:#5B6E69;">%8$s</p>
                </td></tr>
                <tr><td style="padding:16px 28px;border-top:1px solid #D9E3E0;font-size:12px;color:#5B6E69;">
                Você recebeu este e-mail porque este endereço foi usado em uma conta na %3$s (%9$s).
                </td></tr>
                </table>
                </td></tr>
                </table>
                </body>
                </html>
                """.formatted(titulo, VERDE, marca, saudacao, introHtml, link, textoBotao, avisoHtml, site, base);
    }
}

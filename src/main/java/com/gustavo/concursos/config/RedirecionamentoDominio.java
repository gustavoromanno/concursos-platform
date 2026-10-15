package com.gustavo.concursos.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;

/**
 * Quem acessa pelo endereco antigo do Render (*.onrender.com) e mandado, com
 * redirecionamento permanente (301), para o dominio oficial (APP_URL_BASE),
 * mantendo o caminho e a query. Assim favoritos, links antigos e o app instalado
 * passam a usar o dominio novo.
 *
 * Nao redireciona: /health (verificacao do Render), pedidos que nao sejam GET/HEAD
 * (um POST redirecionado perde o corpo) e quando APP_URL_BASE nao e https
 * (desenvolvimento local).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RedirecionamentoDominio extends OncePerRequestFilter {

    private final String destino;

    public RedirecionamentoDominio(@Value("${app.url-base:http://localhost:8080}") String urlBase) {
        String base = urlBase.endsWith("/") ? urlBase.substring(0, urlBase.length() - 1) : urlBase;
        this.destino = base.startsWith("https://") ? base : null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws ServletException, IOException {
        String host = req.getServerName();
        boolean enderecoAntigo = host != null && host.endsWith(".onrender.com");
        boolean leitura = "GET".equals(req.getMethod()) || "HEAD".equals(req.getMethod());
        if (destino != null && enderecoAntigo && leitura && !"/health".equals(req.getRequestURI())
                && !URI.create(destino).getHost().equalsIgnoreCase(host)) {
            String query = req.getQueryString();
            resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", destino + req.getRequestURI() + (query == null ? "" : "?" + query));
            return;
        }
        chain.doFilter(req, resp);
    }
}

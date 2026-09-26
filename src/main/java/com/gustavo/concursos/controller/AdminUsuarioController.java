package com.gustavo.concursos.controller;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

// Exportacao da lista de contatos para e-mail promocional (so ADMIN, via /admin/**).
// Contem APENAS quem marcou que aceita receber — exigencia da LGPD.
@RestController
public class AdminUsuarioController {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final UsuarioRepository usuarioRepository;

    public AdminUsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/admin/usuarios/contatos-marketing.csv")
    public ResponseEntity<byte[]> contatosMarketing() {
        StringBuilder csv = new StringBuilder("\uFEFFnome;email;cadastro;consentimento\n");  // BOM: acentos certos no Excel
        for (Usuario u : usuarioRepository.findByAceitaMarketingTrueOrderByCriadoEmAsc()) {
            csv.append(celula(u.getNome())).append(';')
               .append(celula(u.getEmail())).append(';')
               .append(u.getCriadoEm() == null ? "" : u.getCriadoEm().format(DATA)).append(';')
               .append(u.getMarketingAtualizadoEm() == null ? "" : u.getMarketingAtualizadoEm().format(DATA))
               .append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contatos-marketing.csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    // Aspas duplas e ; no nome nao quebram o CSV. "=", "+", "-" e "@" no inicio
    // viram texto (evita formula executada ao abrir no Excel).
    static String celula(String valor) {
        if (valor == null) return "";
        String v = valor.replace("\"", "\"\"");
        if (!v.isEmpty() && "=+-@".indexOf(v.charAt(0)) >= 0) v = "'" + v;
        return "\"" + v + "\"";
    }
}

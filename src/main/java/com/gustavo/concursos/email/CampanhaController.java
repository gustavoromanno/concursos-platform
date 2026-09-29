package com.gustavo.concursos.email;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class CampanhaController {

    private final CampanhaEmailRepository campanhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CampanhaService campanhaService;
    private final String nomeMarca;

    public CampanhaController(CampanhaEmailRepository campanhaRepository, UsuarioRepository usuarioRepository,
                              CampanhaService campanhaService,
                              @org.springframework.beans.factory.annotation.Value("${app.nome:Concursos Platform}") String nomeMarca) {
        this.nomeMarca = nomeMarca;
        this.campanhaRepository = campanhaRepository;
        this.usuarioRepository = usuarioRepository;
        this.campanhaService = campanhaService;
    }

    public record NovaCampanhaDTO(
            @NotBlank(message = "Informe o assunto") @Size(max = 150, message = "Assunto muito longo") String assunto,
            @NotBlank(message = "Escreva a mensagem") @Size(max = 10000, message = "Mensagem muito longa") String mensagem
    ) {
    }

    public record CampanhaDTO(Long id, String assunto, int destinatarios, int enviados, String status, LocalDateTime criadoEm) {
    }

    @Transactional(readOnly = true)
    @GetMapping("/admin/campanhas")
    public List<CampanhaDTO> listar() {
        return campanhaRepository.findTop20ByOrderByCriadoEmDesc().stream().map(this::paraDTO).toList();
    }

    @PostMapping("/admin/campanhas/teste")
    public ResponseEntity<Void> teste(@Valid @RequestBody NovaCampanhaDTO dto, Authentication authentication) {
        campanhaService.enviarTeste(usuarioLogado(authentication), dto.assunto().strip(), dto.mensagem());
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/admin/campanhas")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CampanhaDTO enviar(@Valid @RequestBody NovaCampanhaDTO dto) {
        int destinatarios = usuarioRepository.findByAceitaMarketingTrueOrderByCriadoEmAsc().size();
        if (destinatarios == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ninguém aceitou receber e-mails promocionais ainda.");
        }
        CampanhaEmail c = new CampanhaEmail();
        c.setAssunto(dto.assunto().strip());
        c.setMensagem(dto.mensagem());
        c.setDestinatarios(destinatarios);
        campanhaRepository.save(c);

        Long id = c.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                campanhaService.enviar(id);
            }
        });
        return paraDTO(c);
    }

    // Link do rodape do e-mail: publico, funciona sem login.
    @Transactional
    @GetMapping(value = "/publico/descadastro", produces = MediaType.TEXT_HTML_VALUE)
    public String descadastrar(@RequestParam(name = "t", defaultValue = "") String token) {
        boolean ok = !token.isBlank() && usuarioRepository.findByTokenDescadastro(token).map(u -> {
            u.setAceitaMarketing(false);
            u.setMarketingAtualizadoEm(LocalDateTime.now());
            return true;
        }).orElse(false);
        String texto = ok
                ? "Pronto: você não vai mais receber e-mails promocionais da " + nomeMarca + ". Se mudar de ideia, reative em \"Minha conta\"."
                : "Link inválido. Você pode gerenciar suas preferências em \"Minha conta\".";
        return "<!doctype html><html lang=\"pt-BR\"><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">"
                + "<title>Preferências de e-mail</title><body style=\"font-family:system-ui,sans-serif;max-width:520px;margin:12vh auto;padding:0 20px;color:#22322E\">"
                + "<h1 style=\"font-size:22px\">Preferências de e-mail</h1><p>" + texto + "</p><p><a href=\"/\">Ir para a " + nomeMarca + "</a></p></body></html>";
    }

    private CampanhaDTO paraDTO(CampanhaEmail c) {
        return new CampanhaDTO(c.getId(), c.getAssunto(), c.getDestinatarios(), c.getEnviados(), c.getStatus(), c.getCriadoEm());
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

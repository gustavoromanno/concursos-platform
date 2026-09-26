package com.gustavo.concursos.solicitacao;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * "Nao achei o conteudo que eu estudo": qualquer usuario logado pede um
 * concurso/materia; o admin acompanha a fila e responde (em /admin/**).
 */
@RestController
public class SolicitacaoConteudoController {

    static final int LIMITE_POR_DIA = 5;

    private final SolicitacaoConteudoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public SolicitacaoConteudoController(SolicitacaoConteudoRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public record NovaSolicitacaoDTO(
            @NotBlank(message = "Informe o concurso ou edital") @Size(max = 150, message = "Nome do concurso muito longo") String concurso,
            @Size(max = 150, message = "Nome do cargo muito longo") String cargo,
            @NotBlank(message = "Informe a matéria") @Size(max = 150, message = "Nome da matéria muito longo") String materia,
            @Size(max = 500, message = "Link muito longo")
            @Pattern(regexp = "^$|^https?://\\S+$", message = "O link do edital deve começar com http:// ou https://") String linkEdital,
            @Size(max = 1000, message = "Detalhes com até 1000 caracteres") String detalhes
    ) {
    }

    public record AtualizarDTO(@NotBlank String status, @Size(max = 500) String resposta) {
    }

    public record SolicitacaoDTO(Long id, String concurso, String cargo, String materia, String linkEdital,
                                 String detalhes, String status, String resposta, LocalDateTime criadoEm,
                                 String emailSolicitante) {
    }

    @Transactional
    @PostMapping("/solicitacoes")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoDTO criar(@Valid @RequestBody NovaSolicitacaoDTO dto, Authentication authentication) {
        Usuario u = usuarioLogado(authentication);
        long hoje = repository.countByUsuarioIdAndCriadoEmGreaterThanEqual(u.getId(), LocalDateTime.now().minusDays(1));
        if (hoje >= LIMITE_POR_DIA) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Você já enviou " + LIMITE_POR_DIA + " pedidos nas últimas 24 horas. Tente amanhã.");
        }
        SolicitacaoConteudo s = new SolicitacaoConteudo();
        s.setUsuario(u);
        s.setConcurso(dto.concurso().strip());
        s.setCargo(vazio(dto.cargo()));
        s.setMateria(dto.materia().strip());
        s.setLinkEdital(vazio(dto.linkEdital()));
        s.setDetalhes(vazio(dto.detalhes()));
        repository.save(s);
        return paraDTO(s, false);
    }

    @Transactional(readOnly = true)
    @GetMapping("/solicitacoes")
    public List<SolicitacaoDTO> minhas(Authentication authentication) {
        return repository.findByUsuarioIdOrderByCriadoEmDesc(usuarioLogado(authentication).getId())
                .stream().map(s -> paraDTO(s, false)).toList();
    }

    // ---- admin ----

    @Transactional(readOnly = true)
    @GetMapping("/admin/solicitacoes")
    public List<SolicitacaoDTO> todas(@RequestParam(required = false) String status) {
        List<SolicitacaoConteudo> lista = status == null || status.isBlank()
                ? repository.findAllByOrderByCriadoEmDesc()
                : repository.findByStatusOrderByCriadoEmAsc(status.toUpperCase());
        return lista.stream().map(s -> paraDTO(s, true)).toList();
    }

    @Transactional
    @PutMapping("/admin/solicitacoes/{id}")
    public SolicitacaoDTO atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarDTO dto) {
        if (!SolicitacaoConteudo.STATUS.contains(dto.status().toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido");
        }
        SolicitacaoConteudo s = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        s.setStatus(dto.status().toUpperCase());
        s.setResposta(vazio(dto.resposta()));
        s.setAtualizadoEm(LocalDateTime.now());
        return paraDTO(s, true);
    }

    private SolicitacaoDTO paraDTO(SolicitacaoConteudo s, boolean admin) {
        return new SolicitacaoDTO(s.getId(), s.getConcurso(), s.getCargo(), s.getMateria(), s.getLinkEdital(),
                s.getDetalhes(), s.getStatus(), s.getResposta(), s.getCriadoEm(),
                admin && s.getUsuario() != null ? s.getUsuario().getEmail() : null);
    }

    private static String vazio(String s) {
        return s == null || s.isBlank() ? null : s.strip();
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

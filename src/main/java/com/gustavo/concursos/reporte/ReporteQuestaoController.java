package com.gustavo.concursos.reporte;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

// Aluno reporta erro numa questao; o admin acompanha a fila (em /admin/**).
@RestController
public class ReporteQuestaoController {

    static final int LIMITE_POR_DIA = 10;

    private final ReporteQuestaoRepository repository;
    private final QuestaoRepository questaoRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteQuestaoController(ReporteQuestaoRepository repository, QuestaoRepository questaoRepository,
                                    UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.questaoRepository = questaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public record NovoReporteDTO(@NotBlank(message = "Escolha o motivo") String motivo,
                                 @Size(max = 1000, message = "Descrição com até 1000 caracteres") String descricao) {
    }

    public record AtualizarDTO(@NotBlank String status, @Size(max = 500) String resposta) {
    }

    public record ReporteDTO(Long id, Long questaoId, String enunciado, String motivo, String descricao,
                             String status, String resposta, String email, LocalDateTime criadoEm) {
    }

    @Transactional
    @PostMapping("/questoes/{id}/reportes")
    @ResponseStatus(HttpStatus.CREATED)
    public void reportar(@PathVariable Long id, @Valid @RequestBody NovoReporteDTO dto, Authentication authentication) {
        String motivo = dto.motivo().toUpperCase();
        if (!ReporteQuestao.MOTIVOS.contains(motivo)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Motivo inválido");
        Usuario u = usuarioLogado(authentication);
        if (repository.countByUsuarioIdAndCriadoEmGreaterThanEqual(u.getId(), LocalDateTime.now().minusDays(1)) >= LIMITE_POR_DIA) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Limite de " + LIMITE_POR_DIA + " avisos por dia atingido. Obrigado pela ajuda!");
        }
        ReporteQuestao r = new ReporteQuestao();
        r.setQuestao(questaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questão não encontrada")));
        r.setUsuario(u);
        r.setMotivo(motivo);
        r.setDescricao(dto.descricao() == null || dto.descricao().isBlank() ? null : dto.descricao().strip());
        repository.save(r);
    }

    @Transactional(readOnly = true)
    @GetMapping("/admin/reportes")
    public List<ReporteDTO> fila(@RequestParam(required = false) String status) {
        List<ReporteQuestao> lista = status == null || status.isBlank()
                ? repository.findTop100ByOrderByCriadoEmDesc()
                : repository.findByStatusOrderByCriadoEmAsc(status.toUpperCase());
        return lista.stream().map(this::paraDTO).toList();
    }

    @Transactional
    @PutMapping("/admin/reportes/{id}")
    public ReporteDTO atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarDTO dto) {
        String status = dto.status().toUpperCase();
        if (!ReporteQuestao.STATUS.contains(status)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido");
        ReporteQuestao r = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aviso não encontrado"));
        r.setStatus(status);
        r.setResposta(dto.resposta() == null || dto.resposta().isBlank() ? null : dto.resposta().strip());
        r.setAtualizadoEm(LocalDateTime.now());
        return paraDTO(r);
    }

    private ReporteDTO paraDTO(ReporteQuestao r) {
        String enunciado = r.getQuestao().getEnunciado();
        return new ReporteDTO(r.getId(), r.getQuestao().getId(),
                enunciado.length() > 160 ? enunciado.substring(0, 160) + "…" : enunciado,
                r.getMotivo(), r.getDescricao(), r.getStatus(), r.getResposta(),
                r.getUsuario() != null ? r.getUsuario().getEmail() : null, r.getCriadoEm());
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

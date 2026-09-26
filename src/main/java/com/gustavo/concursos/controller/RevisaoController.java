package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.QuestaoResponseDTO;
import com.gustavo.concursos.dto.ResumoRevisaoDTO;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.RevisaoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@com.gustavo.concursos.pro.RequerPro("A revisão espaçada")
@RequestMapping("/revisoes")
public class RevisaoController {

    private final RevisaoRepository revisaoRepository;
    private final UsuarioRepository usuarioRepository;

    public RevisaoController(RevisaoRepository revisaoRepository, UsuarioRepository usuarioRepository) {
        this.revisaoRepository = revisaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // GET /revisoes/hoje — questoes agendadas para hoje ou atrasadas.
    @Transactional(readOnly = true)
    @GetMapping("/hoje")
    public List<QuestaoResponseDTO> hoje(Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        return revisaoRepository
                .findByUsuarioIdAndProximaRevisaoLessThanEqualOrderByProximaRevisaoAsc(
                        usuario.getId(), LocalDate.now())
                .stream()
                .map(r -> QuestaoResponseDTO.fromEntity(r.getQuestao()))
                .toList();
    }

    // GET /revisoes/resumo — quantas para hoje e como fica a proxima semana.
    @Transactional(readOnly = true)
    @GetMapping("/resumo")
    public ResumoRevisaoDTO resumo(Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        LocalDate hoje = LocalDate.now();

        List<ResumoRevisaoDTO.DiaAgendaDTO> proximos = revisaoRepository
                .agenda(usuario.getId(), hoje, hoje.plusDays(7))
                .stream()
                .map(a -> new ResumoRevisaoDTO.DiaAgendaDTO(a.getDia(), a.getTotal()))
                .toList();

        return new ResumoRevisaoDTO(
                revisaoRepository.countByUsuarioIdAndProximaRevisaoLessThanEqual(usuario.getId(), hoje),
                revisaoRepository.count(),
                proximos
        );
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

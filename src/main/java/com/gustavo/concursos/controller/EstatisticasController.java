package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.DashboardDTO;
import com.gustavo.concursos.dto.DesempenhoAssuntoDTO;
import com.gustavo.concursos.dto.DesempenhoDisciplinaDTO;
import com.gustavo.concursos.dto.EvolucaoDiariaDTO;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.RespostaRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/estatisticas")
public class EstatisticasController {

    private final RespostaRepository respostaRepository;
    private final UsuarioRepository usuarioRepository;

    public EstatisticasController(
            RespostaRepository respostaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.respostaRepository = respostaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // GET /estatisticas — numeros gerais + desempenho por disciplina.
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<DashboardDTO> dashboard(Authentication authentication) {
        Long usuarioId = usuarioLogadoId(authentication);

        RespostaRepository.TotalGeral geral = respostaRepository.totalGeral(usuarioId);
        long total = geral == null ? 0 : geral.getTotal();
        long acertos = geral == null ? 0 : geral.getAcertos();

        List<DesempenhoDisciplinaDTO> porDisciplina = respostaRepository
                .estatisticasPorDisciplina(usuarioId)
                .stream()
                .map(e -> DesempenhoDisciplinaDTO.de(e.getDisciplina(), e.getTotal(), e.getAcertos()))
                .toList();

        return ResponseEntity.ok(DashboardDTO.de(total, acertos, porDisciplina));
    }

    // GET /estatisticas/assuntos — pontos fracos primeiro (Sprint 7).
    @Transactional(readOnly = true)
    @GetMapping("/assuntos")
    public ResponseEntity<List<DesempenhoAssuntoDTO>> porAssunto(Authentication authentication) {
        List<DesempenhoAssuntoDTO> lista = respostaRepository
                .estatisticasPorAssunto(usuarioLogadoId(authentication))
                .stream()
                .map(e -> DesempenhoAssuntoDTO.de(
                        e.getAssunto(), e.getDisciplina(), e.getTotal(), e.getAcertos()))
                .toList();
        return ResponseEntity.ok(lista);
    }

    // GET /estatisticas/evolucao?dias=30
    @Transactional(readOnly = true)
    @GetMapping("/evolucao")
    public ResponseEntity<List<EvolucaoDiariaDTO>> evolucao(
            @RequestParam(defaultValue = "30") int dias,
            Authentication authentication
    ) {
        Long usuarioId = usuarioLogadoId(authentication);
        LocalDate desde = LocalDate.now().minusDays(Math.max(dias, 1));

        List<EvolucaoDiariaDTO> serie = respostaRepository
                .evolucaoDiaria(usuarioId, desde)
                .stream()
                .map(e -> EvolucaoDiariaDTO.de(e.getDia(), e.getTotal(), e.getAcertos()))
                .toList();

        return ResponseEntity.ok(serie);
    }

    private Long usuarioLogadoId(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
        return usuario.getId();
    }
}

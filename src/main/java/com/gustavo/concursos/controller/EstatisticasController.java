package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.DashboardDTO;
import com.gustavo.concursos.dto.DesempenhoDisciplinaDTO;
import com.gustavo.concursos.dto.EvolucaoDiariaDTO;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.RespostaRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    // GET /estatisticas
    // Numeros gerais do usuario logado + desempenho por disciplina,
    // ordenado da disciplina mais respondida para a menos respondida.
    @GetMapping
    public ResponseEntity<DashboardDTO> dashboard(Authentication authentication) {
        Long usuarioId = usuarioLogadoId(authentication);

        RespostaRepository.TotalGeral geral = respostaRepository.totalGeral(usuarioId);

        // Usuario que ainda nao respondeu nada: devolve zeros em vez de erro.
        long total = geral == null ? 0 : geral.getTotal();
        long acertos = geral == null ? 0 : geral.getAcertos();

        List<DesempenhoDisciplinaDTO> porDisciplina = respostaRepository
                .estatisticasPorDisciplina(usuarioId)
                .stream()
                .map(e -> DesempenhoDisciplinaDTO.de(e.getDisciplina(), e.getTotal(), e.getAcertos()))
                .toList();

        return ResponseEntity.ok(DashboardDTO.de(total, acertos, porDisciplina));
    }

    // GET /estatisticas/evolucao?dias=30
    // Serie temporal: quantas questoes por dia e quantas acertou.
    // Dias sem resposta simplesmente nao aparecem na lista.
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

    // O email vem do token JWT; daqui tiramos o id para as queries.
    private Long usuarioLogadoId(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
        return usuario.getId();
    }
}

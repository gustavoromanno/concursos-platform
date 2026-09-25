package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.DiaEstudoDTO;
import com.gustavo.concursos.dto.EngajamentoDTO;
import com.gustavo.concursos.dto.MetaDTO;
import com.gustavo.concursos.entity.MetaEstudo;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.MetaEstudoRepository;
import com.gustavo.concursos.repository.RespostaRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.gustavo.concursos.service.EngajamentoCalculadora.*;

@RestController
@RequestMapping("/engajamento")
public class EngajamentoController {

    private static final int META_PADRAO = 10;

    private final RespostaRepository respostaRepository;
    private final MetaEstudoRepository metaRepository;
    private final UsuarioRepository usuarioRepository;

    public EngajamentoController(
            RespostaRepository respostaRepository,
            MetaEstudoRepository metaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.respostaRepository = respostaRepository;
        this.metaRepository = metaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // GET /engajamento — meta, progresso de hoje e ofensiva.
    @Transactional(readOnly = true)
    @GetMapping
    public EngajamentoDTO resumo(Authentication authentication) {
        Usuario usuario = usuarioLogado(authentication);
        int meta = metaDoUsuario(usuario.getId());

        Map<LocalDate, long[]> porDia = carregarDias(usuario.getId(), 365);

        LocalDate hoje = LocalDate.now();
        long hojeTotal = porDia.containsKey(hoje) ? porDia.get(hoje)[0] : 0;

        return new EngajamentoDTO(
                meta,
                hojeTotal,
                hojeTotal >= meta,
                ofensivaAtual(porDia, meta, hoje),
                melhorOfensiva(porDia, meta),
                (int) porDia.keySet().stream()
                        .filter(d -> d.getYear() == hoje.getYear() && d.getMonth() == hoje.getMonth())
                        .count(),
                porDia.size()
        );
    }

    // PUT /engajamento/meta — define quantas questoes por dia o usuario quer fazer.
    @Transactional
    @PutMapping("/meta")
    public ResponseEntity<EngajamentoDTO> definirMeta(
            @Valid @RequestBody MetaDTO request,
            Authentication authentication
    ) {
        Usuario usuario = usuarioLogado(authentication);

        MetaEstudo meta = metaRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    MetaEstudo nova = new MetaEstudo();
                    nova.setUsuario(usuario);
                    return nova;
                });

        meta.setQuestoesPorDia(request.questoesPorDia());
        meta.setAtualizadoEm(java.time.LocalDateTime.now());
        metaRepository.save(meta);

        return ResponseEntity.ok(resumo(authentication));
    }

    // GET /engajamento/mapa?dias=365 — dados do mapa de estudo (heatmap).
    // Devolve TODOS os dias do intervalo, inclusive os sem estudo, para o
    // frontend poder desenhar a grade completa sem precisar calcular datas.
    @Transactional(readOnly = true)
    @GetMapping("/mapa")
    public List<DiaEstudoDTO> mapa(
            @RequestParam(defaultValue = "365") int dias,
            Authentication authentication
    ) {
        Usuario usuario = usuarioLogado(authentication);
        int meta = metaDoUsuario(usuario.getId());
        int janela = Math.min(Math.max(dias, 1), 366);

        Map<LocalDate, long[]> porDia = carregarDias(usuario.getId(), janela);

        List<DiaEstudoDTO> resultado = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        for (int i = janela - 1; i >= 0; i--) {
            LocalDate dia = hoje.minusDays(i);
            long[] valores = porDia.getOrDefault(dia, new long[]{0, 0});
            resultado.add(new DiaEstudoDTO(dia, valores[0], valores[1], nivel(valores[0], meta)));
        }
        return resultado;
    }

    // --- apoio ---

    // Carrega o historico diario em um mapa data -> [total, acertos].
    private Map<LocalDate, long[]> carregarDias(Long usuarioId, int dias) {
        Map<LocalDate, long[]> porDia = new HashMap<>();
        respostaRepository.evolucaoDiaria(usuarioId, LocalDate.now().minusDays(dias))
                .forEach(e -> porDia.put(e.getDia(), new long[]{e.getTotal(), e.getAcertos()}));
        return porDia;
    }

    private int metaDoUsuario(Long usuarioId) {
        return metaRepository.findByUsuarioId(usuarioId)
                .map(MetaEstudo::getQuestoesPorDia)
                .orElse(META_PADRAO);
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

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

    private boolean bateuMeta(Map<LocalDate, long[]> porDia, LocalDate dia, int meta) {
        long[] valores = porDia.get(dia);
        return valores != null && valores[0] >= meta;
    }

    // Ofensiva atual: dias consecutivos batendo a meta, contando para tras.
    //
    // Se a meta de HOJE ainda nao foi batida, a contagem comeca em ontem — do
    // contrario a ofensiva apareceria zerada toda manha, antes de o usuario
    // sentar para estudar.
    private int ofensivaAtual(Map<LocalDate, long[]> porDia, int meta, LocalDate hoje) {
        LocalDate cursor = bateuMeta(porDia, hoje, meta) ? hoje : hoje.minusDays(1);

        int streak = 0;
        while (bateuMeta(porDia, cursor, meta)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    // Maior sequencia ja alcancada dentro da janela analisada.
    private int melhorOfensiva(Map<LocalDate, long[]> porDia, int meta) {
        List<LocalDate> diasValidos = porDia.entrySet().stream()
                .filter(e -> e.getValue()[0] >= meta)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        int melhor = 0;
        int atual = 0;
        LocalDate anterior = null;

        for (LocalDate dia : diasValidos) {
            atual = (anterior != null && dia.equals(anterior.plusDays(1))) ? atual + 1 : 1;
            melhor = Math.max(melhor, atual);
            anterior = dia;
        }
        return melhor;
    }

    // Intensidade do quadradinho no mapa, de 0 a 4.
    private int nivel(long respondidas, int meta) {
        if (respondidas == 0) return 0;
        double proporcao = (double) respondidas / meta;
        if (proporcao < 0.5) return 1;
        if (proporcao < 1.0) return 2;
        if (proporcao < 2.0) return 3;
        return 4;
    }

    private Usuario usuarioLogado(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));
    }
}

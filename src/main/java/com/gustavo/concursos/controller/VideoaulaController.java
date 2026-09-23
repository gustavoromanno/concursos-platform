package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.NovaVideoaulaDTO;
import com.gustavo.concursos.dto.VideoaulaDTO;
import com.gustavo.concursos.entity.Assunto;
import com.gustavo.concursos.entity.Disciplina;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.entity.Videoaula;
import com.gustavo.concursos.repository.AssuntoRepository;
import com.gustavo.concursos.repository.DisciplinaRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import com.gustavo.concursos.repository.VideoaulaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/videoaulas")
public class VideoaulaController {

    // Cobre os formatos comuns: youtube.com/watch?v=ID, youtu.be/ID,
    // youtube.com/embed/ID e o proprio ID solto.
    private static final Pattern YOUTUBE_ID = Pattern.compile(
            "(?:v=|youtu\\.be/|embed/|shorts/)([A-Za-z0-9_-]{11})|^([A-Za-z0-9_-]{11})$"
    );

    private final VideoaulaRepository videoaulaRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final AssuntoRepository assuntoRepository;
    private final UsuarioRepository usuarioRepository;

    public VideoaulaController(
            VideoaulaRepository videoaulaRepository,
            DisciplinaRepository disciplinaRepository,
            AssuntoRepository assuntoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.videoaulaRepository = videoaulaRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.assuntoRepository = assuntoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // GET /videoaulas?disciplinaId=1&assuntoId=2 — ambos opcionais.
    @Transactional(readOnly = true)
    @GetMapping
    public List<VideoaulaDTO> listar(
            @RequestParam(required = false) Long disciplinaId,
            @RequestParam(required = false) Long assuntoId
    ) {
        return videoaulaRepository.buscar(disciplinaId, assuntoId).stream()
                .map(VideoaulaDTO::fromEntity)
                .toList();
    }

    // GET /videoaulas/sugestoes — o ponto alto do Sprint 7.
    // Cruza os erros do usuario com os assuntos das videoaulas e devolve
    // primeiro o conteudo dos temas em que ele mais erra.
    @Transactional(readOnly = true)
    @GetMapping("/sugestoes")
    public List<VideoaulaDTO> sugestoes(
            @RequestParam(defaultValue = "6") int limite,
            Authentication authentication
    ) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));

        return videoaulaRepository.sugestoesPorErros(usuario.getId(), Math.max(1, limite)).stream()
                .map(VideoaulaDTO::fromEntity)
                .toList();
    }

    // GET /videoaulas/questao/{id} — videos do assunto daquela questao.
    // Usado para sugerir estudo logo apos o usuario errar.
    @Transactional(readOnly = true)
    @GetMapping("/questao/{questaoId}")
    public List<VideoaulaDTO> porQuestao(@PathVariable Long questaoId) {
        return videoaulaRepository.porQuestao(questaoId).stream()
                .map(VideoaulaDTO::fromEntity)
                .toList();
    }

    // POST /videoaulas — cadastra um video. Aceita URL completa ou so o ID.
    @Transactional
    @PostMapping
    public ResponseEntity<VideoaulaDTO> criar(@Valid @RequestBody NovaVideoaulaDTO request) {
        String youtubeId = extrairYoutubeId(request.urlOuId());

        Disciplina disciplina = disciplinaRepository.findById(request.disciplinaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Disciplina nao encontrada"));

        Assunto assunto = null;
        if (request.assuntoId() != null) {
            assunto = assuntoRepository.findById(request.assuntoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assunto nao encontrado"));
        }

        Videoaula video = new Videoaula();
        video.setTitulo(request.titulo());
        video.setYoutubeId(youtubeId);
        video.setCanal(request.canal());
        video.setDuracaoMinutos(request.duracaoMinutos());
        video.setDisciplina(disciplina);
        video.setAssunto(assunto);
        videoaulaRepository.save(video);

        return ResponseEntity.status(HttpStatus.CREATED).body(VideoaulaDTO.fromEntity(video));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        videoaulaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String extrairYoutubeId(String entrada) {
        Matcher m = YOUTUBE_ID.matcher(entrada.trim());
        if (m.find()) {
            return m.group(1) != null ? m.group(1) : m.group(2);
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Nao consegui identificar o ID do video nessa URL"
        );
    }
}

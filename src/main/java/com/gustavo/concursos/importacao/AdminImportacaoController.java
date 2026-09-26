package com.gustavo.concursos.importacao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Importacao de provas antigas e fila de revisao das questoes geradas.
 * Tudo sob /admin/** — restrito ao papel ADMIN no SecurityConfig.
 */
@RestController
@RequestMapping("/admin")
public class AdminImportacaoController {

    private final ImportacaoService importacaoService;
    private final PublicacaoRascunhoService publicacaoService;
    private final ImportacaoProvaRepository importacaoRepository;
    private final QuestaoBaseRepository baseRepository;
    private final QuestaoRascunhoRepository rascunhoRepository;
    private final ObjectMapper json;

    public AdminImportacaoController(
            ImportacaoService importacaoService,
            PublicacaoRascunhoService publicacaoService,
            ImportacaoProvaRepository importacaoRepository,
            QuestaoBaseRepository baseRepository,
            QuestaoRascunhoRepository rascunhoRepository,
            ObjectMapper json
    ) {
        this.importacaoService = importacaoService;
        this.publicacaoService = publicacaoService;
        this.importacaoRepository = importacaoRepository;
        this.baseRepository = baseRepository;
        this.rascunhoRepository = rascunhoRepository;
        this.json = json;
    }

    // ---------------- DTOs ----------------

    public record ImportacaoDTO(
            Long id, String status, String etapa, int progresso,
            Long concursoId, String concurso, String cargo,
            String arquivoProva, String arquivoGabarito, int ineditasPorQuestao,
            long questoesLidas, long pendentes, long aprovadas, long descartadas,
            long tokensEntrada, long tokensSaida, String mensagemErro,
            LocalDateTime criadoEm, LocalDateTime atualizadoEm
    ) {
    }

    public record AlternativaDTO(String texto, boolean correta) {
    }

    public record RascunhoDTO(
            Long id, String tipo, String disciplina, String assunto, String enunciado,
            List<AlternativaDTO> alternativas, String explicacao, String status,
            Integer baseNumero, String baseEnunciado
    ) {
    }

    public record EditarRascunhoDTO(
            String tipo, String disciplina, String assunto, String enunciado,
            List<AlternativaDTO> alternativas, String explicacao
    ) {
    }

    // ---------------- importacoes ----------------

    @Transactional
    @PostMapping(value = "/importacoes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportacaoDTO> importar(
            @RequestParam("prova") MultipartFile prova,
            @RequestParam("gabarito") MultipartFile gabarito,
            @RequestParam(value = "cargo", required = false) String cargo,
            @RequestParam(value = "ineditas", defaultValue = "1") int ineditas
    ) {
        ImportacaoProva imp = importacaoService.iniciar(prova, gabarito, cargo, ineditas);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(paraDTO(imp));
    }

    @Transactional(readOnly = true)
    @GetMapping("/importacoes")
    public List<ImportacaoDTO> listar() {
        return importacaoRepository.findAllByOrderByCriadoEmDesc().stream().map(this::paraDTO).toList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/importacoes/{id}")
    public ImportacaoDTO detalhar(@PathVariable Long id) {
        return paraDTO(importacaoService.buscar(id));
    }

    @Transactional
    @PostMapping("/importacoes/{id}/reprocessar")
    public ImportacaoDTO reprocessar(@PathVariable Long id) {
        return paraDTO(importacaoService.reprocessar(id));
    }

    @DeleteMapping("/importacoes/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        importacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- fila de revisao ----------------

    @Transactional(readOnly = true)
    @GetMapping("/importacoes/{id}/rascunhos")
    public List<RascunhoDTO> rascunhos(@PathVariable Long id,
                                      @RequestParam(defaultValue = QuestaoRascunho.PENDENTE) String status) {
        return rascunhoRepository.findByImportacaoIdAndStatusOrderByIdAsc(id, status.toUpperCase())
                .stream().map(this::paraDTO).toList();
    }

    @PostMapping("/importacoes/{id}/aprovar-todos")
    public Map<String, Integer> aprovarTodos(@PathVariable Long id) {
        return Map.of("aprovadas", publicacaoService.aprovarTodos(id));
    }

    @PostMapping("/rascunhos/{id}/aprovar")
    public Map<String, Long> aprovar(@PathVariable Long id) {
        return Map.of("questaoId", publicacaoService.aprovar(id).getId());
    }

    @PostMapping("/rascunhos/{id}/descartar")
    public ResponseEntity<Void> descartar(@PathVariable Long id) {
        publicacaoService.descartar(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PutMapping("/rascunhos/{id}")
    public RascunhoDTO editar(@PathVariable Long id, @RequestBody EditarRascunhoDTO dto) {
        List<ValidadorQuestaoGerada.Alternativa> alternativas = dto.alternativas() == null ? List.of()
                : dto.alternativas().stream().map(a -> new ValidadorQuestaoGerada.Alternativa(a.texto(), a.correta())).toList();
        QuestaoRascunho r = importacaoService.editarRascunho(id, new ValidadorQuestaoGerada.Questao(
                dto.tipo(), dto.disciplina(), dto.assunto(), dto.enunciado(), alternativas, dto.explicacao()));
        return paraDTO(r);
    }

    // ---------------- conversoes ----------------

    private ImportacaoDTO paraDTO(ImportacaoProva i) {
        Long id = i.getId();
        return new ImportacaoDTO(
                id, i.getStatus(), i.getEtapa(), i.getProgresso(),
                i.getConcurso() != null ? i.getConcurso().getId() : null,
                i.getConcurso() != null ? i.getConcurso().getNome() : null,
                i.getCargo() != null ? i.getCargo().getNome() : null,
                i.getNomeArquivoProva(), i.getNomeArquivoGabarito(), i.getIneditasPorQuestao(),
                baseRepository.countByImportacaoId(id),
                rascunhoRepository.countByImportacaoIdAndStatus(id, QuestaoRascunho.PENDENTE),
                rascunhoRepository.countByImportacaoIdAndStatus(id, QuestaoRascunho.APROVADA),
                rascunhoRepository.countByImportacaoIdAndStatus(id, QuestaoRascunho.DESCARTADA),
                i.getTokensEntrada(), i.getTokensSaida(), i.getMensagemErro(),
                i.getCriadoEm(), i.getAtualizadoEm());
    }

    private RascunhoDTO paraDTO(QuestaoRascunho r) {
        List<AlternativaDTO> alternativas;
        try {
            alternativas = json.readValue(r.getAlternativas(), new TypeReference<List<AlternativaDTO>>() {});
        } catch (JsonProcessingException e) {
            alternativas = List.of();
        }
        return new RascunhoDTO(
                r.getId(), r.getTipo(), r.getDisciplina(), r.getAssunto(), r.getEnunciado(),
                alternativas, r.getExplicacao(), r.getStatus(),
                r.getBase() != null ? r.getBase().getNumero() : null,
                r.getBase() != null ? r.getBase().getEnunciado() : null);
    }
}

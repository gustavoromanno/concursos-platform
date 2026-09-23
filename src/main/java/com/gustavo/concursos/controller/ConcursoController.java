package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.*;
import com.gustavo.concursos.entity.*;
import com.gustavo.concursos.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
public class ConcursoController {

    private final ConcursoRepository concursoRepository;
    private final ProvaRepository provaRepository;
    private final BancaRepository bancaRepository;
    private final ConcursoCargoRepository cargoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public ConcursoController(
            ConcursoRepository concursoRepository,
            ProvaRepository provaRepository,
            BancaRepository bancaRepository,
            ConcursoCargoRepository cargoRepository,
            DisciplinaRepository disciplinaRepository
    ) {
        this.concursoRepository = concursoRepository;
        this.provaRepository = provaRepository;
        this.bancaRepository = bancaRepository;
        this.cargoRepository = cargoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/concursos")
    public List<ConcursoDTO> listar(@RequestParam(required = false) String situacao) {
        List<Concurso> concursos = (situacao == null || situacao.isBlank())
                ? concursoRepository.findAllByOrderByAnoDescNomeAsc()
                : concursoRepository.findBySituacaoOrderByAnoDescNomeAsc(situacao);
        return concursos.stream().map(this::paraDTO).toList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/concursos/{id}")
    public ConcursoDTO buscar(@PathVariable Long id) {
        return paraDTO(carregar(id));
    }

    @Transactional
    @PostMapping("/concursos")
    public ResponseEntity<ConcursoDTO> criar(@Valid @RequestBody NovoConcursoDTO request) {
        Concurso c = new Concurso();
        c.setNome(request.nome().trim());
        c.setOrgao(request.orgao());
        c.setAno(request.ano());
        c.setVagas(request.vagas());
        c.setInscricoesAte(request.inscricoesAte());
        if (request.situacao() != null && !request.situacao().isBlank()) c.setSituacao(request.situacao());
        if (request.bancaId() != null) {
            c.setBanca(bancaRepository.findById(request.bancaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Banca nao encontrada")));
        }
        concursoRepository.save(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(paraDTO(c));
    }

    @Transactional
    @DeleteMapping("/concursos/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        concursoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- cronograma ---

    @Transactional
    @PostMapping("/concursos/{id}/etapas")
    public ResponseEntity<ConcursoDTO> adicionarEtapa(
            @PathVariable Long id,
            @Valid @RequestBody NovaEtapaDTO request
    ) {
        Concurso c = carregar(id);
        ConcursoEtapa etapa = new ConcursoEtapa();
        etapa.setConcurso(c);
        etapa.setNome(request.nome().trim());
        etapa.setDataPrevista(request.dataPrevista());
        if (request.status() != null && !request.status().isBlank()) etapa.setStatus(request.status());
        etapa.setOrdem(request.ordem() != null ? request.ordem() : c.getEtapas().size() + 1);
        c.getEtapas().add(etapa);
        concursoRepository.save(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(paraDTO(c));
    }

    // --- cargos e conteúdo programático ---

    @Transactional
    @PostMapping("/concursos/{id}/cargos")
    public ResponseEntity<ConcursoDTO> adicionarCargo(
            @PathVariable Long id,
            @Valid @RequestBody NovoCargoDTO request
    ) {
        Concurso c = carregar(id);

        ConcursoCargo cargo = new ConcursoCargo();
        cargo.setConcurso(c);
        cargo.setNome(request.nome().trim());
        cargo.setNivel(request.nivel());
        cargo.setVagas(request.vagas());
        cargo.setCadastroReserva(request.cadastroReserva());
        cargo.setSalario(request.salario());
        cargo.setOrdem(c.getCargos().size() + 1);

        // Conteúdo programático: cada disciplina informada vira uma linha,
        // com o total de tópicos contado a partir dos assuntos cadastrados.
        if (request.disciplinaIds() != null) {
            int ordem = 1;
            for (Long disciplinaId : request.disciplinaIds()) {
                Disciplina d = disciplinaRepository.findById(disciplinaId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "Disciplina nao encontrada: " + disciplinaId));
                CargoDisciplina cd = new CargoDisciplina();
                cd.setCargo(cargo);
                cd.setDisciplina(d);
                cd.setOrdem(ordem++);
                cargo.getConteudo().add(cd);
            }
        }

        c.getCargos().add(cargo);
        concursoRepository.save(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(paraDTO(c));
    }

    @Transactional
    @DeleteMapping("/cargos/{id}")
    public ResponseEntity<Void> removerCargo(@PathVariable Long id) {
        cargoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- prova ---

    @Transactional(readOnly = true)
    @GetMapping("/provas/{id}")
    public ProvaDTO abrirProva(@PathVariable Long id) {
        Prova prova = provaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova nao encontrada"));
        Concurso c = prova.getConcurso();

        List<ProvaDTO.ItemProvaDTO> itens = prova.getQuestoes().stream()
                .map(pq -> new ProvaDTO.ItemProvaDTO(pq.getNumero(), QuestaoResponseDTO.fromEntity(pq.getQuestao())))
                .toList();

        return new ProvaDTO(
                prova.getId(), c.getNome(), c.getOrgao(),
                c.getBanca() != null ? c.getBanca().getNome() : null,
                c.getAno(), prova.getCargo(), prova.getNivel(), itens.size(), itens
        );
    }

    // --- apoio ---

    private Concurso carregar(Long id) {
        return concursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Concurso nao encontrado"));
    }

    private ConcursoDTO paraDTO(Concurso c) {
        List<ConcursoDTO.ProvaResumoDTO> provas = c.getProvas().stream()
                .map(p -> new ConcursoDTO.ProvaResumoDTO(
                        p.getId(), p.getCargo(), p.getNivel(), p.getAplicadaEm(), p.getQuestoes().size()))
                .toList();

        List<ConcursoDTO.EtapaResumoDTO> etapas = c.getEtapas().stream()
                .map(e -> new ConcursoDTO.EtapaResumoDTO(
                        e.getId(), e.getNome(), e.getDataPrevista(), e.getStatus(),
                        e.getDataPrevista() == null ? null
                                : (int) ChronoUnit.DAYS.between(LocalDate.now(), e.getDataPrevista())))
                .toList();

        List<CargoDTO> cargos = c.getCargos().stream()
                .map(cg -> new CargoDTO(
                        cg.getId(), cg.getNome(), cg.getNivel(), cg.getVagas(),
                        cg.getCadastroReserva(), cg.getSalario(),
                        cg.getConteudo().stream()
                                .map(cd -> new CargoDTO.ConteudoDTO(
                                        cd.getDisciplina().getNome(), cd.getTotalTopicos()))
                                .toList()))
                .toList();

        return new ConcursoDTO(
                c.getId(), c.getNome(), c.getOrgao(),
                c.getBanca() != null ? c.getBanca().getNome() : null,
                c.getAno(), c.getSituacao(), c.getVagas(),
                c.getInscricoesDe(), c.getInscricoesAte(), c.getTaxa(),
                c.getEditalUrl(), c.getObservacoes(),
                provas, etapas, cargos
        );
    }
}

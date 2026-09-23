package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.AssuntoDTO;
import com.gustavo.concursos.dto.DisciplinaDTO;
import com.gustavo.concursos.entity.Banca;
import com.gustavo.concursos.entity.Disciplina;
import com.gustavo.concursos.repository.AssuntoRepository;
import com.gustavo.concursos.repository.BancaRepository;
import com.gustavo.concursos.repository.DisciplinaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Endpoints de apoio: alimentam os filtros da interface.
@RestController
public class CatalogoController {

    private final DisciplinaRepository disciplinaRepository;
    private final AssuntoRepository assuntoRepository;
    private final BancaRepository bancaRepository;

    public CatalogoController(
            DisciplinaRepository disciplinaRepository,
            AssuntoRepository assuntoRepository,
            BancaRepository bancaRepository
    ) {
        this.disciplinaRepository = disciplinaRepository;
        this.assuntoRepository = assuntoRepository;
        this.bancaRepository = bancaRepository;
    }

    // GET /disciplinas — cada disciplina com seus assuntos aninhados.
    //
    // Antes: uma query por disciplina para buscar os assuntos raiz, mais uma por
    // assunto para buscar os subassuntos. Agora: duas queries no total, e a
    // arvore e montada em memoria.
    @Transactional(readOnly = true)
    @GetMapping("/disciplinas")
    public List<DisciplinaDTO> disciplinas() {
        List<Disciplina> todasDisciplinas = disciplinaRepository.findAll();
        List<AssuntoRepository.AssuntoPlano> todosAssuntos = assuntoRepository.listarTodosPlano();

        // Agrupa os filhos pelo id do pai (chave null = assunto de primeiro nivel).
        Map<Long, List<AssuntoRepository.AssuntoPlano>> porPai = new HashMap<>();
        for (var a : todosAssuntos) {
            porPai.computeIfAbsent(a.getPaiId(), k -> new ArrayList<>()).add(a);
        }

        List<DisciplinaDTO> resultado = new ArrayList<>();
        for (Disciplina d : todasDisciplinas) {
            List<AssuntoDTO> raizes = porPai.getOrDefault(null, List.of()).stream()
                    .filter(a -> a.getDisciplinaId().equals(d.getId()))
                    .map(a -> montarArvore(a, porPai))
                    .toList();
            resultado.add(new DisciplinaDTO(d.getId(), d.getNome(), raizes));
        }
        return resultado;
    }

    private AssuntoDTO montarArvore(
            AssuntoRepository.AssuntoPlano assunto,
            Map<Long, List<AssuntoRepository.AssuntoPlano>> porPai
    ) {
        List<AssuntoDTO> filhos = porPai.getOrDefault(assunto.getId(), List.of()).stream()
                .map(f -> montarArvore(f, porPai))
                .toList();
        return new AssuntoDTO(assunto.getId(), assunto.getNome(), filhos);
    }

    // GET /disciplinas/{id}/assuntos — lista plana, util para um select simples.
    @Transactional(readOnly = true)
    @GetMapping("/disciplinas/{id}/assuntos")
    public List<AssuntoDTO> assuntos(@PathVariable Long id) {
        return assuntoRepository.findByDisciplinaIdOrderByNomeAsc(id).stream()
                .map(a -> new AssuntoDTO(a.getId(), a.getNome(), List.of()))
                .toList();
    }

    @GetMapping("/bancas")
    public List<Banca> bancas() {
        return bancaRepository.findAll();
    }
}

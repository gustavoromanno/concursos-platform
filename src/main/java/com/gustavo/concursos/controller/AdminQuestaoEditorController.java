package com.gustavo.concursos.controller;

import com.gustavo.concursos.entity.Alternativa;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.repository.QuestaoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Editor de questoes (so ADMIN, via /admin/**): corrige enunciado, explicacao,
 * texto das alternativas e o gabarito.
 *
 * Nao cria nem remove alternativas: as respostas ja dadas apontam para elas,
 * e a quantidade precisa continuar a mesma para o historico fazer sentido.
 */
@RestController
@RequestMapping("/admin/questoes")
public class AdminQuestaoEditorController {

    private final QuestaoRepository questaoRepository;

    public AdminQuestaoEditorController(QuestaoRepository questaoRepository) {
        this.questaoRepository = questaoRepository;
    }

    public record AlternativaDTO(@NotNull Long id, String texto, boolean correta) {
    }

    public record EdicaoDTO(@NotBlank(message = "O enunciado não pode ficar vazio") String enunciado,
                            String explicacao,
                            @NotNull List<AlternativaDTO> alternativas) {
    }

    public record QuestaoEdicaoDTO(Long id, String enunciado, String explicacao, String tipo, String disciplina,
                                   String banca, Integer ano, String origem, List<AlternativaDTO> alternativas) {
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public QuestaoEdicaoDTO buscar(@PathVariable Long id) {
        return paraDTO(carregar(id));
    }

    @Transactional
    @PutMapping("/{id}")
    public QuestaoEdicaoDTO editar(@PathVariable Long id, @Valid @RequestBody EdicaoDTO dto) {
        Questao q = carregar(id);
        Map<Long, Alternativa> atuais = q.getAlternativas().stream()
                .collect(Collectors.toMap(Alternativa::getId, Function.identity()));

        if (dto.alternativas().size() != atuais.size() || !atuais.keySet().containsAll(
                dto.alternativas().stream().map(AlternativaDTO::id).toList())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "As alternativas enviadas não correspondem às da questão");
        }
        if (dto.alternativas().stream().filter(AlternativaDTO::correta).count() != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Marque exatamente uma alternativa correta");
        }
        boolean certoErrado = Questao.CERTO_ERRADO.equals(q.getTipo());
        for (AlternativaDTO a : dto.alternativas()) {
            if (!certoErrado && (a.texto() == null || a.texto().isBlank())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhuma alternativa pode ficar vazia");
            }
        }

        q.setEnunciado(dto.enunciado().strip());
        q.setExplicacao(dto.explicacao() == null || dto.explicacao().isBlank() ? null : dto.explicacao().strip());
        for (AlternativaDTO a : dto.alternativas()) {
            Alternativa alt = atuais.get(a.id());
            if (!certoErrado) alt.setTexto(a.texto().strip());   // em C/E os textos sao sempre "Certo"/"Errado"
            alt.setCorreta(a.correta());
        }
        return paraDTO(q);
    }

    private Questao carregar(Long id) {
        return questaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Questão não encontrada"));
    }

    private QuestaoEdicaoDTO paraDTO(Questao q) {
        List<AlternativaDTO> alternativas = q.getAlternativas().stream()
                .sorted(Comparator.comparing(Alternativa::getOrdem, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(a -> new AlternativaDTO(a.getId(), a.getTexto(), a.isCorreta()))
                .toList();
        return new QuestaoEdicaoDTO(q.getId(), q.getEnunciado(), q.getExplicacao(), q.getTipo(),
                q.getDisciplina().getNome(), q.getBanca().getNome(), q.getAno(), q.getOrigem(), alternativas);
    }
}

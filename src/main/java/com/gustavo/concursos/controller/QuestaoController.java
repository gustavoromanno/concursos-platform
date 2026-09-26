package com.gustavo.concursos.controller;

import com.gustavo.concursos.dto.QuestaoResponseDTO;
import com.gustavo.concursos.entity.Orgao;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.AnotacaoRepository;
import com.gustavo.concursos.repository.EstatisticaQuestaoRepository;
import com.gustavo.concursos.repository.OrgaoRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import com.gustavo.concursos.service.DificuldadeCalculadora;
import com.gustavo.concursos.specification.QuestaoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class QuestaoController {

    private final QuestaoRepository questaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final OrgaoRepository orgaoRepository;
    private final AnotacaoRepository anotacaoRepository;
    private final EstatisticaQuestaoRepository estatisticaRepository;
    private final com.gustavo.concursos.pro.AcessoPro acessoPro;

    public QuestaoController(
            QuestaoRepository questaoRepository,
            UsuarioRepository usuarioRepository,
            OrgaoRepository orgaoRepository,
            AnotacaoRepository anotacaoRepository,
            EstatisticaQuestaoRepository estatisticaRepository,
            com.gustavo.concursos.pro.AcessoPro acessoPro
    ) {
        this.questaoRepository = questaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.orgaoRepository = orgaoRepository;
        this.anotacaoRepository = anotacaoRepository;
        this.estatisticaRepository = estatisticaRepository;
        this.acessoPro = acessoPro;
    }

    // Filtros opcionais e combináveis: palavra-chave, disciplina, assunto, banca,
    // órgão, ano, tipo (MULTIPLA_ESCOLHA ou CERTO_ERRADO), somente com comentários
    // e situação pessoal (RESOLVIDAS, NAO_RESOLVIDAS, CERTAS, ERRADAS).
    @Transactional(readOnly = true)
    @GetMapping("/questoes")
    public Page<QuestaoResponseDTO> listar(
            @RequestParam(required = false) String palavraChave,
            @RequestParam(required = false) Long disciplinaId,
            @RequestParam(required = false) Long bancaId,
            @RequestParam(required = false) Long orgaoId,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) String assunto,
            @RequestParam(required = false) Long assuntoId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Boolean comComentarios,
            @RequestParam(required = false) String situacao,
            @RequestParam(required = false) Boolean comAnotacoes,
            @RequestParam(required = false) String dificuldade,
            @RequestParam(required = false) Long concursoId,
            @RequestParam(required = false) Long cargoId,
            @RequestParam(required = false) String origem,
            Pageable pageable,
            Authentication authentication
    ) {
        // Questoes ineditas geradas a partir de provas sao do plano Pro.
        boolean pro = acessoPro.ehPro(authentication);
        if (!pro && Questao.ORIGEM_IA.equals(origem)) {
            throw com.gustavo.concursos.pro.AcessoPro.bloqueio("As questões inéditas das provas");
        }

        Specification<Questao> filtro = Specification
                .where(QuestaoSpecification.palavraChave(palavraChave))
                .and(QuestaoSpecification.disciplinaId(disciplinaId))
                .and(QuestaoSpecification.bancaId(bancaId))
                .and(QuestaoSpecification.orgaoId(orgaoId))
                .and(QuestaoSpecification.ano(ano))
                .and(QuestaoSpecification.assunto(assunto))
                .and(QuestaoSpecification.assuntoId(assuntoId))
                .and(QuestaoSpecification.tipo(tipo))
                .and(QuestaoSpecification.comComentarios(comComentarios))
                .and(filtroSituacao(situacao, authentication))
                .and(filtroAnotacoes(comAnotacoes, authentication))
                .and(filtroDificuldade(dificuldade))
                .and(QuestaoSpecification.concursoId(concursoId))
                .and(QuestaoSpecification.cargoId(cargoId))
                .and(QuestaoSpecification.origem(origem))
                .and(pro ? null : QuestaoSpecification.semOrigem(Questao.ORIGEM_IA));

        return questaoRepository.findAll(filtro, pageable).map(QuestaoResponseDTO::fromEntity);
    }

    // "Minhas questões": depende do histórico do usuário logado. Certas e
    // erradas seguem a resposta MAIS RECENTE, como a lista de revisão.
    private Specification<Questao> filtroSituacao(String situacao, Authentication authentication) {
        if (situacao == null || situacao.isBlank()) return null;
        if (!java.util.Set.of("RESOLVIDAS", "NAO_RESOLVIDAS", "CERTAS", "ERRADAS").contains(situacao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "situacao invalida: use RESOLVIDAS, NAO_RESOLVIDAS, CERTAS ou ERRADAS");
        }

        Long usuarioId = usuarioLogadoId(authentication);

        return switch (situacao) {
            case "RESOLVIDAS" -> QuestaoSpecification.idEm(questaoRepository.idsRespondidas(usuarioId));
            case "NAO_RESOLVIDAS" -> QuestaoSpecification.idFora(questaoRepository.idsRespondidas(usuarioId));
            case "CERTAS" -> QuestaoSpecification.idEm(questaoRepository.idsPorUltimaResposta(usuarioId, true));
            case "ERRADAS" -> QuestaoSpecification.idEm(questaoRepository.idsPorUltimaResposta(usuarioId, false));
            default -> throw new IllegalStateException("situacao ja validada acima");
        };
    }

    // Somente questoes em que o usuario escreveu anotacao.
    private Specification<Questao> filtroAnotacoes(Boolean comAnotacoes, Authentication authentication) {
        if (!Boolean.TRUE.equals(comAnotacoes)) return null;
        return QuestaoSpecification.idEm(anotacaoRepository.questaoIdsDoUsuario(usuarioLogadoId(authentication)));
    }

    // Dificuldade derivada da taxa de acerto da comunidade (ver DificuldadeCalculadora).
    // Questoes com poucas respostas nao entram em nenhuma faixa.
    private Specification<Questao> filtroDificuldade(String dificuldade) {
        if (dificuldade == null || dificuldade.isBlank()) return null;

        DificuldadeCalculadora.Dificuldade alvo;
        try {
            alvo = DificuldadeCalculadora.Dificuldade.valueOf(dificuldade);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "dificuldade invalida: use FACIL, MEDIA, DIFICIL ou MUITO_DIFICIL");
        }

        List<Long> ids = estatisticaRepository.totaisPorQuestao().stream()
                .filter(t -> DificuldadeCalculadora.classificar(t.getTotal(), t.getAcertos()) == alvo)
                .map(EstatisticaQuestaoRepository.TotaisQuestao::getQuestaoId)
                .toList();
        return QuestaoSpecification.idEm(ids);
    }

    private Long usuarioLogadoId(Authentication authentication) {
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"))
                .getId();
    }

    @Transactional(readOnly = true)
    @GetMapping("/questoes/erradas")
    public List<QuestaoResponseDTO> erradas(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado nao encontrado"));

        return questaoRepository.questoesErradas(usuario.getId()).stream()
                .map(QuestaoResponseDTO::fromEntity)
                .toList();
    }

    // Alimenta o filtro de órgão na interface.
    @GetMapping("/orgaos")
    public List<Orgao> orgaos() {
        return orgaoRepository.findAllByOrderByNomeAsc();
    }
}

package com.gustavo.concursos.importacao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImportacaoService {

    private static final Logger log = LoggerFactory.getLogger(ImportacaoService.class);
    static final long TAMANHO_MAXIMO = 30L * 1024 * 1024;

    private final ImportacaoProvaRepository importacaoRepository;
    private final ImportacaoArquivoRepository arquivoRepository;
    private final QuestaoBaseRepository baseRepository;
    private final QuestaoRascunhoRepository rascunhoRepository;
    private final ImportacaoProcessador processador;
    private final ObjectMapper json;

    public ImportacaoService(
            ImportacaoProvaRepository importacaoRepository,
            ImportacaoArquivoRepository arquivoRepository,
            QuestaoBaseRepository baseRepository,
            QuestaoRascunhoRepository rascunhoRepository,
            ImportacaoProcessador processador,
            ObjectMapper json
    ) {
        this.importacaoRepository = importacaoRepository;
        this.arquivoRepository = arquivoRepository;
        this.baseRepository = baseRepository;
        this.rascunhoRepository = rascunhoRepository;
        this.processador = processador;
        this.json = json;
    }

    @Transactional
    public ImportacaoProva iniciar(MultipartFile prova, MultipartFile gabarito, String cargo, int ineditasPorQuestao) {
        byte[] pdfProva = lerPdf(prova, "prova");
        byte[] pdfGabarito = lerPdf(gabarito, "gabarito");
        if (ineditasPorQuestao < 1 || ineditasPorQuestao > 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escolha de 1 a 3 questoes ineditas por questao da prova");
        }

        ImportacaoProva imp = new ImportacaoProva();
        imp.setNomeArquivoProva(prova.getOriginalFilename());
        imp.setNomeArquivoGabarito(gabarito.getOriginalFilename());
        imp.setCargoInformado(cargo == null || cargo.isBlank() ? null : cargo.strip());
        imp.setIneditasPorQuestao(ineditasPorQuestao);
        imp.setEtapa("Na fila");
        importacaoRepository.save(imp);

        ImportacaoArquivo arquivo = new ImportacaoArquivo();
        arquivo.setImportacaoId(imp.getId());
        arquivo.setPdfProva(pdfProva);
        arquivo.setPdfGabarito(pdfGabarito);
        arquivoRepository.save(arquivo);

        disparar(imp.getId());
        return imp;
    }

    // Recomeca do zero: apaga as originais e os rascunhos nao aprovados.
    // Questoes ja aprovadas (e publicadas) continuam no site.
    @Transactional
    public ImportacaoProva reprocessar(Long id) {
        ImportacaoProva imp = buscar(id);
        if (imp.emAndamento()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta importacao ainda esta em andamento");
        }
        if (!arquivoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Os PDFs desta importacao ja foram descartados. Envie a prova de novo.");
        }
        rascunhoRepository.apagarNaoAprovados(id);
        rascunhoRepository.desvincularBases(id);
        baseRepository.apagarDaImportacao(id);

        imp.setStatus(ImportacaoProva.AGUARDANDO);
        imp.setEtapa("Na fila");
        imp.setProgresso(0);
        imp.setMensagemErro(null);
        imp.setAtualizadoEm(LocalDateTime.now());
        disparar(id);
        return imp;
    }

    @Transactional
    public void excluir(Long id) {
        ImportacaoProva imp = buscar(id);
        if (imp.emAndamento()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Aguarde a importacao terminar para excluir");
        }
        // O banco apaga em cascata originais, rascunhos e PDFs.
        // As questoes ja publicadas continuam no site.
        importacaoRepository.delete(imp);
    }

    @Transactional
    public QuestaoRascunho editarRascunho(Long rascunhoId, ValidadorQuestaoGerada.Questao editada) {
        QuestaoRascunho r = rascunhoRepository.findById(rascunhoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rascunho nao encontrado"));
        if (!QuestaoRascunho.PENDENTE.equals(r.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "So rascunhos pendentes podem ser editados");
        }
        ValidadorQuestaoGerada.Questao valida;
        try {
            String original = r.getBase() == null ? null : r.getBase().getEnunciado();
            valida = ValidadorQuestaoGerada.validar(editada, original);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Questao invalida: " + e.getMessage());
        }
        r.setTipo(valida.tipo());
        r.setDisciplina(valida.disciplina());
        r.setAssunto(valida.assunto());
        r.setEnunciado(valida.enunciado());
        r.setExplicacao(valida.explicacao());
        try {
            r.setAlternativas(json.writeValueAsString(valida.alternativas()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return r;
    }

    public ImportacaoProva buscar(Long id) {
        return importacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Importacao nao encontrada"));
    }

    // Uma reinicializacao do servidor (deploy, hibernacao do plano gratuito) mata
    // o processamento em segundo plano. Marca como erro para permitir reprocessar.
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void marcarInterrompidas() {
        List<ImportacaoProva> presas = importacaoRepository.findByStatusIn(
                List.of(ImportacaoProva.AGUARDANDO, ImportacaoProva.EXTRAINDO, ImportacaoProva.GERANDO));
        for (ImportacaoProva imp : presas) {
            imp.setStatus(ImportacaoProva.ERRO);
            imp.setMensagemErro("Interrompida por reinicio do servidor. Use \"Reprocessar\".");
            imp.setAtualizadoEm(LocalDateTime.now());
        }
        if (!presas.isEmpty()) log.info("{} importacao(oes) interrompida(s) marcadas para reprocessar.", presas.size());
    }

    // Dispara so depois do commit: a thread de fundo precisa enxergar o que foi gravado.
    private void disparar(Long id) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    processador.processar(id);
                }
            });
        } else {
            processador.processar(id);
        }
    }

    private byte[] lerPdf(MultipartFile arquivo, String nome) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Envie o PDF da " + nome);
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O PDF da " + nome + " passa de 30 MB");
        }
        try {
            byte[] bytes = arquivo.getBytes();
            // Todo PDF comeca com "%PDF".
            if (bytes.length < 4 || bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D' || bytes[3] != 'F') {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo da " + nome + " nao e um PDF");
            }
            return bytes;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao foi possivel ler o arquivo da " + nome);
        }
    }
}

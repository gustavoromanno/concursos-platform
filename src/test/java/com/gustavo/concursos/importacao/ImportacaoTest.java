package com.gustavo.concursos.importacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavo.concursos.ia.IaCliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Fluxo completo da importacao com uma IA falsa (respostas prontas): nenhuma
 * chamada real a API, nenhum custo, resultado deterministico.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ImportacaoTest.IaFalsa.class)
class ImportacaoTest {

    static final String ORIGINAL_1 = "Compete privativamente ao Banco Central do Brasil a emissao de moeda no territorio nacional.";

    @TestConfiguration
    static class IaFalsa {
        @Bean
        @Primary
        IaCliente iaFalsa(ObjectMapper json) {
            return pedido -> {
                String resposta = switch (pedido.nomeFerramenta()) {
                    case "registrar_metadados" -> """
                            {"sigla":"Bacen","orgao":"Banco Central do Brasil","ano":2013,"banca":"Cebraspe",
                             "cargo":"Analista","nivel":"Superior","tipo_questoes":"CERTO_ERRADO","total_questoes":3}""";
                    case "registrar_questoes" -> """
                            {"questoes":[
                              {"numero":1,"tipo":"CERTO_ERRADO","disciplina":"Economia","assunto":"Sistema Financeiro Nacional",
                               "enunciado":"%s","alternativas":[],"gabarito":"C","anulada":false},
                              {"numero":2,"tipo":"MULTIPLA_ESCOLHA","disciplina":"Economia","assunto":"Politica monetaria",
                               "enunciado":"Qual instrumento reduz a liquidez da economia no curto prazo?",
                               "alternativas":["Compra de titulos","Venda de titulos","Reducao do compulsorio","Redesconto mais barato","Emissao de moeda"],
                               "gabarito":"B","anulada":false},
                              {"numero":3,"tipo":"CERTO_ERRADO","disciplina":"Economia","assunto":"Cambio",
                               "enunciado":"Item anulado pela banca sobre regime cambial.","alternativas":[],"anulada":true}
                            ]}""".formatted(ORIGINAL_1);
                    case "criar_questoes_ineditas" -> """
                            {"questoes":[
                              {"base_numero":1,"tipo":"CERTO_ERRADO","disciplina":"Economia","assunto":"Sistema Financeiro Nacional",
                               "enunciado":"A autoridade monetaria detem a exclusividade constitucional de colocar papel-moeda em circulacao.",
                               "alternativas":[{"texto":"Certo","correta":true},{"texto":"Errado","correta":false}],
                               "explicacao":"Art. 164 da Constituicao Federal."},
                              {"base_numero":2,"tipo":"MULTIPLA_ESCOLHA","disciplina":"Economia","assunto":"Politica monetaria",
                               "enunciado":"Para conter a inflacao, a autoridade monetaria tende a adotar qual medida?",
                               "alternativas":[{"texto":"Elevar a taxa basica de juros","correta":true},
                                               {"texto":"Reduzir o deposito compulsorio","correta":false},
                                               {"texto":"Comprar titulos publicos","correta":false},
                                               {"texto":"Baratear o redesconto","correta":false},
                                               {"texto":"Ampliar o credito direcionado","correta":false}],
                               "explicacao":"Juros maiores reduzem demanda e liquidez."},
                              {"base_numero":1,"tipo":"CERTO_ERRADO","disciplina":"Economia","assunto":"Sistema Financeiro Nacional",
                               "enunciado":"%s",
                               "alternativas":[{"texto":"Certo","correta":true},{"texto":"Errado","correta":false}],
                               "explicacao":"Copia da original: deve ser barrada."},
                              {"base_numero":99,"tipo":"CERTO_ERRADO","disciplina":"Economia","assunto":"X",
                               "enunciado":"Questao que aponta para uma original inexistente.",
                               "alternativas":[{"texto":"Certo","correta":true},{"texto":"Errado","correta":false}],
                               "explicacao":"Deve ser ignorada."}
                            ]}""".formatted(ORIGINAL_1);
                    default -> throw new IllegalArgumentException("ferramenta inesperada: " + pedido.nomeFerramenta());
                };
                try {
                    return new IaCliente.RespostaFerramenta(json.readTree(resposta), 1000, 200);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            };
        }
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    private MockMultipartFile pdf(String campo) {
        return new MockMultipartFile(campo, campo + ".pdf", "application/pdf", "%PDF-1.4 conteudo de teste".getBytes());
    }

    private long importar() throws Exception {
        MvcResult r = mvc.perform(multipart("/admin/importacoes").file(pdf("prova")).file(pdf("gabarito"))
                        .param("cargo", "Analista").param("ineditas", "1"))
                .andExpect(status().isAccepted())
                .andReturn();
        long id = json.readTree(r.getResponse().getContentAsString()).path("id").asLong();

        await().atMost(Duration.ofSeconds(20)).pollInSameThread().untilAsserted(() ->
                mvc.perform(get("/admin/importacoes/{id}", id))
                        .andExpect(jsonPath("$.status").value("CONCLUIDA")));
        return id;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void importaGeraFiltraEPublicaSoOQueFoiAprovado() throws Exception {
        long id = importar();

        // Concurso e cargo criados a partir da prova; 3 originais lidas (1 anulada);
        // das 4 geradas, a copia e a de original inexistente foram barradas.
        mvc.perform(get("/admin/importacoes/{id}", id))
                .andExpect(jsonPath("$.concurso").value("Bacen 2013"))
                .andExpect(jsonPath("$.cargo").value("Analista"))
                .andExpect(jsonPath("$.questoesLidas").value(3))
                .andExpect(jsonPath("$.pendentes").value(2))
                .andExpect(jsonPath("$.tokensEntrada").value(3000));   // 3 chamadas: metadados, extracao, geracao

        long concursoId = json.readTree(mvc.perform(get("/admin/importacoes/{id}", id))
                .andReturn().getResponse().getContentAsString()).path("concursoId").asLong();

        // Antes da aprovacao, nada aparece no site.
        mvc.perform(get("/questoes").param("concursoId", String.valueOf(concursoId)))
                .andExpect(jsonPath("$.content").isEmpty());

        mvc.perform(post("/admin/importacoes/{id}/aprovar-todos", id))
                .andExpect(jsonPath("$.aprovadas").value(2));

        mvc.perform(get("/questoes").param("concursoId", String.valueOf(concursoId)).param("origem", "IA"))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[*].origem", everyItem(is("IA"))))
                .andExpect(jsonPath("$.content[*].banca", everyItem(is("CESPE/CEBRASPE"))));

        // A questao original da prova nunca aparece no site.
        mvc.perform(get("/questoes").param("palavraChave", "Compete privativamente ao Banco Central"))
                .andExpect(jsonPath("$.content").isEmpty());

        // Concluida com sucesso, os PDFs foram descartados: nao da para reprocessar.
        mvc.perform(post("/admin/importacoes/{id}/reprocessar", id))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void descartarEEditarRascunho() throws Exception {
        long id = importar();
        String lista = mvc.perform(get("/admin/importacoes/{id}/rascunhos", id))
                .andReturn().getResponse().getContentAsString();
        long primeiro = json.readTree(lista).get(0).path("id").asLong();
        long segundo = json.readTree(lista).get(1).path("id").asLong();

        mvc.perform(post("/admin/rascunhos/{id}/descartar", primeiro)).andExpect(status().isNoContent());
        mvc.perform(post("/admin/rascunhos/{id}/aprovar", primeiro)).andExpect(status().isConflict());

        // Edicao valida o conteudo: sem alternativa correta, recusa.
        String invalida = """
                {"tipo":"CERTO_ERRADO","disciplina":"Economia","assunto":"X",
                 "enunciado":"Enunciado editado pelo administrador com tamanho suficiente.",
                 "alternativas":[{"texto":"Certo","correta":false},{"texto":"Errado","correta":false}],
                 "explicacao":"ok"}""";
        mvc.perform(put("/admin/rascunhos/{id}", segundo).contentType("application/json").content(invalida))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void recusaArquivoQueNaoEPdf() throws Exception {
        MockMultipartFile texto = new MockMultipartFile("prova", "prova.pdf", "application/pdf", "nao sou pdf".getBytes());
        mvc.perform(multipart("/admin/importacoes").file(texto).file(pdf("gabarito")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void usuarioComumNaoAcessaAdmin() throws Exception {
        mvc.perform(get("/admin/importacoes")).andExpect(status().isForbidden());
    }

    @Test
    void semTokenRecebe401() throws Exception {
        mvc.perform(get("/admin/importacoes")).andExpect(status().isUnauthorized());
    }
}

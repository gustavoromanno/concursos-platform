package com.gustavo.concursos.importacao;

import com.gustavo.concursos.importacao.ValidadorQuestaoGerada.Alternativa;
import com.gustavo.concursos.importacao.ValidadorQuestaoGerada.Questao;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.gustavo.concursos.importacao.ValidadorQuestaoGerada.similaridade;
import static com.gustavo.concursos.importacao.ValidadorQuestaoGerada.validar;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorQuestaoGeradaTest {

    private static final String ENUNCIADO = "O Banco Central pode emitir moeda para financiar diretamente o Tesouro Nacional.";

    private Questao ce(String enunciado, String textoCorreta) {
        return new Questao("CERTO_ERRADO", "Economia", "Politica monetaria", enunciado,
                List.of(new Alternativa("Certo", textoCorreta.equals("Certo")),
                        new Alternativa("Errado", textoCorreta.equals("Errado"))),
                "A Constituicao veda esse financiamento.");
    }

    private Questao me(List<Alternativa> alternativas) {
        return new Questao("MULTIPLA_ESCOLHA", "Economia", null,
                "Assinale a alternativa que apresenta um instrumento de politica monetaria.",
                alternativas, "Operacoes de mercado aberto sao instrumento classico.");
    }

    @Test
    void certoErradoValidoENormalizado() {
        Questao q = validar(ce(ENUNCIADO, "Errado"), null);
        assertThat(q.alternativas()).containsExactly(new Alternativa("Certo", false), new Alternativa("Errado", true));
    }

    @Test
    void certoErradoAceitaCaixaEAcentuacaoDiferentes() {
        Questao entrada = new Questao("certo_errado", "Economia", "", ENUNCIADO,
                List.of(new Alternativa(" certo ", true), new Alternativa("ERRADO", false)), "Explicacao.");
        Questao q = validar(entrada, null);
        assertThat(q.tipo()).isEqualTo("CERTO_ERRADO");
        assertThat(q.assunto()).isNull();
        assertThat(q.alternativas().get(0)).isEqualTo(new Alternativa("Certo", true));
    }

    @Test
    void multiplaEscolhaValida() {
        Questao q = validar(me(List.of(
                new Alternativa("Operacoes de mercado aberto", true),
                new Alternativa("Emissao de titulos pelo Tesouro", false),
                new Alternativa("Fixacao do salario minimo", false),
                new Alternativa("Cobranca de impostos", false))), null);
        assertThat(q.alternativas()).hasSize(4);
    }

    @Test
    void rejeitaMultiplaEscolhaSemCorretaOuComDuas() {
        assertThatThrownBy(() -> validar(me(List.of(
                new Alternativa("A", false), new Alternativa("B", false),
                new Alternativa("C", false), new Alternativa("D", false))), null))
                .hasMessageContaining("exatamente uma");
        assertThatThrownBy(() -> validar(me(List.of(
                new Alternativa("A", true), new Alternativa("B", true),
                new Alternativa("C", false), new Alternativa("D", false))), null))
                .hasMessageContaining("exatamente uma");
    }

    @Test
    void rejeitaAlternativasRepetidasOuQuantidadeErrada() {
        assertThatThrownBy(() -> validar(me(List.of(
                new Alternativa("Juros", true), new Alternativa("juros", false),
                new Alternativa("C", false), new Alternativa("D", false))), null))
                .hasMessageContaining("repetidas");
        assertThatThrownBy(() -> validar(me(List.of(
                new Alternativa("A", true), new Alternativa("B", false), new Alternativa("C", false))), null))
                .hasMessageContaining("4 ou 5");
    }

    @Test
    void rejeitaTipoDesconhecidoEnunciadoCurtoESemExplicacao() {
        assertThatThrownBy(() -> validar(new Questao("DISCURSIVA", "X", null, ENUNCIADO, List.of(), "e"), null))
                .hasMessageContaining("tipo");
        assertThatThrownBy(() -> validar(ce("Curto demais.", "Certo"), null))
                .hasMessageContaining("curto");
        Questao semExplicacao = new Questao("CERTO_ERRADO", "Economia", null, ENUNCIADO,
                List.of(new Alternativa("Certo", true), new Alternativa("Errado", false)), " ");
        assertThatThrownBy(() -> validar(semExplicacao, null)).hasMessageContaining("explicacao");
    }

    @Test
    void rejeitaCopiaDaQuestaoOriginal() {
        String quaseIgual = "O Banco Central pode emitir moeda para financiar diretamente o Tesouro.";
        assertThatThrownBy(() -> validar(ce(quaseIgual, "Errado"), ENUNCIADO))
                .hasMessageContaining("parecida demais");
    }

    @Test
    void aceitaQuestaoInspiradaMasComTextoProprio() {
        String inedita = "E vedado a autoridade monetaria conceder emprestimos ao ente federal responsavel pelas contas publicas.";
        assertThat(validar(ce(inedita, "Certo"), ENUNCIADO)).isNotNull();
    }

    @Test
    void similaridadeIgnoraAcentoCaixaEPalavrasCurtas() {
        assertThat(similaridade("Política Monetária", "politica monetaria")).isEqualTo(1.0);
        assertThat(similaridade("a de o", "e da os")).isEqualTo(0.0);
        assertThat(similaridade(null, "texto")).isEqualTo(0.0);
    }
}

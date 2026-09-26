package com.gustavo.concursos.importacao;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Filtro de qualidade das questoes geradas pela IA, antes de irem para a fila
 * de revisao. Funcoes puras, testadas sem Spring.
 *
 * Tambem barra questoes parecidas demais com a original da prova: a original
 * e so referencia, e o texto dela nao pode reaparecer no site.
 */
public final class ValidadorQuestaoGerada {

    public static final String MULTIPLA_ESCOLHA = "MULTIPLA_ESCOLHA";
    public static final String CERTO_ERRADO = "CERTO_ERRADO";

    /** Acima disso (sobreposicao de palavras) a questao e considerada copia. */
    public static final double LIMITE_SIMILARIDADE = 0.6;

    public record Alternativa(String texto, boolean correta) {
    }

    public record Questao(String tipo, String disciplina, String assunto, String enunciado,
                          List<Alternativa> alternativas, String explicacao) {
    }

    private ValidadorQuestaoGerada() {
    }

    /**
     * Devolve a questao normalizada ou lanca IllegalArgumentException com o motivo.
     *
     * @param enunciadoOriginal enunciado da questao da prova que serviu de base (pode ser nulo)
     */
    public static Questao validar(Questao q, String enunciadoOriginal) {
        if (q == null) throw new IllegalArgumentException("questao vazia");

        String tipo = q.tipo() == null ? "" : q.tipo().trim().toUpperCase(Locale.ROOT);
        if (!MULTIPLA_ESCOLHA.equals(tipo) && !CERTO_ERRADO.equals(tipo)) {
            throw new IllegalArgumentException("tipo invalido: " + q.tipo());
        }

        String enunciado = limpar(q.enunciado());
        if (enunciado.length() < 20) throw new IllegalArgumentException("enunciado curto demais");

        String disciplina = limpar(q.disciplina());
        if (disciplina.isEmpty()) throw new IllegalArgumentException("disciplina ausente");

        String explicacao = limpar(q.explicacao());
        if (explicacao.isEmpty()) throw new IllegalArgumentException("explicacao ausente");

        List<Alternativa> alternativas = CERTO_ERRADO.equals(tipo)
                ? normalizarCertoErrado(q.alternativas())
                : validarMultiplaEscolha(q.alternativas());

        if (enunciadoOriginal != null && similaridade(enunciado, enunciadoOriginal) > LIMITE_SIMILARIDADE) {
            throw new IllegalArgumentException("parecida demais com a questao original da prova");
        }

        String assunto = limpar(q.assunto());
        return new Questao(tipo, disciplina, assunto.isEmpty() ? null : assunto, enunciado, alternativas, explicacao);
    }

    private static List<Alternativa> normalizarCertoErrado(List<Alternativa> alternativas) {
        if (alternativas == null || alternativas.size() != 2) {
            throw new IllegalArgumentException("Certo/Errado precisa de exatamente duas opcoes");
        }
        long corretas = alternativas.stream().filter(Alternativa::correta).count();
        if (corretas != 1) throw new IllegalArgumentException("Certo/Errado precisa de exatamente um gabarito");

        // O gabarito e decidido pela opcao marcada correta cujo texto e "Certo" ou "Errado".
        Alternativa marcada = alternativas.stream().filter(Alternativa::correta).findFirst().orElseThrow();
        String texto = semAcento(limpar(marcada.texto())).toLowerCase(Locale.ROOT);
        boolean certo;
        if (texto.equals("certo")) certo = true;
        else if (texto.equals("errado")) certo = false;
        else throw new IllegalArgumentException("Certo/Errado com opcoes diferentes de Certo e Errado");

        return List.of(new Alternativa("Certo", certo), new Alternativa("Errado", !certo));
    }

    private static List<Alternativa> validarMultiplaEscolha(List<Alternativa> alternativas) {
        if (alternativas == null || alternativas.size() < 4 || alternativas.size() > 5) {
            throw new IllegalArgumentException("multipla escolha precisa de 4 ou 5 alternativas");
        }
        List<Alternativa> limpas = new ArrayList<>();
        Set<String> vistos = new HashSet<>();
        int corretas = 0;
        for (Alternativa a : alternativas) {
            String texto = limpar(a.texto());
            if (texto.isEmpty()) throw new IllegalArgumentException("alternativa vazia");
            if (!vistos.add(semAcento(texto).toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("alternativas repetidas");
            }
            if (a.correta()) corretas++;
            limpas.add(new Alternativa(texto, a.correta()));
        }
        if (corretas != 1) throw new IllegalArgumentException("precisa de exatamente uma alternativa correta");
        return limpas;
    }

    /**
     * Sobreposicao de palavras (indice de Jaccard) entre dois textos, de 0 a 1.
     * Considera so palavras com 4+ letras, sem acento e em minusculas.
     */
    public static double similaridade(String a, String b) {
        Set<String> pa = palavras(a);
        Set<String> pb = palavras(b);
        if (pa.isEmpty() || pb.isEmpty()) return 0.0;
        Set<String> intersecao = new HashSet<>(pa);
        intersecao.retainAll(pb);
        Set<String> uniao = new HashSet<>(pa);
        uniao.addAll(pb);
        return (double) intersecao.size() / uniao.size();
    }

    private static Set<String> palavras(String texto) {
        Set<String> resultado = new HashSet<>();
        if (texto == null) return resultado;
        for (String p : semAcento(texto).toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) {
            if (p.length() >= 4) resultado.add(p);
        }
        return resultado;
    }

    private static String semAcento(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    private static String limpar(String texto) {
        return texto == null ? "" : texto.strip().replaceAll("[ \\t]+", " ");
    }
}

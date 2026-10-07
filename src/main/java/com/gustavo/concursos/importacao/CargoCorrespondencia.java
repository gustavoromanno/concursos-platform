package com.gustavo.concursos.importacao;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Acha, entre os cargos ja cadastrados de um concurso, aquele que corresponde ao
 * nome de cargo lido da prova. O catalogo (V32) cadastra cargos por area, como
 * "Analista – Área 4: Contabilidade e Finanças", e a IA costuma devolver variacoes
 * ("Analista - Area 4", "Analista – Contabilidade e Financas"). Sem esta etapa a
 * importacao criaria um cargo duplicado.
 *
 * Regras, nesta ordem (a primeira que achar exatamente UM cargo vence):
 *   1. nome igual, ignorando caixa, acentos e pontuacao;
 *   2. mesmo cargo-base (primeira palavra) e mesma "area N";
 *   3. todas as palavras do nome lido aparecem no nome cadastrado (minimo 2 palavras);
 *   4. todas as palavras do nome cadastrado aparecem no nome lido (minimo 2 palavras).
 * Se nenhuma regra der resultado unico, devolve vazio e a importacao cria o cargo,
 * como antes. Ambiguidade nunca vira palpite.
 */
public final class CargoCorrespondencia {

    private static final Set<String> IRRELEVANTES = Set.of("de", "da", "do", "das", "dos", "e", "a", "o", "em", "para");
    private static final Pattern AREA = Pattern.compile("\\barea (\\d+)\\b");

    private CargoCorrespondencia() {
    }

    public static <T> Optional<T> encontrar(List<T> cargos, Function<T, String> nome, String informado) {
        if (informado == null || informado.isBlank() || cargos.isEmpty()) return Optional.empty();
        String lido = normalizar(informado);
        if (lido.isEmpty()) return Optional.empty();

        Optional<T> r = unico(cargos, c -> normalizar(nome.apply(c)).equals(lido));
        if (r.isPresent()) return r;

        Matcher m = AREA.matcher(lido);
        if (m.find()) {
            String area = "area " + m.group(1);
            String base = primeiraPalavra(lido);
            r = unico(cargos, c -> {
                String n = normalizar(nome.apply(c));
                return primeiraPalavra(n).equals(base) && Pattern.compile("\\b" + area + "\\b").matcher(n).find();
            });
            if (r.isPresent()) return r;
        }

        Set<String> palavrasLidas = palavras(lido);
        if (palavrasLidas.size() >= 2) {
            r = unico(cargos, c -> palavras(normalizar(nome.apply(c))).containsAll(palavrasLidas));
            if (r.isPresent()) return r;
        }

        return unico(cargos, c -> {
            Set<String> cadastradas = palavras(normalizar(nome.apply(c)));
            return cadastradas.size() >= 2 && palavrasLidas.containsAll(cadastradas);
        });
    }

    static String normalizar(String s) {
        if (s == null) return "";
        String semAcento = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase().replaceAll("[^a-z0-9]+", " ").trim();
    }

    private static Set<String> palavras(String normalizado) {
        Set<String> r = new LinkedHashSet<>(Arrays.asList(normalizado.split(" ")));
        r.removeAll(IRRELEVANTES);
        r.remove("");
        return r;
    }

    private static String primeiraPalavra(String normalizado) {
        int i = normalizado.indexOf(' ');
        return i < 0 ? normalizado : normalizado.substring(0, i);
    }

    private static <T> Optional<T> unico(List<T> cargos, java.util.function.Predicate<T> criterio) {
        List<T> achados = cargos.stream().filter(criterio).toList();
        return achados.size() == 1 ? Optional.of(achados.get(0)) : Optional.empty();
    }
}

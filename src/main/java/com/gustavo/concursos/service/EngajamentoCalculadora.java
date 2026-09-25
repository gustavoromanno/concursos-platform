package com.gustavo.concursos.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Calculos de engajamento (ofensiva e intensidade do mapa de estudo).
 *
 * Funcoes puras: recebem o historico diario (data -> [total, acertos]) e a meta,
 * e nao dependem de banco, relogio ou Spring. Por isso "hoje" entra como
 * parametro — facilita testar qualquer cenario de data.
 */
public final class EngajamentoCalculadora {

    private EngajamentoCalculadora() {
    }

    public static boolean bateuMeta(Map<LocalDate, long[]> porDia, LocalDate dia, int meta) {
        long[] valores = porDia.get(dia);
        return valores != null && valores[0] >= meta;
    }

    /**
     * Ofensiva atual: dias consecutivos batendo a meta, contando para tras.
     *
     * Se a meta de HOJE ainda nao foi batida, a contagem comeca em ontem — do
     * contrario a ofensiva apareceria zerada toda manha, antes de o usuario
     * sentar para estudar.
     */
    public static int ofensivaAtual(Map<LocalDate, long[]> porDia, int meta, LocalDate hoje) {
        LocalDate cursor = bateuMeta(porDia, hoje, meta) ? hoje : hoje.minusDays(1);

        int streak = 0;
        while (bateuMeta(porDia, cursor, meta)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    /** Maior sequencia ja alcancada dentro da janela analisada. */
    public static int melhorOfensiva(Map<LocalDate, long[]> porDia, int meta) {
        List<LocalDate> diasValidos = porDia.entrySet().stream()
                .filter(e -> e.getValue()[0] >= meta)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        int melhor = 0;
        int atual = 0;
        LocalDate anterior = null;

        for (LocalDate dia : diasValidos) {
            atual = (anterior != null && dia.equals(anterior.plusDays(1))) ? atual + 1 : 1;
            melhor = Math.max(melhor, atual);
            anterior = dia;
        }
        return melhor;
    }

    /** Intensidade do quadradinho no mapa, de 0 a 4. */
    public static int nivel(long respondidas, int meta) {
        if (respondidas == 0) return 0;
        double proporcao = (double) respondidas / meta;
        if (proporcao < 0.5) return 1;
        if (proporcao < 1.0) return 2;
        if (proporcao < 2.0) return 3;
        return 4;
    }
}

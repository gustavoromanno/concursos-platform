package com.gustavo.concursos.service;

/**
 * Dificuldade de uma questao, derivada da taxa de acerto de TODOS os usuarios.
 *
 * Com menos de {@link #MINIMO_RESPOSTAS} respostas a amostra e pequena demais
 * para dizer algo, e a questao fica sem classificacao (null) — em vez de
 * exibir um rotulo que nao se sustenta.
 *
 * Fonte unica da regra: usada tanto no painel da comunidade quanto no filtro.
 */
public final class DificuldadeCalculadora {

    public static final int MINIMO_RESPOSTAS = 5;

    public enum Dificuldade {
        FACIL("Fácil"), MEDIA("Média"), DIFICIL("Difícil"), MUITO_DIFICIL("Muito difícil");

        private final String rotulo;

        Dificuldade(String rotulo) {
            this.rotulo = rotulo;
        }

        public String rotulo() {
            return rotulo;
        }
    }

    private DificuldadeCalculadora() {
    }

    /** Percentual de acerto com uma casa decimal, como exibido na tela. */
    public static double percentual(long total, long acertos) {
        return total == 0 ? 0.0 : Math.round(acertos * 1000.0 / total) / 10.0;
    }

    /** null quando ainda nao ha respostas suficientes. */
    public static Dificuldade classificar(long total, long acertos) {
        if (total < MINIMO_RESPOSTAS) return null;
        double p = percentual(total, acertos);
        if (p >= 80) return Dificuldade.FACIL;
        if (p >= 60) return Dificuldade.MEDIA;
        if (p >= 40) return Dificuldade.DIFICIL;
        return Dificuldade.MUITO_DIFICIL;
    }
}

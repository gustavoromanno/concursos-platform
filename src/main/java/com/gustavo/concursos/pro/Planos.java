package com.gustavo.concursos.pro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Planos pre-pagos. Precos em centavos, configuraveis por variavel de ambiente
 * (sem precisar de deploy de codigo para mudar preco).
 */
@Component
public class Planos {

    public record Plano(String codigo, String nome, int dias, int precoCentavos) {
        /** Preco equivalente por mes, para exibir ("R$ 19,90/mes no anual"). */
        public int precoMensalEquivalente() {
            return (int) Math.round(precoCentavos * 30.0 / dias);
        }
    }

    private final List<Plano> planos;

    public Planos(
            @Value("${pro.preco.mensal:2990}") int mensal,
            @Value("${pro.preco.trimestral:7990}") int trimestral,
            @Value("${pro.preco.anual:23880}") int anual
    ) {
        this.planos = List.of(
                new Plano("MENSAL", "Pro Mensal", 30, mensal),
                new Plano("TRIMESTRAL", "Pro Trimestral", 90, trimestral),
                new Plano("ANUAL", "Pro Anual", 365, anual));
    }

    public List<Plano> todos() {
        return planos;
    }

    public Optional<Plano> buscar(String codigo) {
        return planos.stream().filter(p -> p.codigo().equalsIgnoreCase(codigo == null ? "" : codigo)).findFirst();
    }
}

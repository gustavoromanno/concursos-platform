package com.gustavo.concursos.pro;

import com.gustavo.concursos.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

// Assinatura mensal recorrente na Stripe. O status e o da propria Stripe
// (active, trialing, past_due, canceled, incomplete...).
@Entity
@Table(name = "assinatura")
@Getter
@Setter
@NoArgsConstructor
public class Assinatura {

    /** Status em que a assinatura ainda cobra (ou vai cobrar) a pessoa. */
    public static final Set<String> VIGENTES = Set.of("active", "trialing", "past_due", "incomplete");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "stripe_assinatura_id", nullable = false, unique = true, length = 255)
    private String stripeAssinaturaId;

    @Column(name = "stripe_cliente_id", length = 255)
    private String stripeClienteId;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "valor_centavos", nullable = false)
    private int valorCentavos;

    @Column(name = "periodo_fim")
    private LocalDateTime periodoFim;

    @Column(name = "cancelar_no_fim", nullable = false)
    private boolean cancelarNoFim;

    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm = LocalDateTime.now();

    @Column(name = "encerrada_em")
    private LocalDateTime encerradaEm;

    public boolean vigente() {
        return encerradaEm == null && VIGENTES.contains(status);
    }
}

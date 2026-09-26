package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    // USUARIO (padrão) ou ADMIN. Só ADMIN cadastra e remove conteúdo.
    @Column(nullable = false, length = 20)
    private String papel = "USUARIO";

    // Consentimento para e-mails promocionais (LGPD): desmarcado ate a pessoa aceitar.
    @Column(name = "aceita_marketing", nullable = false)
    private boolean aceitaMarketing;

    @Column(name = "marketing_atualizado_em")
    private java.time.LocalDateTime marketingAtualizadoEm;

    @Column(name = "criado_em", nullable = false)
    private java.time.LocalDateTime criadoEm = java.time.LocalDateTime.now();

    // Acesso Pro pre-pago valido ate esta data (nulo = nunca comprou).
    @Column(name = "pro_ate")
    private java.time.LocalDateTime proAte;

    @Column(length = 2)
    private String uf;

    @Column(name = "carreira_alvo", length = 60)
    private String carreiraAlvo;

    @Column(length = 500)
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String foto;

    // ADMIN sempre tem tudo liberado.
    public boolean ehPro() {
        return "ADMIN".equals(papel) || (proAte != null && proAte.isAfter(java.time.LocalDateTime.now()));
    }

    // E-mail sempre minusculo e sem espacos: e a chave de login.
    public static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}

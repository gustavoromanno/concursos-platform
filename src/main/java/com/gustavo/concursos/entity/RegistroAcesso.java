package com.gustavo.concursos.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Registro de acesso do Marco Civil (tabela criada na V39). A gravacao e a limpeza
// sao feitas por SQL no RegistroAcessoService; a entidade existe para o Hibernate
// validar o schema em producao e criar a tabela no banco em memoria dos testes.
// Sem chave estrangeira de proposito: o registro sobrevive a exclusao da conta.
@Entity
@Table(name = "registro_acesso")
@Getter
@Setter
@NoArgsConstructor
public class RegistroAcesso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String ip;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}

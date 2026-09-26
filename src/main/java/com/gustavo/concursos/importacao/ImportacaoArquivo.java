package com.gustavo.concursos.importacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// PDFs enviados, guardados so ate a importacao terminar com sucesso.
@Entity
@Table(name = "importacao_arquivo")
@Getter
@Setter
@NoArgsConstructor
public class ImportacaoArquivo {

    @Id
    @Column(name = "importacao_id")
    private Long importacaoId;

    @Column(name = "pdf_prova", nullable = false)
    private byte[] pdfProva;

    @Column(name = "pdf_gabarito", nullable = false)
    private byte[] pdfGabarito;
}

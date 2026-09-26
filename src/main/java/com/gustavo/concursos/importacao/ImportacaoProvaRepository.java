package com.gustavo.concursos.importacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportacaoProvaRepository extends JpaRepository<ImportacaoProva, Long> {

    List<ImportacaoProva> findAllByOrderByCriadoEmDesc();

    List<ImportacaoProva> findByStatusIn(List<String> status);
}

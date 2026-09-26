package com.gustavo.concursos.importacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestaoBaseRepository extends JpaRepository<QuestaoBase, Long> {

    List<QuestaoBase> findByImportacaoIdOrderByNumeroAsc(Long importacaoId);

    long countByImportacaoId(Long importacaoId);

    @Modifying
    @Query("DELETE FROM QuestaoBase b WHERE b.importacao.id = :importacaoId")
    void apagarDaImportacao(@Param("importacaoId") Long importacaoId);
}

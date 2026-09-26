package com.gustavo.concursos.importacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestaoRascunhoRepository extends JpaRepository<QuestaoRascunho, Long> {

    List<QuestaoRascunho> findByImportacaoIdAndStatusOrderByIdAsc(Long importacaoId, String status);

    long countByImportacaoIdAndStatus(Long importacaoId, String status);

    // Ao reprocessar, some so o que ainda nao foi aprovado.
    @Modifying
    @Query("DELETE FROM QuestaoRascunho r WHERE r.importacao.id = :importacaoId AND r.status <> 'APROVADA'")
    void apagarNaoAprovados(@Param("importacaoId") Long importacaoId);

    // Rascunhos aprovados perdem o vinculo com a base antes de a base ser apagada.
    @Modifying
    @Query("UPDATE QuestaoRascunho r SET r.base = null WHERE r.importacao.id = :importacaoId")
    void desvincularBases(@Param("importacaoId") Long importacaoId);
}

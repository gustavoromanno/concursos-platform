package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Resposta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Estatisticas coletivas de uma questao: como TODOS os usuarios responderam.
// Repositorio separado para nao inflar o RespostaRepository, que ja trata
// do desempenho individual.
public interface EstatisticaQuestaoRepository extends JpaRepository<Resposta, Long> {

    interface DistribuicaoAlternativa {
        Long getAlternativaId();
        long getTotal();
    }

    @Query("""
        SELECT r.alternativaEscolhida.id AS alternativaId,
               COUNT(r) AS total
        FROM Resposta r
        WHERE r.questao.id = :questaoId
        GROUP BY r.alternativaEscolhida.id
        """)
    List<DistribuicaoAlternativa> distribuicao(@Param("questaoId") Long questaoId);

    @Query("SELECT COUNT(r) FROM Resposta r WHERE r.questao.id = :questaoId")
    long totalRespostas(@Param("questaoId") Long questaoId);

    @Query("SELECT COUNT(r) FROM Resposta r WHERE r.questao.id = :questaoId AND r.correta = true")
    long totalAcertos(@Param("questaoId") Long questaoId);
}

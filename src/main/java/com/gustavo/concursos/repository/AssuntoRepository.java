package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Assunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssuntoRepository extends JpaRepository<Assunto, Long> {

    List<Assunto> findByDisciplinaIdOrderByNomeAsc(Long disciplinaId);

    List<Assunto> findByDisciplinaIdAndPaiIsNullOrderByNomeAsc(Long disciplinaId);

    // Projection plana com TODOS os assuntos em uma unica query.
    // Trazendo disciplinaId e paiId como valores escalares, montamos a arvore
    // em memoria sem disparar lazy loading nenhum.
    interface AssuntoPlano {
        Long getId();
        String getNome();
        Long getDisciplinaId();
        Long getPaiId();
    }

    @Query("""
        SELECT a.id AS id,
               a.nome AS nome,
               a.disciplina.id AS disciplinaId,
               a.pai.id AS paiId
        FROM Assunto a
        ORDER BY a.nome ASC
        """)
    List<AssuntoPlano> listarTodosPlano();
}

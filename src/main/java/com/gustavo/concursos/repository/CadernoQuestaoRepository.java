package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.CadernoQuestao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CadernoQuestaoRepository extends JpaRepository<CadernoQuestao, Long> {

    Optional<CadernoQuestao> findByCadernoIdAndQuestaoId(Long cadernoId, Long questaoId);

    boolean existsByCadernoIdAndQuestaoId(Long cadernoId, Long questaoId);

    long countByCadernoId(Long cadernoId);
}

package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.SimuladoQuestao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SimuladoQuestaoRepository extends JpaRepository<SimuladoQuestao, Long> {

    Optional<SimuladoQuestao> findBySimuladoIdAndQuestaoId(Long simuladoId, Long questaoId);
}

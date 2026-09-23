package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByQuestaoIdOrderByCriadoEmDesc(Long questaoId);
    long countByQuestaoId(Long questaoId);
}

package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Marcador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarcadorRepository extends JpaRepository<Marcador, Long> {

    List<Marcador> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    Optional<Marcador> findByUsuarioIdAndQuestaoId(Long usuarioId, Long questaoId);

    // Ids das questoes marcadas: o frontend usa para desenhar o icone
    // ja preenchido nas questoes que o usuario marcou.
    @Query("SELECT m.questao.id FROM Marcador m WHERE m.usuario.id = :usuarioId")
    List<Long> idsMarcados(@Param("usuarioId") Long usuarioId);
}

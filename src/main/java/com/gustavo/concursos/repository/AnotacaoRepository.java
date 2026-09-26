package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Anotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnotacaoRepository extends JpaRepository<Anotacao, Long> {

    Optional<Anotacao> findByUsuarioIdAndQuestaoId(Long usuarioId, Long questaoId);

    // Ids das questoes em que o usuario tem anotacao (marca o botao e alimenta o filtro).
    @Query("SELECT a.questao.id FROM Anotacao a WHERE a.usuario.id = :usuarioId")
    List<Long> questaoIdsDoUsuario(@Param("usuarioId") Long usuarioId);
}

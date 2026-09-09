package com.gustavo.concursos.repository;

import com.gustavo.concursos.entity.Questao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// JpaSpecificationExecutor permite montar filtros dinamicos e combinaveis
// (disciplina + banca + ano + assunto) sem precisar de um metodo para cada
// combinacao possivel. Usado no endpoint de listagem do Sprint 2.
public interface QuestaoRepository extends JpaRepository<Questao, Long>, JpaSpecificationExecutor<Questao> {
}

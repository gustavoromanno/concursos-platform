package com.gustavo.concursos.email;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampanhaEmailRepository extends JpaRepository<CampanhaEmail, Long> {

    List<CampanhaEmail> findTop20ByOrderByCriadoEmDesc();
}

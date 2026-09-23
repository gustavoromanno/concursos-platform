package com.gustavo.concursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

// VIA_DTO faz o Spring serializar as paginas por meio de um DTO estavel, em vez
// de expor a classe interna PageImpl. Sem isso o Spring emite um aviso a cada
// resposta paginada, porque o formato do JSON pode mudar entre versoes.
// O campo "content" continua igual; os metadados passam a vir dentro de "page".
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@SpringBootApplication
public class ConcursosPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConcursosPlatformApplication.class, args);
    }

}

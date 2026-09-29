package com.gustavo.concursos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// O que o navegador precisa para instalar o app tem de ser publico (sem login).
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PwaTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void arquivosDoAppSaoPublicos() throws Exception {
        for (String caminho : new String[]{"/manifest.webmanifest", "/sw.js", "/robots.txt",
                "/icones/icone-192.png", "/icones/icone-512.png", "/icones/icone.svg"}) {
            mvc.perform(get(caminho)).andExpect(status().isOk());
        }
    }

    @Test
    void nomeDaMarcaVemDaConfiguracao() throws Exception {
        mvc.perform(get("/publico/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Concursos Platform"));
    }
}

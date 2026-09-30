package com.gustavo.concursos;

import com.gustavo.concursos.email.EmailCliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "app.exigir-confirmacao-email=true")
@Import(ConfirmacaoEmailTest.EmailFalso.class)
class ConfirmacaoEmailTest {

    static final List<String> HTMLS = new CopyOnWriteArrayList<>();

    @TestConfiguration
    static class EmailFalso {
        @Bean
        @Primary
        EmailCliente emailFalso() {
            return (para, assunto, html) -> HTMLS.add(html);
        }
    }

    @Autowired private MockMvc mvc;

    private ResultActions login(String email, String senha) throws Exception {
        return mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"));
    }

    @Test
    void soEntraDepoisDeConfirmarEOLinkValeUmaVez() throws Exception {
        String email = "confirma" + System.nanoTime() + "@teste.com";
        HTMLS.clear();
        mvc.perform(post("/auth/registrar").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Nova\",\"email\":\"" + email + "\",\"senha\":\"Senha123!\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.confirmacaoNecessaria").value(true));
        assertThat(HTMLS).hasSize(1);

        login(email, "Senha123!").andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(containsString("Confirme seu e-mail")));

        Matcher m = java.util.regex.Pattern.compile("confirmar=([A-Za-z0-9_-]+)").matcher(HTMLS.get(0));
        assertThat(m.find()).isTrue();
        String corpo = "{\"token\":\"" + m.group(1) + "\"}";
        mvc.perform(post("/auth/confirmar-email").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk());
        login(email, "Senha123!").andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty());

        mvc.perform(post("/auth/confirmar-email").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest());
    }

    @Test
    void muitasSenhasErradasBloqueiamAteASenhaCerta() throws Exception {
        String email = "forca" + System.nanoTime() + "@teste.com";
        mvc.perform(post("/auth/registrar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Alvo\",\"email\":\"" + email + "\",\"senha\":\"Senha123!\"}")).andExpect(status().isCreated());

        for (int i = 0; i < 5; i++) login(email, "Errada123!").andExpect(status().isUnauthorized());
        login(email, "Senha123!").andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value(containsString("Muitas tentativas")));
    }
}

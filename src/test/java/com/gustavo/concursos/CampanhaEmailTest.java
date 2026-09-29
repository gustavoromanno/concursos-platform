package com.gustavo.concursos;

import com.gustavo.concursos.email.EmailCliente;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
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

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "email.pausa-entre-envios-ms=0")
@Import(CampanhaEmailTest.EmailFalso.class)
class CampanhaEmailTest {

    record Enviado(String para, String html, Map<String, String> cabecalhos) {
    }

    static final List<Enviado> ENVIADOS = new CopyOnWriteArrayList<>();

    @TestConfiguration
    static class EmailFalso {
        @Bean
        @Primary
        EmailCliente emailFalso() {
            return new EmailCliente() {
                @Override
                public void enviar(String para, String assunto, String html) {
                    enviar(para, assunto, html, Map.of());
                }

                @Override
                public void enviar(String para, String assunto, String html, Map<String, String> cabecalhos) {
                    ENVIADOS.add(new Enviado(para, html, cabecalhos));
                }
            };
        }
    }

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;

    private Usuario criar(String email, boolean aceita) {
        Usuario u = new Usuario();
        u.setNome("Pessoa <b>Teste</b>");
        u.setEmail(email);
        u.setSenhaHash("x");
        u.setAceitaMarketing(aceita);
        return usuarioRepository.save(u);
    }

    @Test
    void enviaSoParaQuemAceitouComDescadastroQueFunciona() throws Exception {
        ENVIADOS.clear();
        String sufixo = String.valueOf(System.nanoTime());
        criar("aceita" + sufixo + "@teste.com", true);
        criar("recusa" + sufixo + "@teste.com", false);

        mvc.perform(post("/admin/campanhas").with(user("admin").roles("ADMIN")).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"assunto\":\"Novidades\",\"mensagem\":\"Lote novo <script>x</script>\\n\\nBons estudos\"}"))
                .andExpect(status().isAccepted());

        await().atMost(Duration.ofSeconds(10)).until(() ->
                ENVIADOS.stream().anyMatch(e -> e.para().equals("aceita" + sufixo + "@teste.com")));
        assertThat(ENVIADOS).noneMatch(e -> e.para().equals("recusa" + sufixo + "@teste.com"));

        Enviado enviado = ENVIADOS.stream().filter(e -> e.para().equals("aceita" + sufixo + "@teste.com")).findFirst().orElseThrow();
        assertThat(enviado.html()).doesNotContain("<script>").contains("&lt;script&gt;").doesNotContain("<b>");
        assertThat(enviado.cabecalhos()).containsKey("List-Unsubscribe");

        Matcher m = java.util.regex.Pattern.compile("descadastro\\?t=([A-Za-z0-9_-]+)").matcher(enviado.html());
        assertThat(m.find()).isTrue();
        mvc.perform(get("/publico/descadastro").param("t", m.group(1)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("não vai mais receber")));
        assertThat(usuarioRepository.findByEmail("aceita" + sufixo + "@teste.com").orElseThrow().isAceitaMarketing()).isFalse();
    }

    @Test
    void linkInvalidoNaoMudaNadaEUsuarioComumNaoEnvia() throws Exception {
        mvc.perform(get("/publico/descadastro").param("t", "inventado"))
                .andExpect(content().string(containsString("Link inválido")));
        mvc.perform(post("/admin/campanhas").with(user("x").roles("USUARIO")).contentType(MediaType.APPLICATION_JSON)
                .content("{\"assunto\":\"a\",\"mensagem\":\"b\"}")).andExpect(status().isForbidden());
    }
}

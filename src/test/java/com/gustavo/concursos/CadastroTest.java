package com.gustavo.concursos;

import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CadastroTest {

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;

    private String cadastro(String nome, String email, String senha, Boolean marketing) {
        return "{\"nome\":\"" + nome + "\",\"email\":\"" + email + "\",\"senha\":\"" + senha + "\""
                + (marketing == null ? "" : ",\"aceitaMarketing\":" + marketing) + "}";
    }

    private org.springframework.test.web.servlet.ResultActions registrar(String json) throws Exception {
        return mvc.perform(post("/auth/registrar").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void cadastroNovoFuncionaEOLoginEntraEmSeguida() throws Exception {
        registrar(cadastro("Ana", "ana.nova@teste.com", "Senha123!", null)).andExpect(status().isCreated());

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana.nova@teste.com\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void emailComMaiusculasEEspacosViraUmaContaSo() throws Exception {
        registrar(cadastro("Bruno", "  Bruno.Silva@Teste.COM ", "Senha123!", null)).andExpect(status().isCreated());
        assertThat(usuarioRepository.findByEmail("bruno.silva@teste.com")).isPresent();

        // Mesmo e-mail com outra caixa: e a mesma pessoa, nao uma conta nova.
        registrar(cadastro("Bruno", "bruno.silva@teste.com", "Outra123!", null))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("Já existe uma conta")));

        // E o login aceita o e-mail escrito de qualquer jeito.
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"BRUNO.SILVA@teste.com\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void errosDeValidacaoVemEmPortuguesSemTextoTecnico() throws Exception {
        registrar(cadastro("Carla", "carla@teste.com", "123", null))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A senha deve ter pelo menos 6 caracteres"))
                .andExpect(content().string(not(containsString("Validation failed"))));

        registrar(cadastro("Carla", "isso-nao-e-email", "Senha123!", null))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Informe um e-mail válido"));

        registrar(cadastro(" ", "carla@teste.com", "Senha123!", null))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Informe seu nome"));
    }

    @Test
    void marketingComecaDesmarcadoESoEntraQuemAceita() throws Exception {
        registrar(cadastro("Sem Aceite", "sem.aceite@teste.com", "Senha123!", null)).andExpect(status().isCreated());
        registrar(cadastro("Com Aceite", "com.aceite@teste.com", "Senha123!", true)).andExpect(status().isCreated());

        assertThat(usuarioRepository.findByEmail("sem.aceite@teste.com").orElseThrow().isAceitaMarketing()).isFalse();
        assertThat(usuarioRepository.findByEmail("com.aceite@teste.com").orElseThrow().isAceitaMarketing()).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void exportacaoTrazSoQuemConsentiu() throws Exception {
        registrar(cadastro("Quer Promo", "quer.promo@teste.com", "Senha123!", true)).andExpect(status().isCreated());
        registrar(cadastro("Nao Quer", "nao.quer@teste.com", "Senha123!", false)).andExpect(status().isCreated());

        mvc.perform(get("/admin/usuarios/contatos-marketing.csv"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("quer.promo@teste.com")))
                .andExpect(content().string(not(containsString("nao.quer@teste.com"))));
    }

    @Test
    @WithMockUser(roles = "USUARIO")
    void usuarioComumNaoExportaContatos() throws Exception {
        mvc.perform(get("/admin/usuarios/contatos-marketing.csv")).andExpect(status().isForbidden());
    }
}

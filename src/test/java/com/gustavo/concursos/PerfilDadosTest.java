package com.gustavo.concursos;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.pro.Pagamento;
import com.gustavo.concursos.pro.PagamentoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PerfilDadosTest {

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PagamentoRepository pagamentoRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String email;

    @BeforeEach
    void novoUsuario() {
        email = "perfil" + System.nanoTime() + "@teste.com";
        Usuario u = new Usuario();
        u.setNome("Pessoa");
        u.setEmail(email);
        u.setSenhaHash(passwordEncoder.encode("Senha123!"));
        usuarioRepository.save(u);
    }

    @Test
    void salvaDadosPessoaisEAForcaDoPerfilSobe() throws Exception {
        mvc.perform(get("/perfil/completo").with(user(email))).andExpect(jsonPath("$.forcaPerfil").value(20));
        mvc.perform(put("/perfil/dados").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Pessoa Teste\",\"uf\":\"mg\",\"carreiraAlvo\":\"Bancária\",\"bio\":\"Bacen\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uf").value("MG"))
                .andExpect(jsonPath("$.forcaPerfil").value(80));
        mvc.perform(put("/perfil/dados").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"X\",\"uf\":\"ZZ\"}")).andExpect(status().isBadRequest());
    }

    @Test
    void fotoSoAceitaImagem() throws Exception {
        mvc.perform(put("/perfil/foto").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"foto\":\"data:text/html;base64,PHNjcmlwdD4=\"}")).andExpect(status().isBadRequest());
        mvc.perform(put("/perfil/foto").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"foto\":\"data:image/png;base64,iVBORw0KGgo=\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.forcaPerfil").value(40));
    }

    @Test
    void zeraCategoriaEAConsultaContagens() throws Exception {
        mvc.perform(get("/perfil/dados-estudo").with(user(email))).andExpect(jsonPath("$.MARCADORES").value(0));
        mvc.perform(delete("/perfil/dados-estudo/TUDO").with(user(email))).andExpect(status().isOk());
        mvc.perform(delete("/perfil/dados-estudo/QUALQUER").with(user(email))).andExpect(status().isBadRequest());
    }

    @Test
    void excluirContaExigeSenhaEMantemOPagamentoSemVinculo() throws Exception {
        Usuario u = usuarioRepository.findByEmail(email).orElseThrow();
        Pagamento p = new Pagamento();
        p.setUsuario(u);
        p.setEmail(email);
        p.setPlano("MENSAL");
        p.setDias(30);
        p.setValorCentavos(2990);
        p.setStatus(Pagamento.PAGO);
        pagamentoRepository.save(p);

        mvc.perform(post("/perfil/excluir-conta").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"senha\":\"errada\"}")).andExpect(status().isBadRequest());
        assertThat(usuarioRepository.findByEmail(email)).isPresent();

        mvc.perform(post("/perfil/excluir-conta").with(user(email)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"senha\":\"Senha123!\"}")).andExpect(status().isNoContent());
        assertThat(usuarioRepository.findByEmail(email)).isEmpty();
        Pagamento guardado = pagamentoRepository.findById(p.getId()).orElseThrow();
        assertThat(guardado.getEmail()).isEqualTo(email);
    }
}

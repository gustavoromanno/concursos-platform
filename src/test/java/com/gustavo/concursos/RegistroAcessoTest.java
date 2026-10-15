package com.gustavo.concursos;

import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.UsuarioRepository;
import com.gustavo.concursos.security.RegistroAcessoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Marco Civil, art. 15: cada login guarda data, hora e IP; acima de 6 meses e apagado. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistroAcessoTest {

    @Autowired private MockMvc mvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private RegistroAcessoService registroAcesso;

    private Usuario criarUsuario() {
        Usuario u = new Usuario();
        u.setNome("Acesso");
        u.setEmail("acesso" + System.nanoTime() + "@teste.com");
        u.setSenhaHash(passwordEncoder.encode("Senha123!"));
        u.setEmailConfirmado(true);
        return usuarioRepository.save(u);
    }

    private long registrosDe(Long id) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM registro_acesso WHERE usuario_id = ?", Long.class, id);
    }

    @Test
    void loginComSucessoGravaDataHoraEIp() throws Exception {
        Usuario u = criarUsuario();
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Forwarded-For", "203.0.113.7, 10.0.0.1")
                        .content("{\"email\":\"" + u.getEmail() + "\",\"senha\":\"Senha123!\"}"))
                .andExpect(status().isOk());

        assertThat(registrosDe(u.getId())).isEqualTo(1);
        String ip = jdbc.queryForObject("SELECT ip FROM registro_acesso WHERE usuario_id = ?", String.class, u.getId());
        assertThat(ip).isEqualTo("203.0.113.7, 10.0.0.1");
    }

    @Test
    void senhaErradaNaoGravaRegistro() throws Exception {
        Usuario u = criarUsuario();
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + u.getEmail() + "\",\"senha\":\"Errada123!\"}"))
                .andExpect(status().isUnauthorized());
        assertThat(registrosDe(u.getId())).isZero();
    }

    @Test
    void limpezaApagaSoOQuePassouDeSeisMeses() {
        Usuario u = criarUsuario();
        jdbc.update("INSERT INTO registro_acesso (usuario_id, ip, criado_em) VALUES (?, ?, ?)",
                u.getId(), "198.51.100.1", LocalDateTime.now().minusMonths(7));
        jdbc.update("INSERT INTO registro_acesso (usuario_id, ip, criado_em) VALUES (?, ?, ?)",
                u.getId(), "198.51.100.2", LocalDateTime.now().minusMonths(5));

        registroAcesso.apagarVencidos();

        assertThat(registrosDe(u.getId())).isEqualTo(1);
        String restante = jdbc.queryForObject("SELECT ip FROM registro_acesso WHERE usuario_id = ?", String.class, u.getId());
        assertThat(restante).isEqualTo("198.51.100.2");
    }
}

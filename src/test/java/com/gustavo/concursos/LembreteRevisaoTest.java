package com.gustavo.concursos;

import com.gustavo.concursos.email.EmailCliente;
import com.gustavo.concursos.email.LembreteRevisaoService;
import com.gustavo.concursos.entity.Questao;
import com.gustavo.concursos.entity.Revisao;
import com.gustavo.concursos.entity.Usuario;
import com.gustavo.concursos.repository.BancaRepository;
import com.gustavo.concursos.repository.DisciplinaRepository;
import com.gustavo.concursos.repository.QuestaoRepository;
import com.gustavo.concursos.repository.RevisaoRepository;
import com.gustavo.concursos.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "lembrete.cron=-")   // desliga o agendamento durante o teste
@Import(LembreteRevisaoTest.EmailFalso.class)
class LembreteRevisaoTest {

    static final List<String[]> ENVIADOS = new CopyOnWriteArrayList<>();

    @TestConfiguration
    static class EmailFalso {
        @Bean
        @Primary
        EmailCliente emailFalso() {
            return (para, assunto, html) -> ENVIADOS.add(new String[]{para, assunto});
        }
    }

    @Autowired private LembreteRevisaoService servico;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RevisaoRepository revisaoRepository;
    @Autowired private DisciplinaRepository disciplinaRepository;
    @Autowired private BancaRepository bancaRepository;
    @Autowired private QuestaoRepository questaoRepository;

    private Usuario usuario(boolean lembrete, boolean pro) {
        Usuario u = new Usuario();
        u.setNome("Aluno Teste");
        u.setEmail("lembrete" + System.nanoTime() + "@teste.com");
        u.setSenhaHash("x");
        u.setLembreteRevisao(lembrete);
        if (pro) u.setProAte(LocalDateTime.now().plusDays(10));
        return usuarioRepository.save(u);
    }

    private void revisaoVencida(Usuario u, Questao q) {
        Revisao r = new Revisao();
        r.setUsuario(u);
        r.setQuestao(q);
        r.setProximaRevisao(LocalDate.now().minusDays(1));
        revisaoRepository.save(r);
    }

    @Test
    void soQuemLigouEProETemRevisaoRecebe() {
        Questao q = ReporteQuestaoTest.questaoDeTeste(disciplinaRepository, bancaRepository, questaoRepository);
        Usuario certo = usuario(true, true);
        Usuario semLembrete = usuario(false, true);
        Usuario gratuito = usuario(true, false);
        Usuario semPendencia = usuario(true, true);
        revisaoVencida(certo, q);
        revisaoVencida(semLembrete, q);
        revisaoVencida(gratuito, q);

        ENVIADOS.clear();
        servico.enviar(LocalDate.now());

        assertThat(ENVIADOS).extracting(e -> e[0]).containsExactly(certo.getEmail());
        assertThat(ENVIADOS.get(0)[1]).isEqualTo("1 questão para revisar hoje");
        assertThat(semPendencia.getId()).isNotNull();
    }
}

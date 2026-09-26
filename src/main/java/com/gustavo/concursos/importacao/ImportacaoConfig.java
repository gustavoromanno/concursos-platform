package com.gustavo.concursos.importacao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

// Uma importacao por vez: evita estourar o limite de taxa da API e o pool de
// conexoes pequeno do banco (plano gratuito). As demais esperam na fila.
@Configuration
@EnableAsync
public class ImportacaoConfig {

    @Bean(name = "importacaoExecutor")
    public Executor importacaoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("importacao-");
        executor.initialize();
        return executor;
    }
}

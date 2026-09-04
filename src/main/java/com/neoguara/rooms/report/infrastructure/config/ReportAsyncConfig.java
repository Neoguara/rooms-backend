package com.neoguara.rooms.report.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Liga o processamento assíncrono e define o pool que gera os relatórios.
 *
 * <p>O pool é pequeno e a fila é limitada de propósito. Gerar relatório é trabalho pesado e
 * ilimitado por natureza — um intervalo de dez anos varre a agenda inteira —, e sem teto alguns
 * pedidos simultâneos consumiriam as conexões do pool do banco e derrubariam o resto da API junto.
 *
 * <p>Como este é hoje o único trabalho assíncrono da aplicação, o executor vale para todo
 * {@code @Async}. Se surgir outro tipo de tarefa, vale separar em executores por finalidade em vez
 * de deixar as duas disputarem as mesmas threads.
 */
@Configuration
@EnableAsync
public class ReportAsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("report-");
        executor.initialize();
        return executor;
    }
}

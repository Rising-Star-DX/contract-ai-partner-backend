package com.partner.contract.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "gptAsyncExecutor")
    public Executor gptAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);  // 기본적으로 실행될 스레드 개수
        executor.setMaxPoolSize(8);  // 최대 스레드 개수
        executor.setQueueCapacity(20); // 큐에 저장할 태스크 개수
        executor.setThreadNamePrefix("GPT-Async-");
        executor.initialize();
        return executor;
    }
}

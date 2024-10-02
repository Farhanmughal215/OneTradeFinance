package com.xstocks.agent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @ClassName AsyncConfig
 * @Description TODO
 * @Author firtuss
 * @Date 2023/8/21 16:48
 **/
@Configuration
@EnableAsync
public class AsyncConfig {
/*
    @Bean("eventHandleExecutor")
    public Executor eventHandleExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setThreadNamePrefix("eventHandleExecutor-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
    */
}

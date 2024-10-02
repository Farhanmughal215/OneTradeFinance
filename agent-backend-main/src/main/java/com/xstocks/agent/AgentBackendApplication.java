package com.xstocks.agent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.xstocks.agent.mapper")
@SpringBootApplication
public class AgentBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentBackendApplication.class, args);
    }

}

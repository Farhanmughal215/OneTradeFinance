package com.xstocks.uc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UCApplication {

    public static void main(String[] args) {
        SpringApplication.run(UCApplication.class, args);
    }

}

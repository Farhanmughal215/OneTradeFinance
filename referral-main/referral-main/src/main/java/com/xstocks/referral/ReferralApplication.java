package com.xstocks.referral;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.xstocks.referral.mapper")
@SpringBootApplication
public class ReferralApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReferralApplication.class, args);
    }

}

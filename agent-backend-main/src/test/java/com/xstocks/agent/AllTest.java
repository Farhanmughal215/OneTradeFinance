package com.xstocks.agent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootTest
@ActiveProfiles(value = "debug")
public class AllTest {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void stockTest() {

    }

}

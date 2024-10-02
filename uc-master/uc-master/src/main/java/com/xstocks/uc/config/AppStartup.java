package com.xstocks.uc.config;

import com.xstocks.uc.service.remote.PolygoWsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @ClassName AppStartup
 * @Description TODO
 * @Author firtuss
 * @Date 2023/9/5 17:21
 **/
@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.task-config", value = "enable", havingValue = "true")
public class AppStartup implements ApplicationRunner {

    @Autowired
    private PolygoWsService polygoWsService;

    @Override
    public void run(ApplicationArguments args) {
//            polygoWsService.subscribe();
    }


}

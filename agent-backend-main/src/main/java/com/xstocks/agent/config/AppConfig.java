package com.xstocks.agent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName NodeProvider
 * @Description TODO
 * @Author firtuss
 * @Date 2022/11/9 14:39
 **/

@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    private TaskConfig taskConfig;

    @Data
    public static class TaskConfig {
        private Boolean enable;
    }

    private OkHttpConfig okhttpConfig;

    @Data
    public static class OkHttpConfig {
        private Long connectTimeout;
        private Long readTimeout;
        private Long writeTimeout;
        private Integer maxIdleConnections;
        private Long keepAliveDuration;
    }
}

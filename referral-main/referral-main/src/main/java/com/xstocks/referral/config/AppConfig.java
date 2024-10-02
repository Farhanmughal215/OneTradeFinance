package com.xstocks.referral.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

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

    private List<Product> productConfig;

    @Getter
    @Setter
    public static class Product {
        private String product;
        private String host;
        private String aapi;
        private String capi;
        private String bapi;
    }

    private RabbitmqConfig rabbitmqConfig;

    @Getter
    @Setter
    public static class RabbitmqConfig {
        private ProductRabbitmqConfig user;
        private ProductRabbitmqConfig deposit;
        private ProductRabbitmqConfig withdraw;
    }

    @Getter
    @Setter
    public static class ProductRabbitmqConfig {
        private String exchange;
        private String queueOrder;
        private String queueAi;
    }

    private AliyunConfig aliyunConfig;

    @Getter
    @Setter
    public static class AliyunConfig {
        private String ak;
        private String sk;
        private OssConfig ossConfig;
    }

    @Getter
    @Setter
    public static class OssConfig {
        private String endPoint;
        private String bucket;
    }
}

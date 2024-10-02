package com.xstocks.uc.config;

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
        private ProductRabbitmqConfig refund;
    }

    @Getter
    @Setter
    public static class ProductRabbitmqConfig {
        private String exchange;
        private String queueOrder;
        private String queueAi;
    }

    private JwtConfig jwtConfig;

    @Getter
    @Setter
    public static class JwtConfig {
        private String secretKey;
        private Long userExpireSeconds;
        private Long adminExpireSeconds;
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

    private MarketauxConfig marketauxConfig;

    @Getter
    @Setter
    public static class MarketauxConfig {
        private String apiKey;
        private String newsUrl;
        private String economyUrl;
        private String seven24Url;
        private Integer totalPage;
        private String language;
    }

    private StompConfig stompConfig;

    @Data
    public static class StompConfig {
        private String endpoint;
        private String applicationDestinationPrefix;
        private RelayConfig relay;
    }

    @Data
    public static class RelayConfig {
        private String host;
        private Integer port;
        private String login;
        private String passcode;
        private String[] brokerDestinationPrefix;
    }

    private PolygoConfig polygoConfig;

    @Data
    public static class PolygoConfig {
        private String apiKey;
        private Integer aggregatesPageSize;
        private String aggregatesUrl;
        private String latestDayUrl;
        private String latestTradeUrl;
        private String latestQuoteUrl;
        private String latestTickerUrl;
        private String wsUrl;
    }


    private TelesignConfig telesignConfig;

    @Data
    public static class TelesignConfig {
        private String customerId;
        private String apiKey;
        private String verifySms;
        private String verifyInitiate;
    }

    private ZeroBounceConfig zeroBounceConfig;

    @Data
    public static class ZeroBounceConfig{
        private String apiKey;
    }
}

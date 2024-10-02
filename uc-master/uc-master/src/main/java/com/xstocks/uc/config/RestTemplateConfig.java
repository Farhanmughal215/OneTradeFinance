package com.xstocks.uc.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName RestTemplateConfig
 * @Description okhttp3的RestTemplate
 * @Author firtuss
 * @Date 2022/11/8 15:08
 **/
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Autowired
    private AppConfig appConfig;

    @Bean
    public RestTemplate okHttpTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        // 可以添加消息转换
        //restTemplate.setMessageConverters(...);
        // 可以增加拦截器
        //restTemplate.setInterceptors(...);
        return restTemplate;
    }

    public ClientHttpRequestFactory httpRequestFactory() {
        return new OkHttp3ClientHttpRequestFactory(okHttpConfigClient());
    }

    public OkHttpClient okHttpConfigClient() {
        return new OkHttpClient().newBuilder()
                .connectionPool(pool())
                .connectTimeout(appConfig.getOkhttpConfig().getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(appConfig.getOkhttpConfig().getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(appConfig.getOkhttpConfig().getWriteTimeout(), TimeUnit.MILLISECONDS)
                .hostnameVerifier((hostname, session) -> true)
                // 设置代理
                // .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
                // 拦截器
                // .addInterceptor()
                .build();
    }

    public ConnectionPool pool() {
        return new ConnectionPool(appConfig.getOkhttpConfig().getMaxIdleConnections(),
                appConfig.getOkhttpConfig().getKeepAliveDuration(),
                TimeUnit.SECONDS);
    }
}

package com.xstocks.uc.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xstocks.uc.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtManager {

    @Autowired
    private AppConfig appConfig;

    /**
     * 生成JWT
     *
     * @param username 用户名
     * @return JWT
     */
    public String generate(String username, Optional<Long> inputExpireSeconds) {
        // 过期时间
        Date expiryDate =
                new Date(System.currentTimeMillis() +
                        Duration.ofSeconds(inputExpireSeconds.orElse(appConfig.getJwtConfig().getUserExpireSeconds())).toMillis());

        Algorithm algorithm = Algorithm.HMAC256(appConfig.getJwtConfig().getSecretKey());
        return JWT.create()
                .withExpiresAt(expiryDate)
                .withSubject(username)
                .sign(algorithm);
    }

    /**
     * 解析JWT
     *
     * @param token JWT字符串
     * @return 解析成功返回Claims对象，解析失败返回null
     */
    public DecodedJWT parse(String token) {
        // 如果是空字符串直接返回null
        if (!StringUtils.hasLength(token)) {
            return null;
        }
        return JWT.decode(token);
    }
}

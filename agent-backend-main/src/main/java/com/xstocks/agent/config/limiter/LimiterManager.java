package com.xstocks.agent.config.limiter;

import com.xstocks.agent.config.LuaScriptConfig;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author firtuss
 */
@Slf4j
@Component
public class LimiterManager {

    private final ConcurrentMap<String, RedisRateLimiter> limitCache = new ConcurrentHashMap<>();

    final LuaScriptConfig luaScriptConfig;
    final RedissonClient redissonClient;

    public LimiterManager(RedissonClient redissonClient, LuaScriptConfig luaScriptConfig) {
        this.luaScriptConfig = luaScriptConfig;
        this.redissonClient = redissonClient;
    }

    public RedisRateLimiter getLimiter(String limitKey, Long rateCorrespondingMs, Long rate) {
        return limitCache.computeIfAbsent(limitKey, k -> {
            log.info("create new RedisRateLimiter k: {}", k);
            return new RedisRateLimiter(redissonClient, k, luaScriptConfig.getScriptSha(), rateCorrespondingMs, rate);
        });
    }
}

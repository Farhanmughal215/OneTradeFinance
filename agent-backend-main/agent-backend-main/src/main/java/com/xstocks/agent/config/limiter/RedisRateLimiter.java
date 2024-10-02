package com.xstocks.agent.config.limiter;

import com.xstocks.agent.common.CommonConstant;
import com.xstocks.agent.exception.BizException;
import com.xstocks.agent.pojo.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author firtuss
 */
@Slf4j
public class RedisRateLimiter {

    private final RedissonClient redissonClient;
    private final List<Object> keys = new ArrayList<>(16);
    private final String rateLimitKey;
    private final String script;
    private final String rateConfField;
    private final String rateMsConfField;
    private final Long rateCorrespondingMs;
    private final Long rate;

    public RedisRateLimiter(RedissonClient redissonClient, String rateLimitKey, String script) {
        this(redissonClient, rateLimitKey, script, CommonConstant.DEFAULT_RATE_CORRESPONDING_MS, CommonConstant.DEFAULT_RATE);
    }

    public RedisRateLimiter(RedissonClient redissonClient, String rateLimitKey, String script, Long rateCorrespondingMs, Long rate) {
        this.redissonClient = redissonClient;
        this.rateLimitKey = rateLimitKey;
        this.script = script;
        this.rateConfField = String.format("%s-%s", rateLimitKey, CommonConstant.RATE_LIMIT_FIELD);
        this.rateMsConfField = String.format("%s-%s", rateLimitKey, CommonConstant.RATE_MS_FIELD);
        this.rateCorrespondingMs = rateCorrespondingMs;
        this.rate = rate;
        start();
    }


    public void acquire() throws BizException {
        long ms = redissonClient.getScript(StringCodec.INSTANCE).eval(RScript.Mode.READ_WRITE, script, RScript.ReturnType.INTEGER, keys, System.currentTimeMillis());
        if (ms > 0) {
            throw new BizException(ErrorCode.TOO_MANY_REQUESTS, String.format("retry after: %d ms", ms));
        }
    }



    public void start() {
        keys.add(CommonConstant.RATE_LIMIT_KEY);
        keys.add(rateLimitKey);
        keys.add(rateConfField);
        keys.add(rateMsConfField);

        RMap<String, String> map = redissonClient.getMap(CommonConstant.RATE_LIMIT_KEY, StringCodec.INSTANCE);
        map.put(rateConfField, String.valueOf(rate));
        map.put(rateMsConfField, String.valueOf(rateCorrespondingMs));
    }

    public long getIntervalMs() {
        return rateCorrespondingMs / rate;
    }

    public void setRate(Long rate, Long rateMs) {
        if (Objects.isNull(rate) && Objects.isNull(rateMs)) {
            return;
        }
        RMap<String, String> map = redissonClient.getMap(CommonConstant.RATE_LIMIT_KEY, StringCodec.INSTANCE);
        if (Objects.nonNull(rate) && rate > 0) {
            map.put(rateConfField, String.valueOf(rate));
        }
        if (Objects.nonNull(rateMs) && rateMs > 0) {
            map.put(rateMsConfField, String.valueOf(rateMs));
        }
    }
}

package com.xstocks.uc.task;

import com.google.common.util.concurrent.RateLimiter;
import com.xstocks.uc.pojo.constants.CommonConstant;
import static com.xstocks.uc.pojo.constants.CommonConstant.LOCAL_CACHE_ALL_TICKER;
import com.xstocks.uc.pojo.dto.polygo.TickerRefreshDTO;
import com.xstocks.uc.pojo.dto.ticker.TickerAbbreviationDTO;
import com.xstocks.uc.service.remote.PolygoService;
import com.xstocks.uc.utils.IpHelper;
import com.xstocks.uc.utils.JsonUtil;
import com.xstocks.uc.utils.LocalCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName TickerStateRefreshDaily
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/16 17:37
 **/

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.task-config", value = "enable", havingValue = "true")
public class RefreshTickerStateDailyTask {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PolygoService polygoService;

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(10);

    @Scheduled(cron = "0 0 7 * * ?")
    public void refreshTickerState() {
        String nowStr = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        String lockValue = String.format("%s:%s:%s", IpHelper.getIp(), Thread.currentThread().getName(), nowStr);

        if (Boolean.TRUE.equals(
                redisTemplate.boundValueOps(CommonConstant.TICKER_REFRESH_DAILY_LOCK).setIfAbsent(lockValue, 1,
                        TimeUnit.SECONDS))) {
            Map<String, TickerAbbreviationDTO> tickerAbbreviationDTOMap =
                    LocalCacheUtil.<String, Map<String, TickerAbbreviationDTO>>getLoadingCache(LOCAL_CACHE_ALL_TICKER)
                            .get(LOCAL_CACHE_ALL_TICKER);
            if (MapUtils.isNotEmpty(tickerAbbreviationDTOMap)) {
                tickerAbbreviationDTOMap.values()
                        .parallelStream()
                        .forEach(
                                tickerAbbreviationDTO -> {
                                    try {
                                        RATE_LIMITER.acquire();
                                        TickerRefreshDTO tickerRefreshDTO =
                                                polygoService.getTickerRefreshDTO(tickerAbbreviationDTO.getId(),
                                                        tickerAbbreviationDTO.getSymbol());
                                        redisTemplate.opsForHash().putAll("prev:" + tickerAbbreviationDTO.getId(),
                                                JsonUtil.convertPojo2Map(tickerRefreshDTO.getPreDay()));
                                        redisTemplate.opsForHash().putAll("latest:" + tickerAbbreviationDTO.getId(),
                                                JsonUtil.convertPojo2Map(tickerRefreshDTO.getTickerLatestStateDTO()));
                                    } catch (Exception ex) {
                                        log.warn("refreshTickerState_ex_" + tickerAbbreviationDTO.getId(), ex);
                                    }
                                }
                        );
            }
        }
    }
}

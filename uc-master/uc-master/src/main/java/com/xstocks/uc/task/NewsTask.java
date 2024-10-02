package com.xstocks.uc.task;

import com.google.common.base.Splitter;
import com.google.common.util.concurrent.RateLimiter;
import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.pojo.Information;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.dto.marketaux.PageDTO;
import com.xstocks.uc.service.InformationService;
import com.xstocks.uc.utils.IpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.task-config", value = "enable", havingValue = "true")
public class NewsTask {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private InformationService informationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(1);

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    void pullNews() {
        log.info("开始更新news数据");
        String nowStr = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        String lockValue = String.format("%s:%s:%s", IpHelper.getIp(), Thread.currentThread().getName(), nowStr);

        String nowBefor24HoursStr =
                LocalDateTime.now(ZoneOffset.UTC).minusHours(24).format(DateTimeFormatter.ofPattern("yyyy-MM" +
                        "-dd'T'HH:mm"));

        if (Boolean.TRUE.equals(redisTemplate.boundValueOps(CommonConstant.PULL_NEWS_LOCK).setIfAbsent(lockValue, 9,
                TimeUnit.MINUTES))) {
            Splitter.on(",").split(appConfig.getMarketauxConfig().getLanguage()).forEach(
                    lang -> {
                        List<Information> list = new ArrayList<>();
                        Set<ZSetOperations.TypedTuple<Object>> data = new HashSet<>();
                        AtomicInteger newsScore = new AtomicInteger(1);
                        IntStream.range(1, appConfig.getMarketauxConfig().getTotalPage() + 1).forEach(
                                page -> {
                                    PageDTO newsPageDto =
                                            pullPage(appConfig.getMarketauxConfig().getNewsUrl(), lang, page,
                                                    nowBefor24HoursStr);
                                    if (Objects.nonNull(newsPageDto) &&
                                            CollectionUtils.isNotEmpty(newsPageDto.getData())) {
                                        for (PageDTO.NewsItem newsItem : newsPageDto.getData()) {
                                            list.add(new Information(newsItem, 1, lang));
                                            data.add(new DefaultTypedTuple<>(newsItem,
                                                    NumberUtils.toDouble(newsScore.toString())));
                                            newsScore.getAndIncrement();
                                        }
                                    }
                                });
                        if (!data.isEmpty()) {
                            redisTemplate.delete("news:" + lang);
                            redisTemplate.opsForZSet().add("news:" + lang, data);
                        }
                        if (list.size() > 0) {
                            informationService.delete(1, lang);
                            informationService.saveBatch(list);
                        }
                        log.info("更新news数据结束");


                        /*
                        Set<ZSetOperations.TypedTuple<Object>> data = new HashSet<>();
                        IntStream.range(1, appConfig.getMarketauxConfig().getTotalPage() + 1).forEach(
                                page -> {
                                    PageDTO newsPageDto =
                                            pullPage(appConfig.getMarketauxConfig().getNewsUrl(), lang, page,
                                                    nowBefor24HoursStr);
                                    if (Objects.nonNull(newsPageDto) &&
                                            CollectionUtils.isNotEmpty(newsPageDto.getData())) {
                                        for (PageDTO.NewsItem newsItem : newsPageDto.getData()) {
                                            data.add(new DefaultTypedTuple<>(newsItem,
                                                    NumberUtils.toDouble(newsScore.toString())));
                                            newsScore.getAndIncrement();
                                        }
                                    }
                                });
                        if (!data.isEmpty()) {
                            redisTemplate.delete("news:" + lang);
                            redisTemplate.opsForZSet().add("news:" + lang, data);
                        }
                         */
                    });
        }
    }

    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    void pullEconomy() {
        log.info("更新economy数据结束");
        String nowStr = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        String lockValue = String.format("%s:%s:%s", IpHelper.getIp(), Thread.currentThread().getName(), nowStr);

        String nowBeforSeven24HoursStr =
                LocalDateTime.now(ZoneOffset.UTC).minusHours(24 * 7).format(DateTimeFormatter.ofPattern("yyyy-MM" +
                        "-dd'T'HH:mm"));

        if (Boolean.TRUE.equals(redisTemplate.boundValueOps(CommonConstant.PULL_ECONOMY_LOCK).setIfAbsent(lockValue, 14,
                TimeUnit.MINUTES))) {
            Set<ZSetOperations.TypedTuple<Object>> data = new HashSet<>();
            AtomicInteger economyScore = new AtomicInteger(1);
            List<Information> list = new ArrayList<>();
            IntStream.range(1, appConfig.getMarketauxConfig().getTotalPage() + 1).forEach(
                    page -> {
                        PageDTO economyPageDto =
                                pullPage(appConfig.getMarketauxConfig().getEconomyUrl(), StringUtils.EMPTY, page,
                                        nowBeforSeven24HoursStr);
                        if (Objects.nonNull(economyPageDto) &&
                                CollectionUtils.isNotEmpty(economyPageDto.getData())) {
                            for (PageDTO.NewsItem newsItem : economyPageDto.getData()) {
                                list.add(new Information(newsItem, 2, "global"));
                                data.add(new DefaultTypedTuple<>(newsItem,
                                        NumberUtils.toDouble(economyScore.toString())));
                                economyScore.getAndIncrement();
                            }
                        }
                    }
            );
            if (!data.isEmpty()) {
                redisTemplate.delete("economy:global");
                redisTemplate.opsForZSet().add("economy:global", data);
            }
            if (list.size() > 0) {
                informationService.delete(2, "global");
                informationService.saveBatch(list);
            }
            log.info("更新economy数据结束");
        }
    }

    @Scheduled(fixedDelay = 20, timeUnit = TimeUnit.MINUTES)
    void pullSeven24Hours() {
        log.info("开始更新seven14h数据");
        String nowStr = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        String lockValue = String.format("%s:%s:%s", IpHelper.getIp(), Thread.currentThread().getName(), nowStr);

        String nowBeforSeven24HoursStr =
                LocalDateTime.now(ZoneOffset.UTC).minusHours(24 * 7).format(DateTimeFormatter.ofPattern("yyyy-MM" +
                        "-dd'T'HH:mm"));

        if (Boolean.TRUE.equals(
                redisTemplate.boundValueOps(CommonConstant.PULL_SEVEN24HOURS_LOCK).setIfAbsent(lockValue, 19,
                        TimeUnit.MINUTES))) {
            Splitter.on(",").split(appConfig.getMarketauxConfig().getLanguage()).forEach(
                    lang -> {
                        AtomicInteger seven24Score = new AtomicInteger(1);
                        List<Information> list = new ArrayList<>();
                        Set<ZSetOperations.TypedTuple<Object>> data = new HashSet<>();
                        IntStream.range(1, appConfig.getMarketauxConfig().getTotalPage() + 1).forEach(
                                page -> {
                                    PageDTO seven24PageDto =
                                            pullPage(appConfig.getMarketauxConfig().getSeven24Url(), lang, page,
                                                    nowBeforSeven24HoursStr);
                                    if (Objects.nonNull(seven24PageDto) &&
                                            CollectionUtils.isNotEmpty(seven24PageDto.getData())) {
                                        for (PageDTO.NewsItem newsItem : seven24PageDto.getData()) {
                                            list.add(new Information(newsItem, 3, lang));
                                            data.add(new DefaultTypedTuple<>(newsItem,
                                                    NumberUtils.toDouble(seven24Score.toString())));
                                            seven24Score.getAndIncrement();
                                        }
                                    }
                                });
                        if (!data.isEmpty()) {
                            redisTemplate.delete("seven24:" + lang);
                            redisTemplate.opsForZSet().add("seven24:" + lang, data);
                        }
                        if (list.size() > 0) {
                            informationService.delete(3, lang);
                            informationService.saveBatch(list);
                        }
                    });
        }
        log.info("更新seven14h数据结束");
    }

    private PageDTO pullPage(String urlTpl, String lang, int page, String nowStr) {
        RATE_LIMITER.acquire();
        String url = urlTpl.replace("{lang}", lang)
                .replace("{page_no}", page + "")
                .replace("{now}", nowStr);
        try {
            ResponseEntity<PageDTO> responseEntity = restTemplate.getForEntity(url, PageDTO.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        } catch (Throwable tx) {
//            log.error("Marketaux error:", tx);
        }
        return null;
    }
}

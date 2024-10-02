package com.xstocks.uc.controller;

import com.google.common.base.Splitter;
import com.xstocks.uc.aspect.RateLimit;
import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.NewsQueryParam;
import com.xstocks.uc.pojo.vo.BaseResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RestController
public class NewsController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RateLimit(rate = 1000L)
    @PostMapping("/a/n/l")
    public BaseResp<Set<Object>> getNews(@RequestBody NewsQueryParam newsQueryParam) {

        if (StringUtils.isBlank(newsQueryParam.getLang())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "lang required");
        }
        if (Objects.isNull(newsQueryParam.getNewsType())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "newsType required");
        }
        if (Objects.isNull(newsQueryParam.getPageNo())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "pageNo required");
        }
        if (Objects.isNull(newsQueryParam.getPageSize())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "pageSize required");
        }
        Set<String> allowedLangSet =
                new HashSet<>(Splitter.on(",").splitToList(appConfig.getMarketauxConfig().getLanguage()));
        if (!allowedLangSet.contains(newsQueryParam.getLang())) {
            newsQueryParam.setLang(CommonConstant.DEFAULT_LANG);
        }

        Set<Object> result;

        if (1 == newsQueryParam.getNewsType()) {
            result = redisTemplate.opsForZSet().range("news:" + newsQueryParam.getLang(),
                    (newsQueryParam.getPageNo() - 1) * newsQueryParam.getPageSize() + 1,
                    newsQueryParam.getPageNo() * newsQueryParam.getPageSize());
        } else if (2 == newsQueryParam.getNewsType()) {
            result = redisTemplate.opsForZSet()
                    .range("economy:global", (newsQueryParam.getPageNo() - 1) * newsQueryParam.getPageSize() + 1,
                            newsQueryParam.getPageNo() * newsQueryParam.getPageSize());
        } else if (3 == newsQueryParam.getNewsType()) {
            result = redisTemplate.opsForZSet().range("seven24:" + newsQueryParam.getLang(),
                    (newsQueryParam.getPageNo() - 1) * newsQueryParam.getPageSize() + 1,
                    newsQueryParam.getPageNo() * newsQueryParam.getPageSize());
        } else {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "param newsType illegal");
        }
        return BaseResp.success(result);
    }

}

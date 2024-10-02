package com.xstocks.uc.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.base.Splitter;
import com.xstocks.uc.aspect.RateLimit;
import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.Information;
import com.xstocks.uc.pojo.constants.CommonConstant;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.NewsQueryParam;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.service.InformationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * 资讯表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2024-01-14
 */
@Slf4j
@RestController
public class InformationController {


    @Autowired
    private AppConfig appConfig;

    @Autowired
    private InformationService informationService;

    @RateLimit(rate = 1000L)
    @PostMapping("/a/n/information")
    public BaseResp<IPage<Information>> getNews(@RequestBody NewsQueryParam newsQueryParam) {
        if (StringUtils.isBlank(newsQueryParam.getLang())) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "lang required");
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

        return informationService.queryInformation(newsQueryParam);
    }
}

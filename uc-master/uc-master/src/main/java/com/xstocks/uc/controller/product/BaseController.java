package com.xstocks.uc.controller.product;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public abstract class BaseController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    protected abstract String getProduct();

    protected AppConfig.Product getProductService() {
        return appConfig.getProductConfig().stream().filter(x -> getProduct().equals(x.getProduct()))
                .collect(Collectors.toList()).get(0);
    }

    protected String getUrlPath(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String matchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return antPathMatcher.extractPathWithinPattern(matchPattern, path);
    }

    protected BaseResp getBaseResp(String api, ObjectNode jsonObject, UserPO currentLoginUser, String apiPath) {
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);

        if (Objects.isNull(jsonObject)) {
            jsonObject = JsonUtil.createObjectNode();
        }

        if (Objects.nonNull(currentLoginUser)) {
            jsonObject.put("uid", currentLoginUser.getId());
            jsonObject.put("bizId", currentLoginUser.getBizId());
            jsonObject.put("userStatus", currentLoginUser.getUserStatus());
        }

        HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(jsonObject, reqHeaders);

        String url = getProductService().getHost() + apiPath + api;
        log.info("转发{}的地址:{}", getProduct(), url);
        try {
            return restTemplate
                    .postForEntity(url, httpEntity, BaseResp.class)
                    .getBody();
        } catch (RestClientException ex) {
            log.error("转发{} ,url:{} 异常:{}", getProduct(), url, ex.getMessage());
            throw new BizException(ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }

}

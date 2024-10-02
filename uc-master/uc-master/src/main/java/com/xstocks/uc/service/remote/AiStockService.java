package com.xstocks.uc.service.remote;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.pojo.dto.aistock.AiStockUserBalance;
import com.xstocks.uc.pojo.dto.order.OrderUserBalance;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.pojo.vo.UserVO;
import com.xstocks.uc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiStockService {
    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    public AiStockUserBalance getOrderBalance(UserVO userVO) {
        AppConfig.Product product =
                appConfig.getProductConfig().stream().filter(x -> "aistock".equals(x.getProduct()))
                        .collect(Collectors.toList()).get(0);

        String url = product.getHost() + product.getBapi() + "userFinance";

        ObjectNode reqBody = JsonUtil.createObjectNode();
        reqBody.put("uid", userVO.getId());
        reqBody.put("bizId", userVO.getBizId());
        reqBody.put("userStatus", userVO.getUserStatus());

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ObjectNode> httpEntity = new HttpEntity<ObjectNode>(reqBody, reqHeaders);

        ParameterizedTypeReference<BaseResp<AiStockUserBalance>> typeRef =
                new ParameterizedTypeReference<BaseResp<AiStockUserBalance>>() {
                };

        BaseResp<AiStockUserBalance> result = null;
        try {
            result = restTemplate
                    .exchange(url, HttpMethod.POST, httpEntity, typeRef)
                    .getBody();
        } catch (Exception ex) {
            log.error("getOrderBalance_ex:", ex);
        }
        return Objects.nonNull(result) ? result.getData() : new AiStockUserBalance();
    }
}

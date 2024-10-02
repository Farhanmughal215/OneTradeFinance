package com.xstocks.uc.service.remote;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.config.AppConfig;
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

/**
 * @ClassName OrderService
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/15 16:21
 **/

@Slf4j
@Service
public class OrderService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    public OrderUserBalance getOrderBalance(UserVO userVO) {
        AppConfig.Product tickerProduct =
                appConfig.getProductConfig().stream().filter(x -> "order".equals(x.getProduct()))
                        .collect(Collectors.toList()).get(0);

        String url = tickerProduct.getHost() + tickerProduct.getCapi() + "balance";

        ObjectNode reqBody = JsonUtil.createObjectNode();
        reqBody.put("uid", userVO.getId());
        reqBody.put("bizId", userVO.getBizId());
        reqBody.put("userStatus", userVO.getUserStatus());

        HttpHeaders reqHeaders = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json");
        reqHeaders.setContentType(type);

        HttpEntity<ObjectNode> httpEntity = new HttpEntity<ObjectNode>(reqBody, reqHeaders);

        ParameterizedTypeReference<BaseResp<OrderUserBalance>> typeRef =
                new ParameterizedTypeReference<BaseResp<OrderUserBalance>>() {
                };

        BaseResp<OrderUserBalance> result = null;
        try {
            result = restTemplate
                    .exchange(url, HttpMethod.POST, httpEntity, typeRef)
                    .getBody();
        } catch (Exception ex) {
            log.error("getOrderBalance_ex:", ex);
        }
        return Objects.nonNull(result) ? result.getData() : new OrderUserBalance();
    }
}

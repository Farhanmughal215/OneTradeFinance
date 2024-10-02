package com.xstocks.uc.service.remote;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.common.Constants;
import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.utils.JsonUtil;
import com.xstocks.uc.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TelesignService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    public boolean sendSmsVerify(String phoneNo) {
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        reqHeaders.set("accept", "application/json");
        reqHeaders.set("authorization", createBasicAuthString());

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        int num = (int) ((Math.random() * 9 + 1) * 100000);
        String verifyCode = num + StringUtils.EMPTY;
        requestBody.add("is_primary", "true");
        requestBody.add("phone_number", phoneNo);
        requestBody.add("verify_code", verifyCode);

        HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, reqHeaders);

        try {
            ResponseEntity<ObjectNode> responseEntity = restTemplate.exchange(appConfig.getTelesignConfig().getVerifySms(), HttpMethod.POST, requestEntity, ObjectNode.class);
            log.info("sendSmsVerify_return:" + responseEntity.getBody().toPrettyString());
            redisTemplate.opsForValue().set(Constants.VERIFY_CODE_PREFIX + phoneNo, verifyCode, 121L, TimeUnit.SECONDS);
            return true;
        } catch (Exception ex) {
            log.error("sendSmsVerify_ex", ex);
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, LogUtil.getExceptionToString(ex));
        }
    }

    public void initiateVerify(String phoneNo) {
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        reqHeaders.set("accept", "application/json");
        reqHeaders.set("authorization", createBasicAuthString());

        ObjectNode requestBody = JsonUtil.createObjectNode();
        requestBody.put("phone_number", phoneNo);

        HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, reqHeaders);

        try {
            ResponseEntity<ObjectNode> responseEntity = restTemplate.exchange(appConfig.getTelesignConfig().getVerifyInitiate(), HttpMethod.POST, requestEntity, ObjectNode.class);
            log.info("initiateVerify_return:" + responseEntity.getBody().toPrettyString());
        } catch (Exception ex) {
            log.error("initiateVerify_ex", ex);
        }
    }


    private String createBasicAuthString() {
        String auth_string = "";
        auth_string = appConfig.getTelesignConfig().getCustomerId() + ":" + appConfig.getTelesignConfig().getApiKey();
        auth_string = Base64.getEncoder().encodeToString(auth_string.getBytes());
        auth_string = "Basic " + auth_string;

        return auth_string;
    }

}

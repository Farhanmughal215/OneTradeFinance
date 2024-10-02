package com.xstocks.uc.service.remote;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.exception.BizException;
import static com.xstocks.uc.pojo.constants.CommonConstant.LOCAL_CACHE_ALL_TICKER;
import com.xstocks.uc.pojo.dto.ticker.TickerAbbreviationDTO;
import com.xstocks.uc.pojo.dto.ticker.TickerDetailDTO;
import com.xstocks.uc.pojo.dto.ticker.TickerPageDTO;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.remote.TickerSearchParam;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.utils.JsonUtil;
import com.xstocks.uc.utils.LocalCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@Slf4j
@Service
public class TickerService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    void init() {
        LocalCacheUtil.<String, Map<String, TickerAbbreviationDTO>>initLoadingCache(LOCAL_CACHE_ALL_TICKER, 1, 12 * 60,
                key -> getAllTickers().stream()
                        .collect(Collectors.toMap(TickerAbbreviationDTO::getSymbol, Function.identity())));
        LocalCacheUtil.<String, Map<String, TickerAbbreviationDTO>>getLoadingCache(LOCAL_CACHE_ALL_TICKER)
                .get(LOCAL_CACHE_ALL_TICKER);
    }

    public TickerDetailDTO getTickerDetailById(Long id) {
        AppConfig.Product tickerProduct =
                appConfig.getProductConfig().stream().filter(x -> "ticker".equals(x.getProduct()))
                        .collect(Collectors.toList()).get(0);

        String url = tickerProduct.getHost() + tickerProduct.getCapi() + "getBusinessInfoById";

        ObjectNode reqBody = JsonUtil.createObjectNode();
        reqBody.put("id", id);

        HttpHeaders reqHeaders = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json");
        reqHeaders.setContentType(type);

        HttpEntity<ObjectNode> httpEntity = new HttpEntity<ObjectNode>(reqBody, reqHeaders);

        ParameterizedTypeReference<BaseResp<TickerDetailDTO>> typeRef =
                new ParameterizedTypeReference<BaseResp<TickerDetailDTO>>() {
                };

        BaseResp<TickerDetailDTO> result = null;
        try {
            result = restTemplate
                    .exchange(url, HttpMethod.POST, httpEntity, typeRef)
                    .getBody();
        } catch (Exception ex) {
            throw new BizException(ErrorCode.DEFAULT_ERROR, ex.getMessage());
        }
        return Objects.nonNull(result) ? result.getData() : null;
    }

    public List<TickerAbbreviationDTO> getAllTickers() {
        AppConfig.Product tickerProduct =
                appConfig.getProductConfig().stream().filter(x -> "ticker".equals(x.getProduct()))
                        .collect(Collectors.toList()).get(0);

        String url = tickerProduct.getHost() + tickerProduct.getCapi() + "allBusinessInfoList";

        HttpHeaders reqHeaders = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json");
        reqHeaders.setContentType(type);

        HttpEntity<ObjectNode> httpEntity = new HttpEntity<ObjectNode>(null, reqHeaders);

        ParameterizedTypeReference<BaseResp<List<TickerAbbreviationDTO>>> typeRef =
                new ParameterizedTypeReference<BaseResp<List<TickerAbbreviationDTO>>>() {
                };

        BaseResp<List<TickerAbbreviationDTO>> result = null;
        try {
            result = restTemplate
                    .exchange(url, HttpMethod.POST, httpEntity, typeRef)
                    .getBody();
        } catch (Exception ex) {
            log.error("getAllTickers_ex", ex.getMessage());
        }
        return Objects.nonNull(result) ? result.getData() : new ArrayList<>();
    }

    public List<TickerDetailDTO> getSuggestTickers() {
        AppConfig.Product tickerProduct =
                appConfig.getProductConfig().stream().filter(x -> "ticker".equals(x.getProduct()))
                        .collect(Collectors.toList()).get(0);

        String url = tickerProduct.getHost() + tickerProduct.getCapi() + "getSerialNumberList";

        HttpHeaders reqHeaders = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json");
        reqHeaders.setContentType(type);

        HttpEntity<ObjectNode> httpEntity = new HttpEntity<ObjectNode>(null, reqHeaders);
        ParameterizedTypeReference<BaseResp<List<TickerDetailDTO>>> typeRef =
                new ParameterizedTypeReference<BaseResp<List<TickerDetailDTO>>>() {
                };

        BaseResp<List<TickerDetailDTO>> result = null;
        try {
            result = restTemplate
                    .exchange(url, HttpMethod.POST, httpEntity, typeRef)
                    .getBody();
        } catch (Exception ex) {
            throw new BizException(ErrorCode.DEFAULT_ERROR, ex.getMessage());
        }
        return Objects.nonNull(result) ? result.getData() : null;
    }

    public List<TickerDetailDTO> searchTickers(TickerSearchParam tickerSearchParam) {
        AppConfig.Product tickerProduct =
                appConfig.getProductConfig().stream().filter(x -> "ticker".equals(x.getProduct()))
                        .collect(Collectors.toList()).get(0);

        String url = tickerProduct.getHost() + tickerProduct.getCapi() + "searchTickerInfo";

        ObjectNode reqBody = JsonUtil.createObjectNode();
        reqBody.put("keyword", tickerSearchParam.getKeyword());
        if (StringUtils.isNotBlank(tickerSearchParam.getType())) {
            reqBody.put("type", tickerSearchParam.getType());
        }

        HttpHeaders reqHeaders = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json");
        reqHeaders.setContentType(type);

        HttpEntity<ObjectNode> httpEntity = new HttpEntity<ObjectNode>(reqBody, reqHeaders);

        ParameterizedTypeReference<BaseResp<TickerPageDTO>> typeRef =
                new ParameterizedTypeReference<BaseResp<TickerPageDTO>>() {
                };

        BaseResp<TickerPageDTO> result = null;
        try {
            result = restTemplate
                    .exchange(url, HttpMethod.POST, httpEntity, typeRef)
                    .getBody();
        } catch (Exception ex) {
            throw new BizException(ErrorCode.DEFAULT_ERROR, ex.getMessage());
        }
        return Objects.nonNull(result) && Objects.nonNull(result.getData()) &&
                CollectionUtils.isNotEmpty(result.getData()
                        .getList()) ?
                result.getData().getList() :
                new ArrayList<>();
    }
}

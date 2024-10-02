package com.xstocks.uc.service.remote;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.pojo.constants.CommonConstant;
import static com.xstocks.uc.pojo.constants.CommonConstant.LOCAL_CACHE_ALL_TICKER;
import com.xstocks.uc.pojo.dto.polygo.AggregatesDTO;
import com.xstocks.uc.pojo.dto.polygo.AggregatesRes;
import com.xstocks.uc.pojo.dto.polygo.LatestAggDTO;
import com.xstocks.uc.pojo.dto.polygo.LatestQuoteDTO;
import com.xstocks.uc.pojo.dto.polygo.LatestTickerDTO;
import com.xstocks.uc.pojo.dto.polygo.LatestTradeDTO;
import com.xstocks.uc.pojo.dto.polygo.TickerDTO;
import com.xstocks.uc.pojo.dto.polygo.TickerLatestStateDTO;
import com.xstocks.uc.pojo.dto.polygo.TickerRefreshDTO;
import com.xstocks.uc.pojo.dto.polygo.ws.A_DTO;
import com.xstocks.uc.pojo.dto.polygo.ws.Q_DTO;
import com.xstocks.uc.pojo.dto.polygo.ws.T_DTO;
import com.xstocks.uc.pojo.dto.ticker.TickerAbbreviationDTO;
import com.xstocks.uc.pojo.dto.ticker.TickerDetailDTO;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.enums.PolygoWebSocketEvEnum;
import com.xstocks.uc.pojo.param.polygo.AggregatesQueryParam;
import com.xstocks.uc.pojo.vo.Slice;
import com.xstocks.uc.utils.JsonUtil;
import com.xstocks.uc.utils.LocalCacheUtil;
import com.xstocks.uc.utils.ValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class PolygoService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TickerService tickerService;

    @Autowired
    private SimpMessagingTemplate webSocketMessagingTemplate;

    public Slice<AggregatesDTO> getAggregates(AggregatesQueryParam queryParam) {
        StringBuilder url = new StringBuilder();

        //参数校验
        if (Objects.isNull(queryParam)) {
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "required params null");
        }
        if (StringUtils.isBlank(queryParam.getNextPageToken())) {
            if (Objects.isNull(queryParam.getTickerId())) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "tickerId required");
            }
            if (StringUtils.isBlank(queryParam.getTimeSpan())) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "timeSpan required");
            }
            if (StringUtils.isBlank(queryParam.getFrom())) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "from required");
            }
            if (StringUtils.isBlank(queryParam.getTo())) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "to required");
            }
            if (Objects.isNull(queryParam.getMultiplier())) {
                queryParam.setMultiplier(1);
            }
            TickerDetailDTO tickerDetailDTO = tickerService.getTickerDetailById(queryParam.getTickerId());
            if (Objects.isNull(tickerDetailDTO) || (!"Yes".equalsIgnoreCase(tickerDetailDTO.getActive()) &&
                    !"true".equalsIgnoreCase(tickerDetailDTO.getActive()))) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "no corresponding ticker");
            }
            if (Objects.isNull(queryParam.getPageSize())) {
                queryParam.setPageSize(appConfig.getPolygoConfig().getAggregatesPageSize());
            }
            if (queryParam.getPageSize() > CommonConstant.POLYGO_AGG_MAX_PAGESIZE) {
                queryParam.setPageSize(CommonConstant.POLYGO_AGG_MAX_PAGESIZE);
            }
            if ("asc".equalsIgnoreCase(queryParam.getSort()) || "desc".equalsIgnoreCase(queryParam.getSort())) {
                queryParam.setSort(queryParam.getSort());
            } else {
                queryParam.setSort("asc");
            }

            String urlTpl = appConfig.getPolygoConfig().getAggregatesUrl();
            url.append(urlTpl.replace("{stocksTicker}", tickerDetailDTO.getSymbol())
                    .replace("{multiplier}", queryParam.getMultiplier().toString())
                    .replace("{timespan}", queryParam.getTimeSpan())
                    .replace("{from}", queryParam.getFrom())
                    .replace("{to}", queryParam.getTo())
                    .replace("{pageSize}", queryParam.getPageSize().toString())
                    .replace("{sort}", queryParam.getSort()));
        } else {
            String nextUrl = new String(Base64.getUrlDecoder().decode(queryParam.getNextPageToken()));
            if (!ValidatorUtil.url(nextUrl)) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "invalid nextToken");
            }
            log.info("nextToken decode url:{}", nextUrl);
            url.append(nextUrl)
                    .append("&apiKey=")
                    .append(appConfig.getPolygoConfig().getApiKey());
        }

        //获取数据
        try {
            ResponseEntity<AggregatesRes> responseEntity =
                    restTemplate.getForEntity(url.toString(), AggregatesRes.class);
            if (Objects.nonNull(responseEntity.getBody()) &&
                    CollectionUtils.isNotEmpty(responseEntity.getBody().getResults())) {
                log.info("returned next_url:{}", StringUtils.isNotBlank(responseEntity.getBody().getNext_url()) ? responseEntity.getBody().getNext_url() : StringUtils.EMPTY);
                log.info("next_url encode token:{}", StringUtils.isNotBlank(responseEntity.getBody().getNext_url()) ?
                        Base64.getUrlEncoder()
                                .encodeToString(responseEntity.getBody().getNext_url().getBytes()) :
                        StringUtils.EMPTY);
                return new Slice<AggregatesDTO>(queryParam.getPageSize(),
                        responseEntity.getBody().getResults(),
                        StringUtils.isNotBlank(responseEntity.getBody().getNext_url()) ?
                                Base64.getUrlEncoder()
                                        .encodeToString(responseEntity.getBody().getNext_url().getBytes()) :
                                StringUtils.EMPTY);
            }
        } catch (Throwable throwable) {
            log.error("getAggregatesErr:", throwable);
        }
        return new Slice<>(queryParam.getPageSize(), new ArrayList<AggregatesDTO>(),
                StringUtils.EMPTY);
    }

    public Map<Long, TickerLatestStateDTO> getLatestStateTickers(Set<Long> tickerIdSet) {
        Map<String, TickerAbbreviationDTO> tickerAbbreviationDTOMap = LocalCacheUtil.<String, Map<String, TickerAbbreviationDTO>>getLoadingCache(LOCAL_CACHE_ALL_TICKER).get(LOCAL_CACHE_ALL_TICKER);
        tickerIdSet = tickerIdSet.stream().filter(x -> tickerAbbreviationDTOMap.values().stream().anyMatch(y -> Objects.equals(y.getId(), x))).collect(Collectors.toSet());
        return tickerIdSet.stream()
                .map(tickerId -> {
                    if (redisTemplate.hasKey("latest:" + tickerId)) {
                        Map<Object, Object> map = redisTemplate.opsForHash().entries("latest:" + tickerId);
                        try {
                            return JsonUtil.parseObject(JsonUtil.toJSONString(map), TickerLatestStateDTO.class);
                        } catch (Exception ex) {
                            log.error("getTickersLatestQuote_ex:", ex);
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(TickerLatestStateDTO::getTicker, Function.identity()));
    }

    public TickerLatestStateDTO getLatestStateTicker(Long tickerId, String tickerSymbol) {
        if (redisTemplate.hasKey("latest:" + tickerId)) {
            Map<Object, Object> map = redisTemplate.opsForHash().entries("latest:" + tickerId);
            try {
                return JsonUtil.parseObject(JsonUtil.toJSONString(map), TickerLatestStateDTO.class);
            } catch (Exception ex) {
                log.error("getLatestStateTicker_ex:", ex);
            }
        } else {
            TickerRefreshDTO tickerRefreshDTO = getTickerRefreshDTO(tickerId, tickerSymbol);

            redisTemplate.opsForHash().putAll("prev:" + tickerId,
                    JsonUtil.convertPojo2Map(tickerRefreshDTO.getPreDay()));

            redisTemplate.opsForHash().putAll("latest:" + tickerId,
                    JsonUtil.convertPojo2Map(tickerRefreshDTO.getTickerLatestStateDTO()));

            return tickerRefreshDTO.getTickerLatestStateDTO();
        }
        return null;
    }

    public TickerRefreshDTO getTickerRefreshDTO(Long tickerId, String tickerSymbol) {
        TickerRefreshDTO tickerRefreshDTO = new TickerRefreshDTO();
        TickerLatestStateDTO res = new TickerLatestStateDTO();
        res.setTicker(tickerId);

        LatestTickerDTO latestTickerDTO = getLatestTicker(tickerSymbol);
        if (Objects.nonNull(latestTickerDTO)) {
            if (Objects.nonNull(latestTickerDTO.getMin())) {
                res.setH(latestTickerDTO.getMin().getH());
                res.setO(latestTickerDTO.getMin().getO());
                res.setL(latestTickerDTO.getMin().getL());
                res.setC(latestTickerDTO.getMin().getC());
            } else if (Objects.nonNull(latestTickerDTO.getPrevDay())) {
                res.setH(latestTickerDTO.getPrevDay().getH());
                res.setO(latestTickerDTO.getPrevDay().getO());
                res.setL(latestTickerDTO.getPrevDay().getL());
                res.setC(latestTickerDTO.getPrevDay().getC());
            }
            if (Objects.nonNull(latestTickerDTO.getLastQuote())) {
                res.setAp(latestTickerDTO.getLastQuote().getAp());
                res.setBp(latestTickerDTO.getLastQuote().getBp());
            }
            if (Objects.nonNull(latestTickerDTO.getLastTrade())) {
                res.setTp(latestTickerDTO.getLastTrade().getTp());
            }
            res.setTc(latestTickerDTO.getTodaysChangePerc());
            res.setTcv(latestTickerDTO.getTodaysChange());
        }
        res.setTimestamp(System.currentTimeMillis());

        LatestAggDTO prevDayAgg = new LatestAggDTO();
        if (Objects.nonNull(latestTickerDTO) && Objects.nonNull(latestTickerDTO.getPrevDay())) {
            prevDayAgg = latestTickerDTO.getPrevDay();
        }
        tickerRefreshDTO.setPreDay(prevDayAgg);
        tickerRefreshDTO.setTickerLatestStateDTO(res);
        return tickerRefreshDTO;
    }

    public LatestTickerDTO getLatestTicker(String tickerSymbol) {
        String url = appConfig.getPolygoConfig().getLatestTickerUrl().replace("{stocksTicker}", tickerSymbol);

        try {
            ResponseEntity<TickerDTO> responseEntity = restTemplate.getForEntity(url, TickerDTO.class);
            if (Objects.nonNull(responseEntity.getBody()) && responseEntity.getStatusCode() == HttpStatus.OK && "OK".equals(responseEntity.getBody().getStatus())) {
                return responseEntity.getBody().getTicker();
            }
        } catch (Throwable throwable) {
            log.warn("getLatestTicker_ex:", throwable);
        }
        return null;
    }

    @Async("polygoWsExecutor")
    public void process(ArrayNode data) {
        Map<String, TickerAbbreviationDTO> tickerAbbreviationDTOMap = LocalCacheUtil.<String, Map<String, TickerAbbreviationDTO>>getLoadingCache(LOCAL_CACHE_ALL_TICKER).get(LOCAL_CACHE_ALL_TICKER);
        if (Objects.nonNull(tickerAbbreviationDTOMap) && !tickerAbbreviationDTOMap.isEmpty()) {
            StreamSupport
                    .stream(data.spliterator(), true)
                    .forEach(jsonNode -> {
                        if (jsonNode.has("ev")) {
                            PolygoWebSocketEvEnum ev =
                                    PolygoWebSocketEvEnum.valueOf(jsonNode.get("ev").asText());
                            switch (ev) {
                                case A:
                                    A_DTO aDto = JsonUtil.jsonNode2Object(jsonNode, A_DTO.class);
                                    if (StringUtils.isNotBlank(aDto.getSym()) &&
                                            tickerAbbreviationDTOMap.containsKey(aDto.getSym())) {
                                        TickerAbbreviationDTO ticker = tickerAbbreviationDTOMap.get(aDto.getSym());

                                        TickerLatestStateDTO tickerLatestStateDTO;
                                        if (Boolean.TRUE.equals(redisTemplate.hasKey("latest:" + ticker.getId()))) {
                                            Map<Object, Object> latestMap =
                                                    redisTemplate.opsForHash().entries("latest:" + ticker.getId());
                                            tickerLatestStateDTO =
                                                    JsonUtil.parseObject(JsonUtil.toJSONString(latestMap),
                                                            TickerLatestStateDTO.class);
                                            tickerLatestStateDTO.setH(aDto.getH());
                                            tickerLatestStateDTO.setO(aDto.getO());
                                            tickerLatestStateDTO.setL(aDto.getL());
                                            tickerLatestStateDTO.setC(aDto.getC());
                                            tickerLatestStateDTO.setTimestamp(System.currentTimeMillis());
                                        } else {
                                            TickerRefreshDTO tickerRefreshDTO =
                                                    getTickerRefreshDTO(ticker.getId(),
                                                            ticker.getSymbol());
                                            tickerLatestStateDTO = tickerRefreshDTO.getTickerLatestStateDTO();
                                            redisTemplate.opsForHash().putAll("prev:" + ticker.getId(),
                                                    JsonUtil.convertPojo2Map(tickerRefreshDTO.getPreDay()));
                                        }
                                        redisTemplate.opsForHash().putAll("latest:" + ticker.getId(),
                                                JsonUtil.convertPojo2Map(tickerLatestStateDTO));
                                        webSocketMessagingTemplate.convertAndSend(
                                                appConfig.getStompConfig().getRelay().getBrokerDestinationPrefix()[0] + "/latest:" + ticker.getId(),
                                                tickerLatestStateDTO);
//                                        log.info("send_agg_message:{}", JsonUtil.toJSONString(tickerLatestStateDTO));
                                    }
                                    break;
                                case Q:
                                    Q_DTO qDto = JsonUtil.jsonNode2Object(jsonNode, Q_DTO.class);
                                    if (StringUtils.isNotBlank(qDto.getSym()) &&
                                            tickerAbbreviationDTOMap.containsKey(qDto.getSym())) {
                                        TickerAbbreviationDTO ticker = tickerAbbreviationDTOMap.get(qDto.getSym());

                                        TickerLatestStateDTO tickerLatestStateDTO;
                                        if (Boolean.TRUE.equals(redisTemplate.hasKey("latest:" + ticker.getId()))) {
                                            Map<Object, Object> latestMap =
                                                    redisTemplate.opsForHash().entries("latest:" + ticker.getId());
                                            tickerLatestStateDTO =
                                                    JsonUtil.parseObject(JsonUtil.toJSONString(latestMap),
                                                            TickerLatestStateDTO.class);

                                            tickerLatestStateDTO.setAp(qDto.getAp());
                                            tickerLatestStateDTO.setBp(qDto.getBp());
                                            tickerLatestStateDTO.setTimestamp(System.currentTimeMillis());
                                        } else {
                                            TickerRefreshDTO tickerRefreshDTO =
                                                    getTickerRefreshDTO(ticker.getId(),
                                                            ticker.getSymbol());
                                            tickerLatestStateDTO = tickerRefreshDTO.getTickerLatestStateDTO();
                                            redisTemplate.opsForHash().putAll("prev:" + ticker.getId(),
                                                    JsonUtil.convertPojo2Map(tickerRefreshDTO.getPreDay()));
                                        }
                                        redisTemplate.opsForHash().putAll("latest:" + ticker.getId(),
                                                JsonUtil.convertPojo2Map(tickerLatestStateDTO));
                                        webSocketMessagingTemplate.convertAndSend(
                                                appConfig.getStompConfig().getRelay().getBrokerDestinationPrefix()[0] + "/latest:" + ticker.getId(),
                                                tickerLatestStateDTO);
//                                        log.info("send_quote_message:{}", JsonUtil.toJSONString(tickerLatestStateDTO));
                                    }
                                    break;
                                case T:
                                    T_DTO tDto = JsonUtil.jsonNode2Object(jsonNode, T_DTO.class);
                                    if (StringUtils.isNotBlank(tDto.getSym()) &&
                                            tickerAbbreviationDTOMap.containsKey(tDto.getSym())) {
                                        TickerAbbreviationDTO ticker = tickerAbbreviationDTOMap.get(tDto.getSym());

                                        TickerLatestStateDTO tickerLatestStateDTO;
                                        if (Boolean.TRUE.equals(redisTemplate.hasKey("latest:" + ticker.getId()))) {
                                            Map<Object, Object> latestMap =
                                                    redisTemplate.opsForHash().entries("latest:" + ticker.getId());
                                            tickerLatestStateDTO =
                                                    JsonUtil.parseObject(JsonUtil.toJSONString(latestMap),
                                                            TickerLatestStateDTO.class);
                                            tickerLatestStateDTO.setTp(tDto.getP());
                                            if (Boolean.TRUE.equals(redisTemplate.hasKey("prev:" + ticker.getId()))) {
                                                Map<Object, Object> prevMap =
                                                        redisTemplate.opsForHash().entries("prev:" + ticker.getId());
                                                LatestAggDTO prevDayAgg =
                                                        JsonUtil.parseObject(JsonUtil.toJSONString(prevMap),
                                                                LatestAggDTO.class);
                                                if (Objects.isNull(prevDayAgg) || Objects.isNull(tickerLatestStateDTO.getTp()) || Objects.isNull(prevDayAgg.getC()) || prevDayAgg.getC().compareTo(0.00d) == 0) {
                                                    tickerLatestStateDTO.setTc(0.00d);
                                                    tickerLatestStateDTO.setTcv(0.00d);
                                                } else {
                                                    BigDecimal tradePriceChangePercentage =
                                                            BigDecimal.valueOf(
                                                                    (tickerLatestStateDTO.getTp() - prevDayAgg.getC()) /
                                                                            prevDayAgg.getC()).setScale(2, RoundingMode.DOWN);
                                                    tickerLatestStateDTO.setTc(tradePriceChangePercentage.doubleValue());

                                                    BigDecimal tradePriceChange = BigDecimal.valueOf(tickerLatestStateDTO.getTp() - prevDayAgg.getC());

                                                    tickerLatestStateDTO.setTcv(tradePriceChange.doubleValue());
                                                }
                                            }
                                            tickerLatestStateDTO.setTimestamp(System.currentTimeMillis());
                                        } else {
                                            TickerRefreshDTO tickerRefreshDTO =
                                                    getTickerRefreshDTO(ticker.getId(),
                                                            ticker.getSymbol());
                                            tickerLatestStateDTO = tickerRefreshDTO.getTickerLatestStateDTO();
                                            redisTemplate.opsForHash().putAll("prev:" + ticker.getId(),
                                                    JsonUtil.convertPojo2Map(tickerRefreshDTO.getPreDay()));
                                        }
                                        redisTemplate.opsForHash().putAll("latest:" + ticker.getId(),
                                                JsonUtil.convertPojo2Map(tickerLatestStateDTO));
                                        webSocketMessagingTemplate.convertAndSend(
                                                appConfig.getStompConfig().getRelay().getBrokerDestinationPrefix()[0] + "/latest:" + ticker.getId(),
                                                tickerLatestStateDTO);
//                                        log.info("send_trade_message:{}", JsonUtil.toJSONString(tickerLatestStateDTO));
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        }
    }

    public LatestAggDTO getTickerLatestDayAgg(String tickerSymbol) {
        String url = appConfig.getPolygoConfig().getLatestDayUrl().replace("{stocksTicker}", tickerSymbol);

        try {
            ResponseEntity<LatestAggDTO> responseEntity = restTemplate.getForEntity(url, LatestAggDTO.class);
            if (Objects.nonNull(responseEntity.getBody())) {
                return responseEntity.getBody();
            }
        } catch (Throwable throwable) {
            log.error("getTickerLatestDayAgg_ex:", throwable);
        }
        return null;
    }

    public LatestTradeDTO getTickerLatestTrade(String tickerSymbol) {
        String url = appConfig.getPolygoConfig().getLatestTradeUrl().replace("{stocksTicker}", tickerSymbol);

        try {
            ResponseEntity<LatestTradeDTO> responseEntity = restTemplate.getForEntity(url, LatestTradeDTO.class);
            if (Objects.nonNull(responseEntity.getBody())) {
                return responseEntity.getBody();
            }
        } catch (Throwable throwable) {
            log.error("getTickerLatestTrade_ex:", throwable);
        }
        return null;
    }

    public LatestQuoteDTO getTickerLatestQuote(String tickerSymbol) {
        String url = appConfig.getPolygoConfig().getLatestQuoteUrl().replace("{stocksTicker}", tickerSymbol);

        try {
            ResponseEntity<LatestQuoteDTO> responseEntity = restTemplate.getForEntity(url, LatestQuoteDTO.class);
            if (Objects.nonNull(responseEntity.getBody())) {
                return responseEntity.getBody();
            }
        } catch (Throwable throwable) {
            log.error("getTickerLatestQuote_ex:", throwable);
        }
        return null;
    }

}

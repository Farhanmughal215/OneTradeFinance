package com.xstocks.uc.service.remote;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.pojo.dto.polygo.ws.StatusDTO;
import com.xstocks.uc.pojo.dto.ticker.TickerAbbreviationDTO;
import com.xstocks.uc.pojo.enums.PolygoWebSocketStatusEnum;
import com.xstocks.uc.pojo.param.polygo.ws.SubscribeParam;
import com.xstocks.uc.utils.JsonUtil;
import com.xstocks.uc.utils.LocalCacheUtil;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.xstocks.uc.pojo.constants.CommonConstant.LOCAL_CACHE_ALL_TICKER;
import static io.github.resilience4j.core.IntervalFunction.ofExponentialRandomBackoff;

/**
 * @ClassName PolygoTask
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/15 18:04
 **/
@Slf4j
@Service
public class PolygoWsService {

    static final Long INITIAL_INTERVAL = 10000L;
    static final Double MULTIPLIER = 2.0D;
    static final Double RANDOMIZATION_FACTOR = 0.6D;
    static final Integer MAX_RETRIES = 100;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private PolygoService polygoService;

    private StringBuilder sb = new StringBuilder();

    private TextWebSocketHandler textWebSocketHandler = new TextWebSocketHandler() {
        @Override
        public void handleTextMessage(WebSocketSession session, TextMessage message) {
            sb.append(message.getPayload());
            if (!message.isLast()) {
                return;
            }
            try {
                ArrayNode arrayNode = JsonUtil.parseObject(sb.toString(), ArrayNode.class);
                if (Objects.nonNull(arrayNode) && !arrayNode.isEmpty()) {
                    if (!isSubscribing.get()) {
                        JsonNode jsonNode = arrayNode.get(0);
                        StatusDTO statusDTO = JsonUtil.jsonNode2Object(jsonNode, StatusDTO.class);
                        if (Objects.nonNull(statusDTO)) {
                            if (PolygoWebSocketStatusEnum.auth_success == statusDTO.getStatus()) {
                                SubscribeParam subscribeParam = new SubscribeParam();
                                subscribeParam.setAction("subscribe")
                                        .setParams("A.*,T.*,Q.*");
                                try {
                                    session.sendMessage(
                                            new TextMessage(JsonUtil.toJSONString(subscribeParam)));
                                    isSubscribing.compareAndSet(false, true);
//                                    log.info("polygo subsrcibe started");
                                } catch (Exception ex) {
                                    log.error("PolygoSubscribe_ex:", ex);
                                }
                            }
                        }
                    } else {
                        polygoService.process(arrayNode);
                    }
                }
            } catch (Exception ex) {
                log.error("PolygoHandleTextMessage_ex:", ex);
            } finally {
                sb = new StringBuilder();
            }
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            SubscribeParam authParam = new SubscribeParam();
            authParam.setAction("auth")
                    .setParams(appConfig.getPolygoConfig().getApiKey());
            try {
                session.sendMessage(new TextMessage(JsonUtil.toJSONString(authParam)));
            } catch (Exception ex) {
                log.error("PolygoSendAuth_ex:", ex);
            }
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            session.close();
            isSubscribing.compareAndSet(true, false);
            log.error("PolygoTransport_ex:", exception);
            subscribe();
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            session.close();
            isSubscribing.compareAndSet(true, false);
//            log.error("PolygoClose_ex:" + status.toString());
            subscribe();
        }

        @Override
        public boolean supportsPartialMessages() {
            return true;
        }

    };

    private final AtomicBoolean isSubscribing = new AtomicBoolean(false);

    private Retry retry;

    @PostConstruct
    void init() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(MAX_RETRIES)
                .intervalFunction(ofExponentialRandomBackoff(INITIAL_INTERVAL, MULTIPLIER,
                        RANDOMIZATION_FACTOR))
                .retryOnResult(res -> res.equals(Boolean.FALSE))
                .build();
        retry = Retry.of("PolygoConnect", retryConfig);
    }

    public void subscribe() {
        Map<String, TickerAbbreviationDTO> tickerAbbreviationDTOMap =
                LocalCacheUtil.<String, Map<String, TickerAbbreviationDTO>>getLoadingCache(LOCAL_CACHE_ALL_TICKER)
                        .get(LOCAL_CACHE_ALL_TICKER);
        if (Objects.isNull(tickerAbbreviationDTOMap) || tickerAbbreviationDTOMap.isEmpty()) {
            return;
        }
        try {
            retry.executeCallable(this::connect);
        } catch (Exception ex) {
            log.error("PolygoConnect_ex:", ex);
        }
    }

    private Boolean connect() {
        try {
            WebSocketClient webSocketClient = new StandardWebSocketClient();
            webSocketClient.doHandshake(textWebSocketHandler,
                            new WebSocketHttpHeaders(),
                            URI.create(appConfig.getPolygoConfig().getWsUrl()))
                    .get();
            return Boolean.TRUE;
        } catch (Exception ex) {
            log.error("PolygoConnect_ex:", ex);
            return Boolean.FALSE;
        }
    }

    public Boolean getIsSubscribing() {
        return isSubscribing.get();
    }
}


package com.xstocks.uc.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.xstocks.uc.config.security.JwtManager;
import com.xstocks.uc.exception.BizException;
import static com.xstocks.uc.pojo.constants.CommonConstant.LOCAL_CACHE_ALL_TICKER;
import com.xstocks.uc.pojo.dto.ticker.TickerAbbreviationDTO;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.utils.JsonUtil;
import com.xstocks.uc.utils.LocalCacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private JwtManager jwtManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableStompBrokerRelay(appConfig.getStompConfig().getRelay().getBrokerDestinationPrefix())
                .setUserDestinationBroadcast("/topic/unresolved.user.dest")
                .setUserRegistryBroadcast("/topic/registry.broadcast")
                .setRelayHost(appConfig.getStompConfig().getRelay().getHost())
                .setRelayPort(appConfig.getStompConfig().getRelay().getPort())
                .setClientLogin(appConfig.getStompConfig().getRelay().getLogin())
                .setClientPasscode(appConfig.getStompConfig().getRelay().getPasscode())
                .setSystemLogin(appConfig.getStompConfig().getRelay().getLogin())
                .setSystemPasscode(appConfig.getStompConfig().getRelay().getPasscode());

        registry.setApplicationDestinationPrefixes(appConfig.getStompConfig().getApplicationDestinationPrefix());

        registry.setPreservePublishOrder(true);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(appConfig.getStompConfig().getEndpoint()).setAllowedOriginPatterns("*").withSockJS();
        registry.setErrorHandler(new StompSubProtocolErrorHandler() {
            @Override
            @Nullable
            public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
                StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
                accessor.setMessage(ex.getMessage());
                if (ex instanceof MessageDeliveryException) {
                    MessageDeliveryException exToUse = (MessageDeliveryException) ex;
                    if (exToUse.getCause() instanceof BizException) {
                        BizException bizException = (BizException) exToUse.getCause();
                        accessor.setMessage(JsonUtil.toJSONString(BaseResp.error(bizException.getErrCode(), bizException.getMessage())));
                    }
                }
                accessor.setLeaveMutable(true);

                StompHeaderAccessor clientHeaderAccessor = null;
                if (clientMessage != null) {
                    clientHeaderAccessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);
                    if (clientHeaderAccessor != null) {
                        String receiptId = clientHeaderAccessor.getReceipt();
                        if (receiptId != null) {
                            accessor.setReceiptId(receiptId);
                        }
                    }
                }
                byte[] payload = new byte[0];
                return handleInternal(accessor, payload, ex, clientHeaderAccessor);
            }
        });
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String tokenHeader = accessor.getFirstNativeHeader("Authorization");
                    if (StringUtils.isNotBlank(tokenHeader)) {
                        DecodedJWT decodedJWT = jwtManager.parse(tokenHeader);
                        if (Objects.nonNull(decodedJWT)) {
                            boolean isExpired = decodedJWT.getExpiresAt().before(new Date());
                            if (!isExpired) {
                                String username = decodedJWT.getSubject();
                                UserDetails user = userDetailsService.loadUserByUsername(username);
                                Authentication authentication =
                                        new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                                                user.getAuthorities());
                                accessor.setUser(authentication);
                                return message;
                            }
                        }
                    }
                    throw new BizException(ErrorCode.UNAUTHENTICATED, "not login or login expired");
                } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    if (StringUtils.isNotBlank(destination) && destination.startsWith(
                            appConfig.getStompConfig().getRelay().getBrokerDestinationPrefix()[0] + "/")) {
                        Map<String, TickerAbbreviationDTO> tickerAbbreviationDTOMap = LocalCacheUtil.<String, Map<String, TickerAbbreviationDTO>>getLoadingCache(LOCAL_CACHE_ALL_TICKER).get(LOCAL_CACHE_ALL_TICKER);
                        if (Objects.nonNull(tickerAbbreviationDTOMap) && !tickerAbbreviationDTOMap.isEmpty()) {
                            String subscribeTicker =
                                    destination.replace(
                                            appConfig.getStompConfig().getRelay().getBrokerDestinationPrefix()[0] + "/",
                                            StringUtils.EMPTY).replace("latest:", StringUtils.EMPTY);
                            if (tickerAbbreviationDTOMap.values().stream().anyMatch(x -> x.getId().toString().equalsIgnoreCase(subscribeTicker))) {
                                return message;
                            }
                        }
                    }
                    throw new BizException(ErrorCode.ILLEGAL_REQUEST, "illegal destination");
                } else if (StompCommand.SEND.equals(accessor.getCommand())) {
                    throw new BizException(ErrorCode.ILLEGAL_REQUEST, "SEND not supported yet");
                }
                return message;
            }
        });
    }
}

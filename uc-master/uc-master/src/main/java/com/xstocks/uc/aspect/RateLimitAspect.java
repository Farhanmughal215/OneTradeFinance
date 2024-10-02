package com.xstocks.uc.aspect;

import com.xstocks.uc.config.limiter.LimiterManager;
import com.xstocks.uc.config.limiter.RedisRateLimiter;
import com.xstocks.uc.config.security.UserDetail;
import com.xstocks.uc.pojo.po.UserPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class RateLimitAspect {

    @Autowired
    private LimiterManager limiterManager;

    @Around("execution(public * com.xstocks.uc.controller.*.*(..)) && @annotation(com.xstocks.uc.aspect.RateLimit)")
    public Object rateLimitProcess(ProceedingJoinPoint pjp) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes())
                .getRequest();

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        RateLimit rateLimitAnnotation = methodSignature.getMethod().getAnnotation(RateLimit.class);
        String requestURI = request.getRequestURI();

        if (Objects.nonNull(rateLimitAnnotation)) {
            long rateCorrespondingMs = rateLimitAnnotation.timeUnit().toMillis(1);
            long rate = rateLimitAnnotation.rate();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (Objects.nonNull(authentication)) {
                UserDetail userDetail = (UserDetail) authentication.getPrincipal();
                if(Objects.nonNull(userDetail)) {
                    UserPO userPO = userDetail.getUserPOEntity();
                    if (Objects.nonNull(userPO)) {
                        String rateLimitKey = requestURI + ":{" + userPO.getId().toString() + "}";
                        RedisRateLimiter redisRateLimiter =
                                limiterManager.getLimiter(rateLimitKey, rateCorrespondingMs, rate);
                        redisRateLimiter.acquire();
                    }
                }
            } else {
                String remoteIp = request.getHeader("x-forwarded-for");
                if (StringUtils.isNotBlank(remoteIp)) {
                    String rateLimitKey = requestURI + ":{" + remoteIp.trim() + "}";
                    RedisRateLimiter redisRateLimiter =
                            limiterManager.getLimiter(rateLimitKey, rateCorrespondingMs, rate);
                    redisRateLimiter.acquire();
                }
            }
        }
        return pjp.proceed();
    }
}

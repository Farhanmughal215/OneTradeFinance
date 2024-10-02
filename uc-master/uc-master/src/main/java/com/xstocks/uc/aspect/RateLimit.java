package com.xstocks.uc.aspect;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

import com.xstocks.uc.pojo.constants.CommonConstant;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long rate() default CommonConstant.DEFAULT_RATE;

}

package com.xstocks.statistics.common;

public class CommonConstant {

    /**
     * 默认速率 qps 10/s
     */
    public static final long DEFAULT_RATE = 20;
    /**
     * 默认时长
     */
    public static final long DEFAULT_RATE_CORRESPONDING_MS = 1000;

    public static final String RATE_LIMIT_KEY = "xstock:rate:limit";

    public static final String RATE_LIMIT_FIELD = "rate";

    public static final String RATE_MS_FIELD = "rate_ms";


    //已经消费的记录-保存5分钟
    public static final String CONSUMED_KEY = "statistics:consumed:";
}

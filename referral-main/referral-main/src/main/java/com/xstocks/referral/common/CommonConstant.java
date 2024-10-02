package com.xstocks.referral.common;

import java.util.regex.Pattern;

public class CommonConstant {

    public static final String SYS_CONF = "XSTOCK_SYS_CONF";
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

    public static final String NEW_TOKEN = "new token returned";

    public static final String PULL_NEWS_LOCK = "PULL_NEWS_LOCK";

    public static final String PULL_ECONOMY_LOCK = "PULL_ECONOMY_LOCK";

    public static final String PULL_SEVEN24HOURS_LOCK = "PULL_SEVEN24HOURS_LOCK";

    public static final String TICKER_REFRESH_DAILY_LOCK = "TICKER_REFRESH_DAILY_LOCK";

    public static final String CURRENT_LOGIN_USER = "CURRENT_LOGIN_USER";

    public static final String DEFAULT_LANG = "en";

    public static final int POLYGO_AGG_MAX_PAGESIZE = 1000;

    public static final String LOCAL_CACHE_ALL_TICKER = "ALL_TICKER";

    public static final String CREATE_USER_LIMIT_KEY = "referral:create:";
    public static final String WITHDRAW_LIMIT_KEY = "referral:withdraw:";

    public static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s" +
            ".-]?\\d{3}[\\s.-]?\\d{4,7}$");

    public static final Pattern EMAIL_PATTERN = Pattern.compile("[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+");

}

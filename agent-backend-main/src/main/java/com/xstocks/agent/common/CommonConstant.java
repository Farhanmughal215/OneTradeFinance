package com.xstocks.agent.common;

import java.util.regex.Pattern;

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

    public static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s" +
            ".-]?\\d{3}[\\s.-]?\\d{4,7}$");

    public static final Pattern EMAIL_PATTERN = Pattern.compile("[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+");

}

package com.xstocks.statistics.common;

import java.util.*;

/**
 * @author Kevin
 * @date 2024/1/6 12:47
 * @apiNote 常量
 */
public class Constants {


    public static Map<String, List<String>> ORDER_SYMBOL_TYPE_MAP = new HashMap<>();

    static {
        ORDER_SYMBOL_TYPE_MAP.put("trade_sum", Arrays.asList("long", "close", "break"));
        ORDER_SYMBOL_TYPE_MAP.put("long", Collections.singletonList("long"));
        ORDER_SYMBOL_TYPE_MAP.put("close", Collections.singletonList("close"));
        ORDER_SYMBOL_TYPE_MAP.put("break", Collections.singletonList("break"));
        ORDER_SYMBOL_TYPE_MAP.put("long_market_cap", Collections.singletonList("long_market_cap"));
        ORDER_SYMBOL_TYPE_MAP.put("close_market_cap", Collections.singletonList("close_market_cap"));
        ORDER_SYMBOL_TYPE_MAP.put("break_market_cap", Collections.singletonList("break_market_cap"));
    }


    public static Map<String, List<String>> AMOUNT_ORDER_SYMBOL_TYPE_MAP = new HashMap<>();

    static {
        AMOUNT_ORDER_SYMBOL_TYPE_MAP.put("profit_loss_sum", Arrays.asList("close","break"));
    }


    public static Map<String, List<String>> TODAY_ORDER_SYMBOL_TYPE_MAP = new HashMap<>();

    static {
        TODAY_ORDER_SYMBOL_TYPE_MAP.put("bid_count", Arrays.asList("long", "close", "break"));
        TODAY_ORDER_SYMBOL_TYPE_MAP.put("bid_long_market_cap", Collections.singletonList("long_market_cap"));
        TODAY_ORDER_SYMBOL_TYPE_MAP.put("bid_close_market_cap", Collections.singletonList("close_market_cap"));

        TODAY_ORDER_SYMBOL_TYPE_MAP.put("ask_trade_count", Arrays.asList("long", "close", "break"));
        TODAY_ORDER_SYMBOL_TYPE_MAP.put("ask_long_market_cap", Collections.singletonList("long_market_cap"));
        TODAY_ORDER_SYMBOL_TYPE_MAP.put("ask_close_market_cap", Collections.singletonList("close_market_cap"));
    }

    public static Map<String, List<String>> REMAIN_ORDER_SYMBOL_TYPE_MAP = new HashMap<>();

    static {
        REMAIN_ORDER_SYMBOL_TYPE_MAP.put("remain_trade", Arrays.asList("long", "close", "break"));
        REMAIN_ORDER_SYMBOL_TYPE_MAP.put("remain_long", Collections.singletonList("long"));
        REMAIN_ORDER_SYMBOL_TYPE_MAP.put("remain_close", Collections.singletonList("close"));
        REMAIN_ORDER_SYMBOL_TYPE_MAP.put("remain_break", Collections.singletonList("break"));
        REMAIN_ORDER_SYMBOL_TYPE_MAP.put("remain_trade_address", Arrays.asList("long", "close", "break"));
        REMAIN_ORDER_SYMBOL_TYPE_MAP.put("remain_long_address", Collections.singletonList("close_market_cap"));
        REMAIN_ORDER_SYMBOL_TYPE_MAP.put("remain_close_address", Collections.singletonList("break_market_cap"));
        REMAIN_ORDER_SYMBOL_TYPE_MAP.put("remain_break_address", Collections.singletonList("break_market_cap"));

    }

}

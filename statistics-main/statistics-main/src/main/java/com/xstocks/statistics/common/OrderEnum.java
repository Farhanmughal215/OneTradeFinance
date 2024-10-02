package com.xstocks.statistics.common;

import lombok.Getter;

@Getter
public enum OrderEnum {

    //类型,long:建仓,close:平仓,break:爆仓,long_market_cap:建仓市值,close_market_cap:平仓市值,
    // break_market_cap:爆仓市值,

    LONG("long", "建仓"),
    CLOSE("close", "平仓"),
    BREAK("break", "爆仓"),
    LONG_MARKET_CAP("long_market_cap", "建仓市值"),
    CLOSE_MARKET_CAP("close_market_cap", "平仓市值"),
    BREAK_MARKET_CAP("break_market_cap", "爆仓市值");

    private String type;

    OrderEnum(String type, String desc) {
        this.type = type;
    }

}

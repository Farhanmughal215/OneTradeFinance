package com.xstocks.statistics.pojo.enums;

public enum RankSubTypeEnum {
    trade_count("trade_count", "交易笔数"),
    realize("realize", "实现盈亏"),
    profit_rate("profit_rate", "收益率")
    ;

    private final String code;

    private final String msg;

    RankSubTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsg(String code) {
        RankSubTypeEnum[] enums = values();
        for (RankSubTypeEnum e : enums) {
            if (e.code().equals(code)) {
                return e.msg();
            }
        }
        return "";
    }


    public static RankSubTypeEnum getEnum(String code) {
        RankSubTypeEnum[] enums = values();
        for (RankSubTypeEnum e : enums) {
            if (e.code().equals(code)) {
                return e;
            }
        }
        return null;
    }

    public String code() {
        return this.code;
    }

    public String msg() {
        return this.msg;
    }


}

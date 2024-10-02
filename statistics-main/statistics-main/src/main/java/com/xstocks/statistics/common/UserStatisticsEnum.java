package com.xstocks.statistics.common;

import lombok.Getter;

@Getter
public enum UserStatisticsEnum {

//    类型,today_reg:每日新增注册地址数,total_count:注册地址总数,trade_sum:每日交易地址数,long:建仓笔数,close:平仓笔数,break:爆仓笔数,long_market_cap:建仓市值,close_market_cap:平仓市值,break_market_cap:爆仓市值,profit_loss_sum:所有地址总实现盈亏,max_profit:最大实现盈利,max_loss:最大实现亏损,remain_trade:留存地址交易笔数,remain_long:留存地址建仓笔数,remain_close:留存地址平仓笔数,remain_break:留存地址爆仓笔数,remain_trade_address:留存地址交易地址数,remain_long_address:留存地址建仓地址数,remain_close_address:留存地址平仓地址数,remain_break_address:留存地址爆仓地址数,trade_count:每日日内交易笔数,avg_opening_time:每日日内交易平均持仓时间,
    //  bid_long_market_cap:多单建仓市值,bid_close_market_cap:多单平仓市值,bid_count:多单笔数,ask_long_market_cap:空单建仓市值,ask_close_market_cap:空单平仓市值,ask_trade_count:空单笔数

    /*
    类型,today_reg:每日新增注册地址数,total_count:注册地址总数,trade_sum:每日交易地址数,long:建仓笔数,close:平仓笔数,break:爆仓笔数,
    long_market_cap:建仓市值,close_market_cap:平仓市值,break_market_cap:爆仓市值,profit_loss_sum:所有地址总实现盈亏,
    max_profit:最大实现盈利,max_loss:最大实现亏损,remain_trade:留存地址交易笔数,remain_long:留存地址建仓笔数,
    remain_close:留存地址平仓笔数,remain_break:留存地址爆仓笔数,remain_trade_address:留存地址交易地址数,
    remain_long_address:留存地址建仓地址数,remain_close_address:留存地址平仓地址数,remain_break_address:留存地址爆仓地址数,
    trade_count:每日日内交易笔数,avg_opening_time:每日日内交易平均持仓时间,
    bid_long_market_cap:多单建仓市值,bid_close_market_cap:多单平仓市值,bid_count:多单笔数,ask_long_market_cap:空单建仓市值,
    ask_close_market_cap:空单平仓市值,ask_trade_count:空单笔数
     */
    TODAY_REG("today_reg", "每日新增注册地址数"),
    TOTAL_REG("total_count", "注册地址总数"),
    TRADE_SUM("trade_sum", "交易地址数"),
    LONG("long", "建仓笔数"),
    CLOSE("close", "平仓笔数"),
    BREAK("break", "爆仓笔数"),
    LONG_MARKET_CAP("long_market_cap", "建仓市值"),
    CLOSE_MARKET_CAP("close_market_cap", "平仓市值"),
    BREAK_MARKET_CAP("break_market_cap", "爆仓市值"),
    PROFIT_LOSS_SUM("profit_loss_sum", "所有地址总实现盈亏"),
    MAX_PROFIT("max_profit", "最大实现盈利额度"),
    MAX_PROFIT_ADDRESS("max_profit_address", "最大实现盈利地址"),
    MAX_LOSS("max_loss", "最大实现亏损额度"),
    MAX_LOSS_ADDRESS("max_loss_address", "最大实现亏损地址"),
    MAX_PROFIT_RATE("max_profit_rate", "最大实现收益率"),
    MAX_PROFIT_RATE_ADDRESS("max_profit_rate_address", "最大实现收益率地址"),
    MAX_LOSS_RATE("max_loss_rate", "最大实现亏损率"),
    MAX_LOSS_RATE_ADDRESS("max_loss_rate_address", "最大实现亏损率地址"),
    REMAIN_TRADE("remain_trade", "留存地址交易笔数"),
    REMAIN_LONG("remain_long", "留存地址建仓笔数"),
    REMAIN_CLOSE("remain_close", "留存地址平仓笔数"),
    REMAIN_BREAK("remain_break", "留存地址爆仓笔数"),
    REMAIN_TRADE_ADDRESS("remain_trade_address", "留存地址交易地址数"),
    REMAIN_LONG_ADDRESS("remain_long_address", "留存地址建仓地址数"),
    REMAIN_CLOSE_ADDRESS("remain_close_address", "留存地址平仓地址数"),
    REMAIN_BREAK_ADDRESS("remain_break_address", "留存地址爆仓地址数"),
    TRADE_COUNT("trade_count", "日内交易笔数"),
    AVG_OPENING_TIME("avg_opening_time", "日内交易平均持仓时间"),
    BID_LONG_MARKET_CAP("bid_long_market_cap", "日内交易多单建仓市值"),
    BID_CLOSE_MARKET_CAP("bid_close_market_cap", "日内交易多单平仓市值"),
    BID_COUNT("bid_count", "日内交易多单笔数"),
    ASK_LONG_MARKET_CAP("ask_long_market_cap", "日内交易空单建仓市值"),
    ASK_CLOSE_MARKET_CAP("ask_close_market_cap", "日内交易空单平仓市值"),
    ASK_TRADE_COUNT("ask_trade_count", "日内交易空单笔数");

    private final String type;

    private final String desc;

    UserStatisticsEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    //根据type 获取 desc
    public static String getDesc(String type) {
        for (UserStatisticsEnum e : UserStatisticsEnum.values()) {
            if (e.getType().equalsIgnoreCase(type)) {
                return e.getDesc();
            }
        }
        return null;
    }

}

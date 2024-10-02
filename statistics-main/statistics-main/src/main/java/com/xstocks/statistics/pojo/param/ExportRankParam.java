package com.xstocks.statistics.pojo.param;

import lombok.Data;

@Data
public class ExportRankParam {

    /**
     * 查询榜单类型：
     * day - 日榜
     * overall - 总榜
     */
    private String rankType;

    /**
     * 查询榜单子类型：
     *  trade_count:交易笔数；
     *  realize:实现盈亏；
     *  profit_rate:收益率；
     */
    private String rankSubType;

    /**
     * 导出前多少名
     */
    private Integer top;

    /**
     * 日期，格式：yyyy-MM-dd
     */
    private String date;

}

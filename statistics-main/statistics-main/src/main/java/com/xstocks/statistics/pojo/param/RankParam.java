package com.xstocks.statistics.pojo.param;

import lombok.Data;

@Data
public class RankParam {

    /**
     * 当前登录用户uid
     */
    private Long uid;

    /**
     * 当前登录用户钱包地址
     */
    private String address;

    /**
     * 查询榜单状态：
     *  pending：待放榜
     *  release：已放榜
     */
    private String rankStatus;

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

}

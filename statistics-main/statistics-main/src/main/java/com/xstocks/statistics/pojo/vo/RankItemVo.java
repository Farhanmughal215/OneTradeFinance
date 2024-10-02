package com.xstocks.statistics.pojo.vo;

import lombok.Data;

@Data
public class RankItemVo {

    /**
     * 排名
     *   在待放榜中表示排名区间，如 100~199
     *   在已放榜中表示具体的排名如 99
     */
    private String rankNo;

    /**
     * 用户uid
     */
    private Long uid;

    /**
     * 用户钱包地址
     */
    private String address;

    /**
     * 该用户在榜单中成绩数据值
     * 可表示：交易笔数、实现盈亏、收益率
     */
    private String amount;
}

package com.xstocks.uc.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Kevin
 * @date 2024/3/25 14:34
 * @apiNote
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TraderStatisticsVo extends BTraderListVo{


    /**
     * 跟单用户数
     */
    private Integer followCount;

    /**
     * 交易笔数
     */
    private Integer tradeCount;

    /**
     * 盈亏额
     */
    private BigDecimal plAmount;

    /**
     * 跟单规模
     */
    private BigDecimal totalAmount;

    /**
     * 跟单用户实现盈亏
     */
    private BigDecimal followTotalAmount;

    /**
     * 跟单用户浮动盈亏
     */
    private BigDecimal followPl;

    /**
     * 总分润
     */
    private BigDecimal totalProfit;

}

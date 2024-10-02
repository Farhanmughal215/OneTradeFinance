package com.xstocks.uc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 交易员数据
 * </p>
 *
 * @author kevin
 * @since 2024-03-25
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_trader_data")
public class TraderData implements Serializable {


    private static final long serialVersionUID = 5185454825519476966L;

    /**
     * 交易员ID
     */
    @TableId(value = "trader_id", type = IdType.INPUT)
    private Integer traderId;

    private Integer userId;

    /**
     * 跟单人数
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

package com.xstocks.uc.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Kevin
 * @date 2024/3/13 10:46
 * @apiNote
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraderDataVo {

    private Integer id;

    private String logo;

    private String nickName;

    //带单时长/跟单时间
    private Integer days;

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private BigDecimal profitAmount = BigDecimal.ZERO;

    public TraderDataVo(Integer days, BigDecimal totalAmount, BigDecimal profitAmount) {
        this.days = days;
        this.totalAmount = totalAmount;
        this.profitAmount = profitAmount;
    }
}

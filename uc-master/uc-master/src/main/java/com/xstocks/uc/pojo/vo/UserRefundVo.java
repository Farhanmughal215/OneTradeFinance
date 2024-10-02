package com.xstocks.uc.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRefundVo implements Serializable {


    private static final long serialVersionUID = -2441937442444915840L;
    /**
     * 用户id
     */
    private Integer userId;


    private String address;

    /**
     * 交易员id
     */
    private Integer traderId;

    /**
     * 交易员用户ID
     */
    private Integer traderUid;

    /**
     * 金额
     */
    private BigDecimal amount;
}

package com.xstocks.uc.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FollowSetVo {

    private BigDecimal maxAmount;

    //跟单限额
    private BigDecimal amount;

    //跟单额度类型.0:固定金额,1:固定比例
    private Integer type;
}

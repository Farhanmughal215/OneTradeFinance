package com.xstocks.referral.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithdrawVo {

    private String address;

    private BigDecimal amount;

    private String net;
}

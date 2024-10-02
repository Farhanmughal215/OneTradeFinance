package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/*
{
      net: "ETH",
      txType, // 0充值 4 授权 -这里根据你自己定义
      txHash: tx.hash, //交易hash
      strAmounts,//金额
      address: '0x0000000000000000000000000000000000000000'
    }
 */
@Data
public class RechargeParam {

    @NotEmpty(message = "net required")
    private String net;

    // 0:充值 1:授权
    @NotEmpty(message = "net required")
    private Integer txType;

    @NotEmpty(message = "hash required")
    private String txHash;

    @NotEmpty(message = "amount required")
    private String strAmounts;

    @NotEmpty(message = "address required")
    private String address;
}

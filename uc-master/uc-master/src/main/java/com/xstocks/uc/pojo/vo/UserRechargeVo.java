package com.xstocks.uc.pojo.vo;

import lombok.Data;

@Data
public class UserRechargeVo {

    private String fromAddress;

    private String toAddress;

    private String amount;

    private String txHash;

    private String blockNumber;

    private String tokenContract;

    private Integer status;
}

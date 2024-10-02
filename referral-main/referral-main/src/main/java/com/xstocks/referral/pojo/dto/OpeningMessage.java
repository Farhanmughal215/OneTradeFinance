package com.xstocks.referral.pojo.dto;

import lombok.Data;

@Data
public class OpeningMessage {
    //{"uid":"1", //用户id
    // "pl":"-0.00000001", //浮动盈亏 正负数
    // "burstLine":"0.00000001", //浮动盈亏+保证金=资产
    // "margin":"0.00000001", //保证金
    // "currency":1, //标的id
    // "transactionType":"ask", // ask-卖-做空-long  bid-买-做多-short
    // "tradeType":"opening",//opening 持仓中
    // "sourcePrice":"0.00000001", //建仓价
    // "sourceSize":"1.00000000" //建仓数量
    // }

    private String uid;
    private String pl;
    private String burstLine;
    private String margin;
    private String transactionType;
    private String tradeType;
    private String sourcePrice;
    private String sourceSize;
    private Integer currency;
}

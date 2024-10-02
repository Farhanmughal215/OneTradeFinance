package com.xstocks.statistics.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MessageEntity {
//{"uid":"49","pl":-14515.0,"orderId":1705932969258258,"burstLine":26425.723457,
// "margin":"40940.723457","currency":"31","transactionType":"bid",
// "tradeType":"long","sourcePrice":40830.5,"sourceSize":"10"}


    private String uid;
    private String currency;
    private String transactionType;
    private String tradeType;
    private String sourceSize;
    private Long orderId;
    private BigDecimal sourcePrice;
    private BigDecimal burstLine;
    private BigDecimal pl;
    private BigDecimal margin;

    public MessageEntity() {
    }
}

package com.xstocks.uc.pojo.dto.ticker;

import lombok.Data;

@Data
public class TickerDetailDTO {

    private Long id;

    private String symbol;

    private String symbolRoot;

    private String name;

    private String active;

    private String address;

    private String branding;

    private String currency;

    private String description;

    private String homepage;

    private String ipoDate;

    private String local;

    private String market;

    private String marketCap;

    private String primaryExchange;

    private String type;

    private String typeDesc;

    private Integer isUse;

    private Integer leverNumber;
}

package com.xstocks.uc.pojo.dto.ticker;

import lombok.Data;

@Data
public class TickerAbbreviationDTO {
    private Long id;

    private String symbol;

    private String primaryExchange;
}

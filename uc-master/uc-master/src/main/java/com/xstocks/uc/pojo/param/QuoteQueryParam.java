package com.xstocks.uc.pojo.param;

import lombok.Data;

import java.util.Set;

@Data
public class QuoteQueryParam {
    private Set<Long> tickerIds;
}

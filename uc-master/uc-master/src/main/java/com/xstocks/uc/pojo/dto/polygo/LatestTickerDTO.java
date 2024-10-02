package com.xstocks.uc.pojo.dto.polygo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @ClassName LatestTickerDTO
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/16 15:51
 **/

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LatestTickerDTO {

    private LatestAggDTO day;

    private LatestQuoteDTO lastQuote;

    private LatestTradeDTO lastTrade;

    private LatestAggDTO min;

    private LatestAggDTO prevDay;

    private Double todaysChange;

    private Double todaysChangePerc;
}

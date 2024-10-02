package com.xstocks.uc.pojo.dto.polygo;

import lombok.Data;

/**
 * @ClassName TickerRefreshDTO
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/16 18:11
 **/

@Data
public class TickerRefreshDTO {

    private TickerLatestStateDTO tickerLatestStateDTO;

    private LatestAggDTO preDay;
}

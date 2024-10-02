package com.xstocks.uc.pojo.vo;

import com.xstocks.uc.pojo.dto.polygo.TickerLatestStateDTO;
import com.xstocks.uc.pojo.dto.ticker.TickerDetailDTO;
import lombok.Data;

/**
 * @ClassName TickerVO
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/16 21:35
 **/

@Data
public class TickerVO {

    private TickerDetailDTO tickerInfo;

    private TickerLatestStateDTO tickerState;

    private Boolean isCollected;
}

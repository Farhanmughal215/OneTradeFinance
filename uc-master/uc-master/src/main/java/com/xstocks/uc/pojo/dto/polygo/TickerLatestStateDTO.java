package com.xstocks.uc.pojo.dto.polygo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName TikerLatestStreamDTO
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/11 13:06
 **/

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class TickerLatestStateDTO {
    private Long ticker;

    private Long timestamp = System.currentTimeMillis();

    private Double ap = 0.00;

    private Double bp = 0.00;

    private Double tp = 0.00;

    private Double o = 0.00;

    private Double h = 0.00;

    private Double l = 0.00;

    private Double c = 0.00;

    private Double tc = 0.00;

    private Double tcv = 0.00;
}

package com.xstocks.uc.pojo.dto.polygo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @ClassName NbboDTO
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/16 15:11
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LatestAggDTO {
    private Double h = 0.00;
    private Double o = 0.00;
    private Double l = 0.00;
    private Double c = 0.00;
}

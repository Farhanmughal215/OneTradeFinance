package com.xstocks.uc.pojo.dto.polygo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @ClassName LatestTradeDTO
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/16 15:14
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LatestTradeDTO {
    /**
     * tp
     */
    @JsonAlias("p")
    private Double tp=0.00;
}

package com.xstocks.uc.pojo.dto.polygo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @ClassName LatestNbboDTO
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/16 15:16
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LatestQuoteDTO {
    /**
     * ap
     */
    @JsonAlias("P")
    private Double ap=0.00;
    /**
     * bp
     */
    @JsonAlias("p")
    private Double bp=0.00;
}

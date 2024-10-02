package com.xstocks.uc.pojo.dto.polygo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickerDTO {
    private LatestTickerDTO ticker;
    private String status;
    private String request_id;
}

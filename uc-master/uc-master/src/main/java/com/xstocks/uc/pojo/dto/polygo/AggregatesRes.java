package com.xstocks.uc.pojo.dto.polygo;

import lombok.Data;

import java.util.List;

@Data
public class AggregatesRes {
    private Boolean adjusted;
    private String next_url;
    private Long queryCount;
    private String request_id;
    private Long resultsCount;
    private String status;
    private String ticker;
    private List<AggregatesDTO> results;
}

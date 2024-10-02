package com.xstocks.uc.pojo.param.polygo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AggregatesQueryParam {
    private Long tickerId;
    private Integer multiplier;
    private String timeSpan;
    private String from;
    private String to;
    private Integer pageSize;
    private String nextPageToken;
    private String sort;
}

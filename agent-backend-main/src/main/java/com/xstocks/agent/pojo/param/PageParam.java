package com.xstocks.agent.pojo.param;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

@Data
public class PageParam {

    @Positive
    private Long pageNo;

    @Positive
    @Max(100)
    private Long pageSize;
}

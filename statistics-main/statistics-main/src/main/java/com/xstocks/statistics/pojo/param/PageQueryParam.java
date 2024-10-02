package com.xstocks.statistics.pojo.param;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;

/**
 * @ClassName PageQueryParam
 * @Author firtuss
 * @Date 2023/9/11 9:35
 **/
@Data
public class PageQueryParam {

    @Positive
    private Long pageNo;

    @Positive
    @Max(100)
    private Long pageSize;
}

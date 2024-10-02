package com.xstocks.uc.pojo.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @author Kevin
 * @date 2024/3/9 12:05
 * @apiNote
 */
@Data
public class TraderQueryParam  {

    //交易员资产最小额
    private Integer assetsMin;

    //交易员资产最大额
    private Integer assetsMax;

    //带单规模最小额
    private Integer leadingScaleMin;

    //带单规模最大额
    private Integer leadingScaleMax;

    //胜率
    private BigDecimal winRate;

    //带单时长
    private Integer leadingDays;

    //0：满员,1：不满员
    private Integer full;

    //排序字段
    private String sort;

    @Positive
    private Integer pageNum;

    @Positive
    @Max(100)
    private Integer pageSize;
}

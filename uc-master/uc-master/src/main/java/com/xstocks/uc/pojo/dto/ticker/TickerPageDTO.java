package com.xstocks.uc.pojo.dto.ticker;

import lombok.Data;

import java.util.List;

/**
 * @ClassName TickerPageDTO
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/12/14 11:52
 **/
@Data
public class TickerPageDTO {
    private Integer total;
    private Integer size;

    private Integer pages;

    private Integer current;

    private List<TickerDetailDTO> list;

    private Integer totalPage;

    private Integer currPage;

    private Integer totalCount;

    private Integer pageSize;
}

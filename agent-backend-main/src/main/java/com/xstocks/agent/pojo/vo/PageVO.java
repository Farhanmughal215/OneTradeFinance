package com.xstocks.agent.pojo.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 目的:最小化分页参数返回给前端
 */
@Data
public class PageVO<T> {
    //页码
    private int pageNo;
    //页大小
    private int pageSize;
    //总记录数
    private long totalCount;
    //总页数
    private long totalPages;
    protected List<T> records = Collections.emptyList();
}

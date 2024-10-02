package com.xstocks.statistics.pojo.vo;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

@Getter
public class Slice<T> {

    /**
     * 实体对象列表
     */
    private final List<T> data;

    /**
     * 每页记录数，如果pageSize<=0，则默认为20.
     */
    private final int pageSize;

    private final String nextPageToken;

    public Slice(int pageSize, List<T> data, String nextPageToken) {
        Objects.requireNonNull(data);
        this.pageSize = pageSize < 1 ? 20 : pageSize;
        this.data = data;
        this.nextPageToken = nextPageToken;
    }

    public static <T> Slice<T> of(int pageSize, List<T> data, String nextPageToken) {
        return new Slice<>(pageSize, data, nextPageToken);
    }

    public boolean hasNextSlice() {
        return pageSize <= data.size() || StringUtils.isNotBlank(nextPageToken);
    }

}

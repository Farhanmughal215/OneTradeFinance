package com.xstocks.uc.pojo.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NewsQueryParam extends PageQueryParam {

    private Integer newsType;

    private String lang;

    private String date;
}

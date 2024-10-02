package com.xstocks.uc.pojo.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class RecordQueryParam {

    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date day;

}

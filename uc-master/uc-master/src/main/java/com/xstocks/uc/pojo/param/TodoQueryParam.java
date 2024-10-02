package com.xstocks.uc.pojo.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TodoQueryParam extends PageQueryParam {

    private Long userId;

    private String userBizId;

    private String phone;

    private Integer todoType;

    private Integer todoStatus;
}

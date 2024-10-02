package com.xstocks.uc.pojo.param;

import lombok.Data;

@Data
public class CreateOrgParam {
    private String code;
    private String name;
    private String parentOrgCode;
}

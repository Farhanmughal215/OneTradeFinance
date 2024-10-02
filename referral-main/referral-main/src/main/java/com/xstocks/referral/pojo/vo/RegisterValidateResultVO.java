package com.xstocks.referral.pojo.vo;

import lombok.Data;

@Data
public class RegisterValidateResultVO {
    private Boolean valid;

    private Integer validateType;
}

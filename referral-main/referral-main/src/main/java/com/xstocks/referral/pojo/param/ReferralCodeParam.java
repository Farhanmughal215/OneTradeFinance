package com.xstocks.referral.pojo.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReferralCodeParam extends CommonParam {

    private String code;

}

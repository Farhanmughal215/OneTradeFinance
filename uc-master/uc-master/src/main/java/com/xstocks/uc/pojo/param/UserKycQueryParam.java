package com.xstocks.uc.pojo.param;

import com.xstocks.uc.pojo.enums.RoleTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserKycQueryParam extends PageQueryParam {

    private Long userId;

    private String userName;

    private Integer state;

}

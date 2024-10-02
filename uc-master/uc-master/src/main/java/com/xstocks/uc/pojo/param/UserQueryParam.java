package com.xstocks.uc.pojo.param;

import com.xstocks.uc.pojo.enums.RoleTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryParam extends PageQueryParam {

    private Long userId;

    private String userBizId;

    private String phone;

    private String orgCode;

    private Long inviteBy;

    private RoleTypeEnum role;
}

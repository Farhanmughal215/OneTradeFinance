package com.xstocks.uc.pojo.param;

import com.xstocks.uc.pojo.enums.RoleTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author firtuss
 */
@Data
public class CreateOrgUserParam {

    @NotBlank(message = "phone required")
    private String phone;

    @NotBlank(message = "psw required")
    private String psw;

    private Long createBy;

    private RoleTypeEnum roleTypeEnum;

    private String orgCode;

    private String userName;
}

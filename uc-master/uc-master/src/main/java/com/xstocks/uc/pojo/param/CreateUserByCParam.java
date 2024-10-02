package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author firtuss
 */
@Data
public class CreateUserByCParam {

    @NotBlank(message = "phone required")
    private String phone;

    @NotBlank(message = "psw required")
    private String psw;

    private String inviteBy;

    private String validateCode;

}

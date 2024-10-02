package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录参数
 *
 * @author firtuss
 */
@Data
public class ResetPwdParam {
    private Long userId;

    @NotBlank(message = "phone required")
    private String phone;

    @NotBlank(message = "password required")
    private String psw;

    @NotBlank(message = "verify code required")
    private String verifyCode;
}

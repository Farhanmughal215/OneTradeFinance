package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName RegisterParam
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/10/28 15:39
 **/

@Data
public class RegisterParam {
    @NotBlank(message = "phone required")
    private String phone;

    @NotBlank(message = "password required")
    private String psw;

    private String inviteCode;
}

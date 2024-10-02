package com.xstocks.uc.pojo.param;

import lombok.Data;

/**
 * @author Kevin
 * @date 2024/1/6 11:11
 * @apiNote 发送验证码
 */
@Data
public class LoginCommonParam {

    private String account;

    private String verifyCode;
}

package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author firtuss
 */
@Data
public class UpdatePwdParam {

    @NotBlank(message = "old password required")
    private String oldPwd;

    @NotBlank(message = "new password required")
    private String newPwd;
}

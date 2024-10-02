package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改当前用户的TRC地址 - 请求参数
 * @author Henry
 */
@Data
public class UpdateTrcAddressParam {

    /**
     * TRC地址
     */
    @NotBlank(message = "TRC address can not be null")
    private String trcAddress;
}

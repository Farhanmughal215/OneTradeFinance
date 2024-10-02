package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author firtuss
 */
@Data
public class ScanedUserParam {

    @NotBlank(message = "net required")
    private String net;

    @NotBlank(message = "address required")
    private String address;
}

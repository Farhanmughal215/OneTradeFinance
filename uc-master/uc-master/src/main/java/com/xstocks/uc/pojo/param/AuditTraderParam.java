package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Kevin
 * @date 2024/3/9 12:05
 * @apiNote
 */
@Data
public class AuditTraderParam {

    /**
     * id
     */
    @NotNull(message = "id required")
    private Integer id;

    /**
     * 0:待审核,1:通过,2:拒绝,3:封禁
     * status
     */
    @NotNull(message = "status required")
    private Integer status;

}

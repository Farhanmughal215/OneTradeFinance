package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Kevin
 * @date 2024/3/13 23:04
 * @apiNote
 */
@Data
public class TraderFollowParam {

    @NotNull(message = "traderUid required")
    private Long traderUid;
}

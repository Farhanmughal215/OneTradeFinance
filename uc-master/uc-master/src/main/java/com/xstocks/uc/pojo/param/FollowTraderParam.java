package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Kevin
 * @date 2024/3/9 12:05
 * @apiNote
 */
@Data
public class FollowTraderParam {

    /**
     * id
     */
    @NotNull(message = "traderId required")
    private Integer traderId;

    @NotNull(message = "amount required")
    private BigDecimal amount;

    @NotNull(message = "max amount required")
    private BigDecimal maxAmount;;

    @NotNull(message = "multiple type")
    private Integer type;
}

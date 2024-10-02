package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class WithdrawRecordApplyParam {

    private Long userId;

    /**
     * 提现金额
     */
    @NotNull(message = "amount required")
    private BigDecimal amount;

    /**
     * 手续费
     */
    @NotNull(message = "handling fee required")
    private BigDecimal handleFee;


    @NotEmpty(message = "currency type required")
    private String currency;

    @NotEmpty(message = "address required")
    private String address;


    @NotEmpty(message = "network required")
    private String network;

}

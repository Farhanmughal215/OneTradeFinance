package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class WithdrawRecordAuditParam {

    @NotEmpty(message = "id required")
    private Long id;

    private Long userId;

    /**
     * 状态,0:待审核,1:通过,-1:拒绝
     */
    @NotNull(message = "state required")
    private Integer state;

    private String remark;
}

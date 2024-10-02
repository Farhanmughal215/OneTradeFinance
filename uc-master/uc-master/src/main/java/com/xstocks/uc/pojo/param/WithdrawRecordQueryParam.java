package com.xstocks.uc.pojo.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WithdrawRecordQueryParam extends PageQueryParam {

    private Long userId;

}

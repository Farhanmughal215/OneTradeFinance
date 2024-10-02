package com.xstocks.uc.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TxStatusEnum {
    PENDING(0, "pending"),
    FAIL_OR_DELAY(1, "fail or delay"),
    SUCCESS_OR_AGREE(2, "success or agree");
    private final int code;
    private final String desc;
}

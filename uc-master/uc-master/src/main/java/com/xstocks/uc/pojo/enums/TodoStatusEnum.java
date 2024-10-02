package com.xstocks.uc.pojo.enums;

import com.xstocks.uc.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TodoStatusEnum {
    PENDING(0, "pending"),
    ACCEPT(1, "OK"),
    DENY(2, "NO");
    private final int code;
    private final String desc;

    public static TodoStatusEnum getByCode(int code) {
        for (TodoStatusEnum value : TodoStatusEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new BizException(ErrorCode.ILLEGAL_REQUEST);
    }
}

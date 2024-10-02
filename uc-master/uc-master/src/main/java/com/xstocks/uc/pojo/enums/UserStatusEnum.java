package com.xstocks.uc.pojo.enums;

import com.xstocks.uc.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatusEnum {
    FREEZE(0, "freeze"),
    TRADABLE(1, "tradable");
    private final int code;
    private final String desc;

    public static UserStatusEnum getByCode(int code) {
        for (UserStatusEnum value : UserStatusEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new BizException(ErrorCode.ILLEGAL_REQUEST);
    }
}

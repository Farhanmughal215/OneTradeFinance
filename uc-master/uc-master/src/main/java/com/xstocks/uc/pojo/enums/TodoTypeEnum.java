package com.xstocks.uc.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TodoTypeEnum {
    PROFILE_REVIEW(0, "user profile review"),
    BANKNO_REVIEW(1,"user bankno review"),
    DEPOSIT(2, "deposit"),
    WITHDRAW(3, "withdraw");
    private final int code;
    private final String desc;
}

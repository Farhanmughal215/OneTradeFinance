package com.xstocks.uc.pojo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author firtuss
 */

@Getter
@AllArgsConstructor
public enum RoleTypeEnum {
    ROLE_SYSTEM(1L, "ROLE_SYSTEM", "system"),
    ROLE_ADMIN(2L, "ROLE_ADMIN", "administrator"),
    ROLE_OPERATOR(3L, "ROLE_OPERATOR", "operator"),
    ROLE_USER(4L, "ROLE_USER", "user"),
    ROLE_ROOT(5L, "ROLE_ROOT", "root");

    private final Long roleId;

    private final String code;

    private final String name;

    public static RoleTypeEnum getByCode(String code) {
        for (RoleTypeEnum value : RoleTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ROLE_USER;
    }
}

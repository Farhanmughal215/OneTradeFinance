package com.xstocks.uc.pojo.enums;

/**
 * @author Kevin
 * @date 2024/3/22 16:21
 * @apiNote
 */
public enum TransactionalEnum {
    //    'PENDING', 'PROCESSING', 'SUCCESS', 'FAILED'
    PENDING(0, "PENDING"),
    PROCESSING(1, "PROCESSING"),
    SUCCESS(2, "SUCCESS"),
    FAILED(3, "FAILED");
    private final int code;
    private final String desc;

    TransactionalEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

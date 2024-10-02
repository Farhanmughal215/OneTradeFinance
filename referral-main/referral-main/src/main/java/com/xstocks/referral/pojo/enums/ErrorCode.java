package com.xstocks.referral.pojo.enums;

public enum ErrorCode {
    SUCCESS(0, "success"),
    DEFAULT_ERROR(500, "fail"),
    TOO_MANY_REQUESTS(429, "too many requests"),
    METHOD_NOT_SUPPORT(400, "method not support"),
    UNAUTHORIZED(403, "unauthorized"),
    NOT_FOUND(404,"not found"),
    UNAUTHENTICATED(401, "unauthenticated please login"),

    CHAIN_NOT_SUPPORTED(400, "chain not support"),

    ILLEGAL_REQUEST(400, "illegal request"),

    ILLEGAL_STATE(500, "illegal state"),

    UNSUPPORTED_METHOD(500, "unsupported method"),

    SERVER_INTERNAL_ERROR(500, "server internal error"),
    CODE_ALREADY_EXISTS_ERROR(1001, "Invitation code already exists"),
    CODE_NOT_EXISTS_ERROR(1002, "Invitation code does not exists"),
    ILLICIT_CHARACTER_ERROR(1003, "Illegal characters"),
    OVER_MAX_COUNT_ERROR(1004, "Each user can create a maximum of 6 invitation codes"),
    USER_ALREADY_BIND_ERROR(1005, "User already bound"),
    CAN_NOT_BIND_OWNER_ERROR(1006, "Cannot bind your own invitation code"),
    USER_NOT_EXIST_ERROR(1007, "User data abnormal"),
    NOT_ENOUGH_REBATES_ERROR(1008, "Insufficient balance");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

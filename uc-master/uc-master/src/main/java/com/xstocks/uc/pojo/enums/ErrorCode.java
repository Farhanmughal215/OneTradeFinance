package com.xstocks.uc.pojo.enums;

public enum ErrorCode {
    SUCCESS(0, "success"),
    DEFAULT_ERROR(500, "fail"),
    TOO_MANY_REQUESTS(429, "too many requests"),
    METHOD_NOT_SUPPORT(400, "method not support"),
    UNAUTHORIZED(403, "unauthorized"),
    NOT_FOUND(404,"not found"),
    UNAUTHENTICATED(401, "unauthenticated please login"),
    CHAIN_TYPE_REQUIRED(400, "chainType required"),
    PARAM_ERROR(400, "params required"),

    CHAIN_NOT_SUPPORTED(400, "chain not support"),
    NO_CORRESPOND_CHAIN_API(400, "no correspond chain api"),

    ILLEGAL_REQUEST(400, "illegal request"),

    ILLEGAL_STATE(500, "illegal state"),

    UNSUPPORTED_METHOD(500, "unsupported method"),

    SERVER_INTERNAL_ERROR(500, "server internal error"),
    WATCH_DATA_FAIL(500, "watchdata fail"),
    USER_NOT_FOUND_ERROR(600, "user not found"),

    USER_NOT_EXIST_ERROR(1000, "no corresponding user"),

    USER_STATUS_NOT_ALLOW_ERROR(1001, "user status not allow"),

    USER_ALREADY_FOLLOWED_ERROR(1002, "user already follow trader"),
    TRADER_ALREADY_AUDIT_ERROR(1003, "trader has audited"),
    TRADER_NOT_EXIST_ERROR(1004, "no corresponding trader"),
    TRADER_AUDIT_ERROR(1005,"create trader ETH address error"),
    ILLEGAL_STATE_ERROR(1006,"illegal state"),
    HAVE_OPENING_ORDER_ERROR(1007,"Have a position order error"),
    ILLEGITMATE_NICKNAME_ERROR(1008,"illegitimate nickname"),
    SYNOPSIS_TOO_LONG_ERROR(1009,"synopsis is too long");

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

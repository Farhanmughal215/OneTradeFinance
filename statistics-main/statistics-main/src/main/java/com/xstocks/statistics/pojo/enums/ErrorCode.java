package com.xstocks.statistics.pojo.enums;

public enum ErrorCode {
    SUCCESS(0, "success"),
    DEFAULT_ERROR(500, "fail"),
    TOO_MANY_REQUESTS(429, "too many requests"),
    METHOD_NOT_SUPPORT(400, "method not support"),
    ILLEGAL_ARGUMENT(400, "argument require"),
    UNAUTHORIZED(403, "unauthorized"),
    NOT_FOUND(404, "not found"),
    UNAUTHENTICATED(401, "unauthenticated please login"),
    CHAIN_TYPE_REQUIRED(400, "chainType required"),

    CHAIN_NOT_SUPPORTED(400, "chain not support"),
    NO_CORRESPOND_CHAIN_API(400, "no correspond chain api"),

    ILLEGAL_REQUEST(400, "illegal request"),

    ILLEGAL_STATE(500, "illegal state"),

    UNSUPPORTED_METHOD(500, "unsupported method"),

    SERVER_INTERNAL_ERROR(500, "server internal error"),
    WATCH_DATA_FAIL(500, "watchdata fail"),
    ILLEGAL_FILE(500, "illegal file"),
    IMPORT_FAIL(500, "import fail");

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

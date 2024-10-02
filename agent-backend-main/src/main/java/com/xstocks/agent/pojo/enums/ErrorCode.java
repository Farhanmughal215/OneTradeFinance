package com.xstocks.agent.pojo.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(0, "success"),
    DEFAULT_ERROR(500, "fail"),
    TOO_MANY_REQUESTS(429, "too many requests"),
    METHOD_NOT_SUPPORT(400, "method not support"),
    UNAUTHORIZED(403, "unauthorized"),
    NOT_FOUND(404, "not found"),
    UNAUTHENTICATED(401, "unauthenticated please login"),

    CHAIN_NOT_SUPPORTED(400, "chain not support"),

    ILLEGAL_REQUEST(400, "illegal request"),

    ILLEGAL_STATE(500, "illegal state"),

    UNSUPPORTED_METHOD(500, "unsupported method"),

    SERVER_INTERNAL_ERROR(500, "server internal error");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}

package com.xstocks.statistics.exception;

import com.xstocks.statistics.pojo.enums.ErrorCode;

public class BizException extends RuntimeException {

    private static final long serialVersionUID = -7356920609839198963L;
    private ErrorCode errCode;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.setErrCode(errorCode);
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.setErrCode(errorCode);
    }

    public BizException(Throwable cause, ErrorCode errorCode, String message) {
        super(message, cause);
        this.setErrCode(errorCode);
    }

    public ErrorCode getErrCode() {
        return this.errCode;
    }

    public void setErrCode(ErrorCode errCode) {
        this.errCode = errCode;
    }
}

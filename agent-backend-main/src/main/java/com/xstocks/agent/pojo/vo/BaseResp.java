package com.xstocks.agent.pojo.vo;

import com.xstocks.agent.pojo.enums.ErrorCode;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class BaseResp<T> {

    private int code;

    private String message;

    private T data;

    public BaseResp(int code, String message, T data) {
        this.setCode(code);
        this.setMessage(message);
        this.setData(data);
    }

    /**
     * 成功
     */
    public static <T> BaseResp<T> success() {
        return new BaseResp<T>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功 + 返回数据
     */
    public static <T> BaseResp<T> success(T data) {
        return new BaseResp<T>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功 + 消息 + 返回数据
     */
    public static <T> BaseResp<T> success(String message, T data) {
        return new BaseResp<T>(ErrorCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 默认失败
     */
    public static <T> BaseResp<T> defaultError() {
        return new BaseResp<T>(ErrorCode.DEFAULT_ERROR.getCode(), ErrorCode.DEFAULT_ERROR.getMessage(), null);
    }

    /**
     * 默认失败失败 + 消息
     */
    public static <T> BaseResp<T> defaultError(String message) {
        return new BaseResp<T>(ErrorCode.DEFAULT_ERROR.getCode(), message, null);
    }

    /**
     * 失败 + code
     */
    public static <T> BaseResp<T> error(ErrorCode errorCode) {
        return new BaseResp<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败 + code + 消息
     */
    public static <T> BaseResp<T> error(ErrorCode errorCode, String message) {
        return new BaseResp<>(errorCode.getCode(), message, null);
    }


    /**
     * 失败 + code + 消息 + data
     */
    public static <T> BaseResp<T> error(ErrorCode errorCode, String message,T data) {
        return new BaseResp<>(errorCode.getCode(), message, data);
    }

}

//package com.xstocks.rc.config;
//
//import com.xstocks.rc.exception.BizException;
//import com.xstocks.rc.pojo.enums.ErrorCode;
//import com.xstocks.rc.pojo.vo.BaseResp;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import javax.servlet.http.HttpServletResponse;
//
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//    /**
//     * 其余异常错误
//     */
//    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
//    public BaseResp<Object> handleException(Exception e, HttpServletResponse response) {
//        log.error("request internal error: ", e);
//        String message = StringUtils.EMPTY;
//        if (e.getStackTrace().length > 0) {
//            StackTraceElement first = e.getStackTrace()[0];
//            message = String.format("%s [%s:%d]", message, first.getFileName(), first.getLineNumber());
//        }
//        response.setStatus(200);
//        return BaseResp.defaultError(message);
//    }
//
//    /**
//     * 空指针异常
//     */
//    @ExceptionHandler(NullPointerException.class)
//    public BaseResp<Object> handleNullPointerException(NullPointerException e, HttpServletResponse response) {
//        String message = "空指针异常";
//        if (e.getStackTrace().length > 0) {
//            StackTraceElement first = e.getStackTrace()[0];
//            message = String.format("%s [%s:%d]", message, first.getFileName(), first.getLineNumber());
//        }
//        log.error(message, e);
//        response.setStatus(200);
//        return BaseResp.defaultError(message);
//    }
//
//    /**
//     * http not supported 异常
//     */
//    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
//    public BaseResp<Object> handleNotSupportedRequestException(HttpRequestMethodNotSupportedException e,
//                                                               HttpServletResponse response) {
//        response.setStatus(200);
//        return BaseResp.error(ErrorCode.METHOD_NOT_SUPPORT);
//    }
//
//    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
//    public BaseResp<Object> handleNotSupportedRequestException(MethodArgumentNotValidException e,
//                                                               HttpServletResponse response) {
//        response.setStatus(200);
//        return BaseResp.error(ErrorCode.ILLEGAL_REQUEST, e.getMessage());
//    }
//
//    /**
//     * 业务异常
//     */
//    @ExceptionHandler(value = {BizException.class})
//    public BaseResp<Object> handleDomainException(BizException e, HttpServletResponse response) {
//        log.error("request domain error:", e);
//        response.setStatus(200);
//        return BaseResp.error(e.getErrCode(), e.getMessage());
//    }
//
//    /**
//     * 未授权
//     *
//     * @param e
//     * @param response
//     * @return
//     */
//    @ExceptionHandler(value = {AccessDeniedException.class})
//    public BaseResp<Object> handleAccessDeniedException(AccessDeniedException e, HttpServletResponse response) {
//        response.setStatus(200);
//        return BaseResp.error(ErrorCode.UNAUTHORIZED);
//    }
//
//    /**
//     * 未认证
//     *
//     * @param e
//     * @param response
//     * @return
//     */
//    @ExceptionHandler(value = {AuthenticationException.class})
//    public BaseResp<Object> handleAuthenticationException(AuthenticationException e, HttpServletResponse response) {
//        response.setStatus(200);
//        return BaseResp.error(ErrorCode.UNAUTHENTICATED);
//    }
//}

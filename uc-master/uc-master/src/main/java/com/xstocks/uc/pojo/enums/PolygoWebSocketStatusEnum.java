package com.xstocks.uc.pojo.enums;

import lombok.Getter;

/**
 * @ClassName NodeEnum
 * @Description TODO
 * @Author firtuss
 * @Date 2022/11/9 14:02
 **/
@Getter
public enum PolygoWebSocketStatusEnum {
    connected,
    auth_success,
    auth_timeout,
    auth_failed
}

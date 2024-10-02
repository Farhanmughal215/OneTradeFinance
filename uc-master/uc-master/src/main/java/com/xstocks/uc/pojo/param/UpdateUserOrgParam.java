package com.xstocks.uc.pojo.param;

import lombok.Data;

/**
 * @ClassName UpdateUserOrgParam
 * @Description TODO
 * @Author junfudong@xiaomi.com
 * @Date 2023/11/22 11:43
 **/

@Data
public class UpdateUserOrgParam {
    private Long userId;

    private String targetOrgCode;
}

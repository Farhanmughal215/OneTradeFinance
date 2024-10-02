package com.xstocks.uc.pojo.param;

import com.xstocks.uc.pojo.enums.RoleTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author firtuss
 */
@Data
public class UpdateUserParam {
    @NotNull(message = "userId required")
    private Long userId;

    private String orgCode;

    private Integer userStatus;

    private String userName;

    private String address;

    private String bankNo;

    private String inviteBy;

    private RoleTypeEnum roleTypeEnum;


    /**
     * 头像
     */
    private String logo;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 个人简介
     */
    private String synopsis;
}

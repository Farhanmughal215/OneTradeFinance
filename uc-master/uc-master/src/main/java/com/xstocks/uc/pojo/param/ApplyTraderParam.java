package com.xstocks.uc.pojo.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Kevin
 * @date 2024/3/9 12:05
 * @apiNote
 */
@Data
public class ApplyTraderParam {

    /**
     * 头像
     */
    @NotEmpty(message = "logo required")
    private String logo;

    /**
     * 昵称
     */
    @NotEmpty(message = "nick name required")
    private String nickName;

    /**
     * 个人简介
     */
    @NotEmpty(message = "synopsis required")
    private String synopsis;

    /**
     * 邮箱
     */
    @NotEmpty(message = "email required")
    private String email;

    /**
     * 社交媒体账号
     */
    private String socialAccount;

    /**
     * 分润比例,百分比
     */
    @NotNull(message = "profit ratio required")
    private Integer profitRatio;

    private String contractCertificate;
}

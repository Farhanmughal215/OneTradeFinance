package com.xstocks.uc.pojo.vo;

import lombok.Data;

@Data
public class TraderInfoDataVo {
    /**
     * id
     */
    private Integer traderUid;

    /**
     * 关注人数
     */
    private Integer followerCount;

    /**
     * 最大带单人数
     */
    private Integer maxFollowCount;
}

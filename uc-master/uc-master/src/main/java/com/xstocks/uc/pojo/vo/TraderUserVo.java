package com.xstocks.uc.pojo.vo;

import lombok.Data;

@Data
public class TraderUserVo {
    /**
     * id
     */
    private Integer id;

    private Integer status;

    /**
     * 用户ID
     */
    private Long userId;


    private String userAddress;

    /**
     * 0:待审核,1:通过,2:拒绝

     @JsonIgnore private Integer status;
     */
    /**
     * 邮箱
     */
    private String email;

    /**
     * 社交媒体账号
     */
    private String socialAccount;

    /**
     * 分润比例,百分比
     */
    private Integer profitRatio;


    //合约交易证明
    private String contractCertificate;


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


    //0:未跟单,1:已跟单
    private Integer followStatus;
}

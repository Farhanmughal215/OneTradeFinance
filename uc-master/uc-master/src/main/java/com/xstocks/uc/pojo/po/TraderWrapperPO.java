package com.xstocks.uc.pojo.po;

import lombok.Data;

@Data
public class TraderWrapperPO {

    private String logo;

    private String nickName;

    private Integer userId;

    private Integer maxFollowCount;

    private Integer currentFollowCount;

    private Integer followStatus;
}

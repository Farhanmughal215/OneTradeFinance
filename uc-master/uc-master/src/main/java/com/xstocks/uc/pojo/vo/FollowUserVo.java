package com.xstocks.uc.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Kevin
 * @date 2024/3/11 11:02
 * @apiNote
 */
@Data
public class FollowUserVo {

    private Integer userId;

    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 分润比例,百分比
     */
    private Integer profitRatio;

    //跟单限额
    private BigDecimal amount;

    private BigDecimal maxAmount;

    //跟单额度类型.0:固定金额,1:固定比例
    private Integer type;
}

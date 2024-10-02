package com.xstocks.referral.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 邀请关系表
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_referral_relation")
public class ReferralRelation implements Serializable {


    private static final long serialVersionUID = 7786915963065429249L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


    /**
     * 邀请用户id
     */
    private Integer referralUserId;

    /**
     * 总回扣
     */
    private BigDecimal totalRebates;

    /**
     * 邀请码id
     */
    private Integer referralId;

    /**
     * 被邀请用户ID
     */
    private Integer userId;

    /**
     * 用户地址
     */
    private String userAddress;

    /**
     * 用户logo
     */
    private String userLogo;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

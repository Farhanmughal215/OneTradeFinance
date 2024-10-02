package com.xstocks.referral.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 邀请码表
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_referral_code")
public class ReferralCode implements Serializable {


    private static final long serialVersionUID = -2504010354843565437L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 邀请码
     */
    private String referralCode;

    /**
     * 交易量
     */
    private BigDecimal totalVolume;

    /**
     * 邀请人数
     */
    private Integer referralNum;

    /**
     * 总收益
     */
    private BigDecimal totalRebates;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

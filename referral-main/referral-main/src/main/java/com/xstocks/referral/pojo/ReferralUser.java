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
 * 邀请用户表
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_referral_user")
public class ReferralUser implements Serializable {


    private static final long serialVersionUID = 7362062899338346214L;
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
     * 用户地址
     */
    private String address;

    /**
     * 邀请人数
     */
    private Integer referralNum;

    /**
     * 总收益
     */
    private BigDecimal totalVolume;

    /**
     * 回扣
     */
    private BigDecimal rebates;

    /**
     * 可提现金额
     */
    private BigDecimal claimableAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

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
 * 裂变提现表
 * </p>
 *
 * @author kevin
 * @since 2024-04-13
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_referral_withdraw")
public class ReferralWithdraw implements Serializable {


    private static final long serialVersionUID = -2479850466571331729L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 网络
     */
    private String net;

    private String orderNo;

    /**
     * 交易地址
     */
    private String address;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 交易hash
     */
    private String txHash;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

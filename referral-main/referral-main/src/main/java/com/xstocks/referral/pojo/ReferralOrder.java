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
 * 返佣订单
 * </p>
 *
 * @author kevin
 * @since 2024-04-09
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_referral_order")
public class ReferralOrder implements Serializable {


    private static final long serialVersionUID = -3691340586976092891L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 返佣比例
     */
    private BigDecimal rate;

    /**
     * 返佣金额
     */
    private BigDecimal amount;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 订单创建时间
     */
    private LocalDateTime orderTime;
}

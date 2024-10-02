package com.xstocks.uc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 充值记录表
 * </p>
 *
 * @author kevin
 * @since 2024-01-09
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_recharge_record")
public class RechargeRecord implements Serializable {


    private static final long serialVersionUID = 7325815851348280284L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 网络
     */
    private String net;

    /**
     * 类型,0:充值 1:授权
     */
    private Integer txType;

    /**
     * 交易hash
     */
    private String txHash;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 地址
     */
    private String address;

    /**
     * 创建时间
     */
    private Date createTime;
}

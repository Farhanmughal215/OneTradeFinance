package com.xstocks.uc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 交易员带单用户表
 * </p>
 *
 * @author kevin
 * @since 2024-03-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_trader_follow")
public class TraderFollow implements Serializable {


    private static final long serialVersionUID = 3375907554152880393L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 交易员ID
     */
    private Integer traderId;

    /**
     * 用户ID
     */
    private Long userId;


    private BigDecimal maxAmount;

    //跟单限额
    private BigDecimal amount;

    //跟单额度类型.0:固定金额,1:固定比例
    private Integer type;

    //充值状态,0:未充值,1:已充值
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}

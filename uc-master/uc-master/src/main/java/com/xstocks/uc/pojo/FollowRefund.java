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
 * 跟单退款表
 * </p>
 *
 * @author kevin
 * @since 2024-03-31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_follow_refund")
public class FollowRefund implements Serializable {


    private static final long serialVersionUID = 7777573443683633833L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;


    private String userAddress;

    /**
     * 交易员id
     */
    private Integer traderId;

    /**
     * 交易员用户ID
     */
    private Integer traderUid;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 状态,0:失败,1:成功
     */
    private Integer status;

    /**
     * 交易hash
     */
    private String txHash;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}

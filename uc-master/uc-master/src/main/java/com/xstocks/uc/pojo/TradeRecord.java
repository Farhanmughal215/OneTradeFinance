package com.xstocks.uc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 资金划转表
 * </p>
 *
 * @author kevin
 * @since 2024-03-20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_trade_record")
public class TradeRecord implements Serializable {


    private static final long serialVersionUID = 1158446103390789433L;
    /**
     * Id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 币种
     */
    private String currency;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 金额
     */
    private String amount;

    /**
     * 发起转账地址
     */
    @JsonIgnore
    private String fromAddress;

    /**
     * 类型,0:到资金账户,1:到交易账户
     */
    private Integer type;

    /**
     * 接收地址
     */
    @JsonIgnore
    private String toAddress;

    /**
     * 交易hash
     */
    @JsonIgnore
    private String txHash;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(exist = false)
    private String date;

    @TableField(exist = false)
    private String time;
}

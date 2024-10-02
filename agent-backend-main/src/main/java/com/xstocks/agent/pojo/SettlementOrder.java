package com.xstocks.agent.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 结算订单表
 * </p>
 *
 * @author kevin
 * @since 2024-09-03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_settlement_order")
public class SettlementOrder implements Serializable {


    private static final long serialVersionUID = 8196329222916475433L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 钱包地址
     */
    private String address;

    /**
     * 下单时间
     */
    private String createTs;

    /**
     * 结束时间
     */
    private String endTs;

    /**
     * 手续费(u)
     */
    private String fee;

    /**
     * 订单盈亏(u)
     */
    private String realPl;

    /**
     * 订单结算时间
     */
    private String settlementTs;

    /**
     * 结算状态,0:违反用,1:已返佣
     */
    private String status;
}

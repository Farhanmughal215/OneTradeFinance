package com.xstocks.statistics.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户原始订单数据
 * </p>
 *
 * @author kevin
 * @since 2024-05-20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_order_metadata")
public class OrderMetadata implements Serializable {


    private static final long serialVersionUID = 3795905587480402483L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 币种
     */
    private String symbol;

    /**
     * 用户ID
     */
    private String uid;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 类型,long:建仓,close:平仓,break:爆仓,long_market_cap:建仓市值,close_market_cap:平仓市值,break_market_cap:爆仓市值,
     */
    private String type;

    /**
     * 成交时间
     */
    private String doneTs;

    /**
     * 金额
     */
    private String amount;


    // 保证金-有杠杆就有这个参数
    private String marginAmount;


    /**
     * 平仓单id-该id记录了原订单id
     */
    private String reverseOrderId;

    /**
     * spot 现货，long 做多，short 做空'
     */
    private String tradeType;

    /**
     * bid 买，ask卖
     */
    private String transactionType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

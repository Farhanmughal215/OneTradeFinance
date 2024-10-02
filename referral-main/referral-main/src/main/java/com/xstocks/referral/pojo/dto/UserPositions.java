package com.xstocks.referral.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户持仓表(UserPositions)实体类
 * user_positions
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserPositions implements Serializable {

    public static final String LIMIT = "limit"; // 限价单-建仓用
    public static final String MARKET = "market"; // 市价单-建仓用
    public static final String TRAIL_LIT = "trailLit"; // 触及追踪限价单/买卖都有可能-建仓用

    public static final String TRAIL = "trail"; // 跟踪止损市价单-平仓用
    public static final String TRAIL_LIMIT = "trailLimit"; // 跟踪止损限价单-平仓用
    public static final String STOP_PROFIT = "stopProfit"; // 止盈单-平仓用
    public static final String STOP_LOSS = "stopLoss"; // 止损单-平仓用

    public static final String BID = "bid"; // 买-做多 long
    public static final String ASK = "ask"; // 卖-做空 short

    public static final String PENDING = "pending"; // 挂单中-建仓

    public static final String OPENING = "opening"; // 持仓中

    public static final String CLOSING = "closing"; // 持仓全部卖出-已经关闭

    public static final String REVERSE_CLOSING = "reverse_closing"; // 反向平仓单-给已有持仓单的反向单用于标记-包含限价单-止盈单-止损单

    public static final String CANCEL = "cancel"; // 取消订单

    public static final String FAILED = "failed"; // 失败

    public static final String LONG = "long"; // 做多
    public static final String SHORT = "short"; // 做空
    private static final long serialVersionUID = -4366517095118112281L;

    private Long id;

    /**
     * 用户uid
     */
    private String uid;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 买入bid / 卖出ask
     */
    private String transactionType;

    /**
     * 订单类型-限价单 limit /市价单 market/ stopProfit 止盈单/ stopLoss 止损单
     */
    private String orderType;

    /**
     * spot：现货，long：做多，short：做空
     */
    private String tradeType;

    /**
     * 订单-dex不用合并订单，所以需要订单id
     */
    private Long orderId;

    /**
     * 标的id
     */
    private String currency;

    /**
     * 交易引擎编号
     */
    private String symbol;

    /**
     * 原持仓数
     */
    private String sourceSize;

    /**
     * 原持仓价-建仓价格
     */
    private BigDecimal sourcePrice;

    /**
     * <p>1.如果是建仓转变为持仓后，这里是实际持仓价
     * <p>2.如果是已平仓-那么这里是平仓成交价格
     */
    private BigDecimal donePrice;

    // 保证金-有杠杆就有这个参数
    private String marginAmount;

    // 杠杆倍数
    private String lever;

    // 持仓状态
    private String positionStatus;

    /**
     * 创建时间
     */
    private String createTs;

    /**
     * 创建时间
     */
    private String doneTs;

    /**
     * 平仓单id-该id记录了原订单id
     */
    private Long reverseOrderId;

    /**
     * 浮动盈亏
     */
    private String pl;

    // 止盈订单id
    private Long takeProfitOrderId;

    // 止损订单id
    private Long stopLossOrderId;

    //追踪金额
    private BigDecimal trailAmt;

    //限价偏离值
    private BigDecimal lmtOffset;

    //下单后的最高价位
    private BigDecimal hPrice;

    //手续费比例
    private BigDecimal feeRatio;


    private BigDecimal fee;

}
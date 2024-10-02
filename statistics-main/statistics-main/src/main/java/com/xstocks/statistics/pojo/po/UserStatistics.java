package com.xstocks.statistics.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户数据统计
 * </p>
 *
 * @author kevin
 * @since 2024-05-20
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_user_statistics")
public class UserStatistics implements Serializable {


    private static final long serialVersionUID = 8137511722128419119L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 日期，格式：yyyy-MM-dd
     */
    private LocalDate statisticsDate;

    /**
     * 标的
     */
    private String symbol;

    /**
     * 地址
     */
    private String address;

    /**
     * 类型,new_reg:新增注册地址数,reg_sum:注册地址总数,trade_sum:每日交易地址数,long:建仓笔数,close:平仓笔数,break:爆仓笔数,long_market_cap:建仓市值,close_market_cap:平仓市值,break_market_cap:爆仓市值,profit_loss_sum:所有地址总实现盈亏,max_profit:最大实现盈利,max_loss:最大实现亏损,remain_trade:留存地址交易笔数,remain_long:留存地址建仓笔数,remain_close:留存地址平仓笔数,remain_break:留存地址爆仓笔数,remain_trade_address:留存地址交易地址数,remain_long_address:留存地址建仓地址数,remain_close_address:留存地址平仓地址数,remain_break_address:留存地址爆仓地址数,trade_count:每日日内交易笔数,avg_opening_time:每日日内交易平均持仓时间,bid_long_market_cap:多单建仓市值,bid_close_market_cap:多单平仓市值,bid_count:多单笔数,ask_long_market_cap:空单建仓市值,ask_close_market_cap:空单平仓市值,bid_trade_count:空单笔数
     */
    private String type;

    /**
     * 金额/数量
     */
    private String amount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

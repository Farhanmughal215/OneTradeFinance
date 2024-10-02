package com.xstocks.statistics.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 总榜-待放榜
 * </p>
 *
 * @author kevin
 * @since 2024-05-23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_rank_overall_pending")
public class RankOverallPending implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类型,trade_count:总交易笔数,realize:总实现盈亏,profit_rate:总收益率
     */
    private String type;

    /**
     * 用户uid
     */
    private String uid;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 数量
     */
    private String amount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

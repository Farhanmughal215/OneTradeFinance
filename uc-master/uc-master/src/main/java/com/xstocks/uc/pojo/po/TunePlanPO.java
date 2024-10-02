package com.xstocks.uc.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 扎针计划
 * @TableName tune_plan
 */
@TableName(value ="tune_plan")
@Data
public class TunePlanPO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标的id
     */
    private Long tickerId;

    /**
     * 涨跌:0跌,1涨
     */
    private Integer isUp;

    /**
     * 期望价格
     */
    private BigDecimal expectedPrice;

    /**
     * start time
     */
    private Long startTime;

    /**
     * end time
     */
    private Long endTime;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * create by
     */
    private Long createBy;

    /**
     * update by
     */
    private Long updateBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
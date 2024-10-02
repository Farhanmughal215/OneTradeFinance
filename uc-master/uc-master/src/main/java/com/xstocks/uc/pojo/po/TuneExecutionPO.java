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
 * 扎针计划拟合数据
 * @TableName tune_execution
 */
@TableName(value ="tune_execution")
@Data
public class TuneExecutionPO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 计划id
     */
    private Long tunePlanId;

    /**
     * 标的id
     */
    private Long tickerId;

    /**
     * 时间戳
     */
    private Long ts;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * create_time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
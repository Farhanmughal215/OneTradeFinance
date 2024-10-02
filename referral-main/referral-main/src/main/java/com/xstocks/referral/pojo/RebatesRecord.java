package com.xstocks.referral.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户互返佣记录表
 * </p>
 *
 * @author kevin
 * @since 2024-04-10
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_rebates_record")
public class RebatesRecord implements Serializable {


    private static final long serialVersionUID = -7584384206637600077L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 回扣
     */
    private BigDecimal rebates;

    /**
     * 状态,0:待计算,1:已计算
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

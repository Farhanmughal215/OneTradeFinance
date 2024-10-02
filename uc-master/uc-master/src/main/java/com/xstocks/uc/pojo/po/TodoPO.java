package com.xstocks.uc.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName todo
 */
@TableName(value ="todo")
@Data
public class TodoPO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * todo type,0用户资料审核1充值2提现
     */
    private Integer todoType;

    /**
     * create by
     */
    private Long createBy;

    /**
     * update by
     */
    private Long updateBy;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * attachments
     */
    private String attachments;

    /**
     * description
     */
    private String description;

    /**
     * todo status,0Pending1Ok2No
     */
    private Integer todoStatus;

    /**
     * feed back when not approval
     */
    private String feedBack;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
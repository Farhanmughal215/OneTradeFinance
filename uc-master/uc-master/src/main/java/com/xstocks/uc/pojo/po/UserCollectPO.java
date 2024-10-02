package com.xstocks.uc.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * user collect
 * @TableName user_collect
 */
@TableName(value ="user_collect")
@Data
public class UserCollectPO implements Serializable {
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
     * ticker id
     */
    private Long tickerId;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * is delete:0no,1yes
     */
    private Integer isDel;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
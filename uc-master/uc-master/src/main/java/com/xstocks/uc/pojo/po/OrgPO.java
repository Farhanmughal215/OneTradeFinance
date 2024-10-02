package com.xstocks.uc.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * org
 * @TableName org
 */
@TableName(value ="org")
@Data
public class OrgPO implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * id
     */
    private String code;

    /**
     * org name
     */
    private String name;

    /**
     * org level
     */
    private Integer level;

    /**
     * org code path
     */
    private String orgCodePath;

    /**
     * org name path
     */
    private String orgNamePath;

    /**
     * parent org code
     */
    private String parentOrgCode;

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

    /**
     * add by
     */
    private Long addBy;

    /**
     * update by
     */
    private Long updateBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
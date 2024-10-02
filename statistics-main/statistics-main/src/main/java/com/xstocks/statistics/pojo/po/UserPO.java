package com.xstocks.statistics.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * user
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
public class UserPO implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 2056942629420555273L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * phone
     */
    private String phone;

    /**
     * password
     */
    private String psw;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * invite by
     */
    private Long inviteBy;

    /**
     * biz id
     */
    private String bizId;

    /**
     * available assets
     */
    private BigDecimal availableAssets;

    /**
     * org code
     */
    private String orgCode;

    /**
     * is delete:0no,1yes
     */
    private Integer isDel;

    /**
     * user status:0forbidden,1default
     */
    private Integer userStatus;

    /**
     * user name
     */
    private String userName;

    /**
     * address
     */
    private String address;

    /**
     * front and back pic of id card
     */
    private String idCard;

    private String bankNo;

    private Long updateBy;

    //用户类型,0:注册用户,1:DEX用户
    private Integer type;

    private String txAddress;

    private String trcAddress;

    /**
     * 头像
     */
    private String logo;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 个人简介
     */
    private String synopsis;

}

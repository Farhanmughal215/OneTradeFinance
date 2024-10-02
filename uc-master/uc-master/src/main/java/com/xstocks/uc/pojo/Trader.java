package com.xstocks.uc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * Jiao交易员表
 * </p>
 *
 * @author kevin
 * @since 2024-03-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_trader")
public class Trader implements Serializable {


    private static final long serialVersionUID = -8648549948931942016L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 0:待审核,1:通过,2:拒绝,3:封禁
     */
    @JsonIgnore
    private Integer status;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 社交媒体账号
     */
    private String socialAccount;

    /**
     * 分润比例,百分比
     */
    private Integer profitRatio;


    //合约交易证明
    private String contractCertificate;

    /**
     * 最大带单人数
     */
    private Integer maxFollowCount;

    /**
     * 地址随机数
     */
    @JsonFormat
    private Integer addressRandom;

    /**
     * 地址
     */
    @JsonFormat
    private String address;

    /**
     * 交易地址path
     */
    @JsonFormat
    private String path;

    /**
     * 可用资产
     */
    private BigDecimal assets;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}

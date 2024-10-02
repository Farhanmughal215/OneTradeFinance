package com.xstocks.uc.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Kevin
 * @date 2024/1/4 15:46
 * @apiNote userKycPo
 */
@Data
@TableName(value ="t_user_kyc")
public class UserKycPO implements Serializable {

    private static final long serialVersionUID = 3711619189540163226L;
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    //状态,0:待审核,1:审核通过,-1:审核拒绝
    private Integer state;

    private String userName;

    private String address;

    private String remark;

    private Date createTime;

    private Date updateTime;

    private Long updateBy;
}

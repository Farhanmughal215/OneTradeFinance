package com.xstocks.uc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author kevin
 * @since 2024-03-22
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_transactional_record")
public class TransactionalRecord implements Serializable {


    private static final long serialVersionUID = 7516150886219263128L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 操作类型
     */
    private String optType;

    /**
     * 操作内容
     */
    private String data;

    /**
     * 状态
     */
    private String status;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 接收时间
     */
    private Date receiveTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 备注信息
     */
    private String remark;
}

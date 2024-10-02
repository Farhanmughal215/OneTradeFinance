package com.xstocks.uc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 提现网络表
 * </p>
 *
 * @author kevin
 * @since 2024-01-15
 */
@Data
@Accessors(chain = true)
@TableName("t_withdraw_net")
public class WithdrawNet implements Serializable {


    private static final long serialVersionUID = 5761182964471005369L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String net;

    private String tokenAddress;

    private String symbol;

    private String accountAddress;
}

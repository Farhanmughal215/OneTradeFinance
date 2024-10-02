package com.xstocks.uc.pojo.vo;

import com.xstocks.uc.pojo.dto.aistock.AiStockUserBalance;
import com.xstocks.uc.pojo.dto.order.OrderUserBalance;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@ToString
public class UserVO {
    /**
     * id
     */
    private Long id;

    /**
     * phone
     */
    private String phone;

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
    private BigDecimal availableAssets = BigDecimal.ZERO;

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

    private String trcAddress;

    private String role;

    private String token;

    private BigDecimal netAssets = BigDecimal.ZERO;

    private BigDecimal totalAssets = BigDecimal.ZERO;

    private OrderUserBalance userBalance;

    private AiStockUserBalance aiStockUserBalance;
}

package com.xstocks.referral.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
public class UserReferralVo {

    /**
     * 邀请人数
     */
    private Integer referralNum;

    /**
     * 总收益
     */
    private BigDecimal totalVolume;

    /**
     * 回扣
     */
    private BigDecimal rebates;

    /**
     * 可提现金额
     */
    private BigDecimal claimableAmount;

    public UserReferralVo(Integer referralNum, BigDecimal totalVolume, BigDecimal rebates, BigDecimal claimableAmount) {
        this.referralNum = referralNum;
        this.totalVolume = totalVolume.setScale(2, RoundingMode.DOWN);
        this.rebates = rebates.setScale(2, RoundingMode.DOWN);
        this.claimableAmount = claimableAmount.setScale(4, RoundingMode.DOWN);
    }
}

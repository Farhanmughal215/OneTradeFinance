package com.xstocks.referral.pojo.vo;

import com.xstocks.referral.pojo.ReferralCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
public class ReferralCodeVo {


    /**
     * 邀请码
     */
    private String code;

    /**
     * 邀请人数
     */
    private Integer referralNum;

    /**
     * 总交易量
     */
    private BigDecimal totalVolume;

    /**
     * 总回扣
     */
    private BigDecimal totalRebates;

    public ReferralCodeVo(ReferralCode referralCode) {
        this.code = referralCode.getReferralCode();
        this.referralNum = referralCode.getReferralNum();
        this.totalVolume = referralCode.getTotalVolume().setScale(2, RoundingMode.DOWN);
        this.totalRebates = referralCode.getTotalRebates().setScale(2, RoundingMode.DOWN);
    }
}

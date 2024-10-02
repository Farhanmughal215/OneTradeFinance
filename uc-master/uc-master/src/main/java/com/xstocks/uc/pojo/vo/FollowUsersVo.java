package com.xstocks.uc.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Kevin
 * @date 2024/3/13 10:46
 * @apiNote
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowUsersVo {

    private Integer traderUid;

    private Integer followUid;

    private Integer days;

    private BigDecimal pl;

    private BigDecimal followAmount;

    public FollowUsersVo(Integer traderUid, BigDecimal pl, BigDecimal followAmount) {
        this.traderUid = traderUid;
        this.pl = pl;
        this.followAmount = followAmount;
    }
}

package com.xstocks.referral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.referral.pojo.ReferralCode;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * <p>
 * 邀请码表 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
public interface ReferralCodeMapper extends BaseMapper<ReferralCode> {

    ReferralCode queryCode(Integer userId);

    @Select("SELECT COUNT(0) FROM t_referral_relation WHERE referral_user_id = #{userId,jdbcType=INTEGER} AND create_time>=DATE_SUB(NOW(),INTERVAL 7 DAY)")
    int getSevenDaysNum(Integer userId);

    @Select("SELECT IFNULL(SUM(total_volume),0) FROM t_referral_code WHERE user_id = #{userId,jdbcType=INTEGER}")
    BigDecimal getTotalVolume(Integer userId);

    void fixReferralNum(Integer userId);
}

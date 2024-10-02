package com.xstocks.referral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.referral.pojo.ReferralUser;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 邀请用户表 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
public interface ReferralUserMapper extends BaseMapper<ReferralUser> {

    @Select("SELECT u.* FROM t_referral_relation r INNER JOIN t_referral_user u ON u.user_id=r.referral_user_id WHERE r.user_id = #{uid,jdbcType=INTEGER} LIMIT 1")
    ReferralUser getReferralUser(String uid);

    void fixUserReferralNum(Integer userId);
}

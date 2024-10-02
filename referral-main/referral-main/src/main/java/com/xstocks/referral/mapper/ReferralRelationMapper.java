package com.xstocks.referral.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xstocks.referral.pojo.ReferralRelation;
import com.xstocks.referral.pojo.param.CommonParam;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 邀请关系表 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
public interface ReferralRelationMapper extends BaseMapper<ReferralRelation> {

    List<ReferralRelation> userList(CommonParam param);

    @Select("SELECT COUNT(0) FROM t_referral_relation WHERE user_id = #{userId,jdbcType=INTEGER}")
    int getUserBindCount(Integer userId);
}

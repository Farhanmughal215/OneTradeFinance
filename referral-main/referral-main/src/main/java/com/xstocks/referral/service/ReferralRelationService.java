package com.xstocks.referral.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.referral.pojo.ReferralRelation;
import com.xstocks.referral.pojo.param.CommonParam;
import com.xstocks.referral.pojo.param.ReferralCodeParam;

import java.util.List;

/**
 * <p>
 * 邀请关系表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
public interface ReferralRelationService extends IService<ReferralRelation> {

    void bind(ReferralCodeParam param);

//    void unBind(ReferralCodeParam param);

    //查询用户关系列表
    List<ReferralRelation> userList(CommonParam param);

    ReferralRelation getReferralRelation(String uid);
}

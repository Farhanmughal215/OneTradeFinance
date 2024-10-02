package com.xstocks.referral.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.referral.pojo.ReferralUser;
import com.xstocks.referral.pojo.param.CommonParam;
import com.xstocks.referral.pojo.vo.UserReferralVo;

import java.util.LinkedHashMap;

/**
 * <p>
 * 邀请用户表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
public interface ReferralUserService extends IService<ReferralUser> {

    long checkUser(Integer uid);

    void createUser(LinkedHashMap data);

    UserReferralVo myData(CommonParam param);

    ReferralUser getReferralUser(String uid);

    ReferralUser getUserId(Integer userId);

    void fixUserReferralNum(Integer userId);

    void withdraw(CommonParam param);
}

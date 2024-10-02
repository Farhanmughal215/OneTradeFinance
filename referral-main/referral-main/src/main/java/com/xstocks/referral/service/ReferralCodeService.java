package com.xstocks.referral.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.referral.pojo.ReferralCode;
import com.xstocks.referral.pojo.param.CommonParam;
import com.xstocks.referral.pojo.param.ReferralCodeParam;
import com.xstocks.referral.pojo.vo.ReferralCodeVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 邀请码表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
public interface ReferralCodeService extends IService<ReferralCode> {

    void create(ReferralCodeParam param);

    boolean check(ReferralCodeParam param);

    ReferralCode checkBind(String code);

    List<ReferralCodeVo> codeList(CommonParam param);

    //查询邀请码
    ReferralCode queryCode(Integer userId);

    int getSevenDaysNum(Integer userId);

    BigDecimal getTotalVolume(Integer userId);

    void fixReferralNum(Integer userId);
}

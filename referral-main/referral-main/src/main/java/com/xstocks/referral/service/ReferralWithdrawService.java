package com.xstocks.referral.service;

import com.alibaba.fastjson.JSONObject;
import com.xstocks.referral.pojo.ReferralWithdraw;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 裂变提现表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-04-13
 */
public interface ReferralWithdrawService extends IService<ReferralWithdraw> {

    void callback(JSONObject object);
}

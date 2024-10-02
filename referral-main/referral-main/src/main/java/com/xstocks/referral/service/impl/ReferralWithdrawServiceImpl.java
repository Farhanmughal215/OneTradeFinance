package com.xstocks.referral.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.referral.mapper.ReferralWithdrawMapper;
import com.xstocks.referral.pojo.ReferralWithdraw;
import com.xstocks.referral.service.ReferralWithdrawService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 裂变提现表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-04-13
 */
@Service
public class ReferralWithdrawServiceImpl extends ServiceImpl<ReferralWithdrawMapper, ReferralWithdraw> implements ReferralWithdrawService {

    @Override
    public void callback(JSONObject object) {
        if (!object.containsKey("bizId"))
            return;
        if (!object.containsKey("txStatus"))
            return;
        if (!object.getBoolean("txStatus"))
            return;

        String bizId = object.getString("bizId");
        ReferralWithdraw referralWithdraw = this.getOne(Wrappers.<ReferralWithdraw>lambdaQuery().eq(ReferralWithdraw::getOrderNo, bizId));
        if (referralWithdraw == null)
            return;
        referralWithdraw.setTxHash(object.getString("txHash"));
        this.updateById(referralWithdraw);
    }
}

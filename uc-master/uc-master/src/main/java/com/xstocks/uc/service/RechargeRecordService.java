package com.xstocks.uc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.RechargeRecord;
import com.xstocks.uc.pojo.param.RechargeParam;

/**
 * <p>
 * 充值记录表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-01-09
 */
public interface RechargeRecordService extends IService<RechargeRecord> {

    String recharge(RechargeParam rechargeParam);
}

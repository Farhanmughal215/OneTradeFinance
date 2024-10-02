package com.xstocks.referral.service;

import com.xstocks.referral.pojo.RebatesRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户互返佣记录表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-04-10
 */
public interface RebatesRecordService extends IService<RebatesRecord> {

    List<RebatesRecord> getUnfreezeData();
}

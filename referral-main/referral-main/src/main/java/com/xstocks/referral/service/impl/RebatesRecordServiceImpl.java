package com.xstocks.referral.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.referral.mapper.RebatesRecordMapper;
import com.xstocks.referral.pojo.RebatesRecord;
import com.xstocks.referral.service.RebatesRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户互返佣记录表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-04-10
 */
@Service
public class RebatesRecordServiceImpl extends ServiceImpl<RebatesRecordMapper, RebatesRecord> implements RebatesRecordService {

    @Override
    public List<RebatesRecord> getUnfreezeData() {
        return this.getBaseMapper().getUnfreezeData();
    }
}

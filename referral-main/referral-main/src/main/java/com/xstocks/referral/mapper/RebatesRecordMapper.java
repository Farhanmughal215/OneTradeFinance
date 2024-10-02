package com.xstocks.referral.mapper;

import com.xstocks.referral.pojo.RebatesRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 用户互返佣记录表 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-04-10
 */
public interface RebatesRecordMapper extends BaseMapper<RebatesRecord> {

    List<RebatesRecord> getUnfreezeData();
}

package com.xstocks.uc.mapper;

import com.xstocks.uc.pojo.TraderData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 交易员数据 Mapper 接口
 * </p>
 *
 * @author kevin
 * @since 2024-03-25
 */
public interface TraderDataMapper extends BaseMapper<TraderData> {

    List<Integer> getTraderUids(List<Integer> list);
}

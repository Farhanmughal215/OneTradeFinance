package com.xstocks.uc.service.impl;

import com.xstocks.uc.pojo.TraderData;
import com.xstocks.uc.mapper.TraderDataMapper;
import com.xstocks.uc.service.TraderDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 交易员数据 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-03-25
 */
@Service
public class TraderDataServiceImpl extends ServiceImpl<TraderDataMapper, TraderData> implements TraderDataService {

    @Override
    public List<Integer> getTraderUids(List<Integer> list) {
        return this.getBaseMapper().getTraderUids(list);
    }
}

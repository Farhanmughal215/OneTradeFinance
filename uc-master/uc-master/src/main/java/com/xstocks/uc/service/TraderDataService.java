package com.xstocks.uc.service;

import com.xstocks.uc.pojo.TraderData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 交易员数据 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-03-25
 */
public interface TraderDataService extends IService<TraderData> {

    List<Integer> getTraderUids(List<Integer> list);
}

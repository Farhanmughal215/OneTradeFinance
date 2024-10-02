package com.xstocks.statistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.statistics.pojo.dto.UserPositions;
import com.xstocks.statistics.pojo.po.OrderMetadata;

/**
 * <p>
 * 用户原始订单数据 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-05-20
 */
public interface OrderMetadataService extends IService<OrderMetadata> {

    public void saveData(UserPositions positions);
}

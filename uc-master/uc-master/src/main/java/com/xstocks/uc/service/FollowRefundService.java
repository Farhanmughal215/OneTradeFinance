package com.xstocks.uc.service;

import com.xstocks.uc.pojo.FollowRefund;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 跟单退款表 服务类
 * </p>
 *
 * @author kevin
 * @since 2024-03-31
 */
public interface FollowRefundService extends IService<FollowRefund> {

    FollowRefund isRefundMessage(String txHash);
}

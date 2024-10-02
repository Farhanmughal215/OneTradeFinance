package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.mapper.FollowRefundMapper;
import com.xstocks.uc.pojo.FollowRefund;
import com.xstocks.uc.service.FollowRefundService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 跟单退款表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-03-31
 */
@Service
public class FollowRefundServiceImpl extends ServiceImpl<FollowRefundMapper, FollowRefund> implements FollowRefundService {

    @Override
    public FollowRefund isRefundMessage(String txHash) {
        return this.getOne(Wrappers.<FollowRefund>lambdaQuery().eq(FollowRefund::getTxHash, txHash).last("LIMIT 1"));
    }
}

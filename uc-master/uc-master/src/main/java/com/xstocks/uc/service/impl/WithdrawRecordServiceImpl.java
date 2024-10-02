package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.common.Constants;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.mapper.WithdrawRecordMapper;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.WithdrawRecordApplyParam;
import com.xstocks.uc.pojo.param.WithdrawRecordAuditParam;
import com.xstocks.uc.pojo.param.WithdrawRecordQueryParam;
import com.xstocks.uc.pojo.po.WithdrawRecordPO;
import com.xstocks.uc.service.UserService;
import com.xstocks.uc.service.WithdrawRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 提现记录表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-01-08
 */
@Slf4j
@Service
public class WithdrawRecordServiceImpl extends ServiceImpl<WithdrawRecordMapper, WithdrawRecordPO> implements WithdrawRecordService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<WithdrawRecordPO> queryPageUserWithDraw(WithdrawRecordQueryParam recordQueryParam) {
        Page<WithdrawRecordPO> page = new Page<>(recordQueryParam.getPageNo(), recordQueryParam.getPageSize());
        page.addOrder(OrderItem.desc("create_time"));
        LambdaQueryWrapper<WithdrawRecordPO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(WithdrawRecordPO::getUserId, recordQueryParam.getUserId());
        return page(page, wrapper);
    }

    @Override
    @Transactional
    public void apply(WithdrawRecordApplyParam applyParam) {
        String key = Constants.WITHDRAW_LOCK_KEY + applyParam.getUserId();
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "locked", 1800, TimeUnit.MILLISECONDS);
            log.info("获取提现锁结果:{}", result);
            if (!(result != null && result))
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "The request is too frequent.");

            //校验金额
            BigDecimal preReduceAmount = applyParam.getAmount().add(applyParam.getHandleFee());
            if (preReduceAmount.compareTo(userService.getBalance(applyParam.getUserId())) > 0) {
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "The balance is insufficient.");
            }

            WithdrawRecordPO recordPO = new WithdrawRecordPO();
            BeanUtils.copyProperties(applyParam, recordPO);
            recordPO.setCreateTime(new Date());
            recordPO.setState(0);
            save(recordPO);
            userService.reduceBalance(applyParam.getUserId(), preReduceAmount);
        } finally {
            redisTemplate.delete(key);
        }
    }

    @Override
    public void audit(WithdrawRecordAuditParam auditParam) {
        WithdrawRecordPO recordPO = getById(auditParam.getId());
        if (null == recordPO)
            throw new BizException(ErrorCode.ILLEGAL_REQUEST, "The withdraw record is not exist.");

        if (recordPO.getState() != 0)
            throw new BizException(ErrorCode.ILLEGAL_STATE, "The withdraw record is already audited.");
        WithdrawRecordPO po = new WithdrawRecordPO();
        po.setState(auditParam.getState());
        po.setUpdateBy(auditParam.getUserId());
        po.setUpdateTime(new Date());
        po.setId(recordPO.getId());
        po.setRemark(auditParam.getRemark());
        updateById(po);
    }
}

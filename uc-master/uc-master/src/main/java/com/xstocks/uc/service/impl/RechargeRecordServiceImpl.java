package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.common.Constants;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.mapper.RechargeRecordMapper;
import com.xstocks.uc.pojo.RechargeRecord;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.RechargeParam;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.service.RechargeRecordService;
import com.xstocks.uc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 充值记录表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-01-09
 */
@Slf4j
@Service
public class RechargeRecordServiceImpl extends ServiceImpl<RechargeRecordMapper, RechargeRecord> implements RechargeRecordService {


    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public String recharge(RechargeParam rechargeParam) {
        log.info("rechargeParam:{}", rechargeParam);
        String key = Constants.RECHARGE_LOCK_KEY + rechargeParam.getAddress();
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "locked", 1800, TimeUnit.MILLISECONDS);
            log.info("获取充值锁结果:{}", result);
            if (!(result != null && result))
                throw new BizException(ErrorCode.ILLEGAL_REQUEST, "The request is too frequent.");

            UserPO userPO = userService.getByTxAddress(rechargeParam.getAddress());
            if (userPO == null)
                return "address 不在地址库";
            RechargeRecord record = new RechargeRecord();
            record.setUserId(userPO.getId());
            record.setNet(rechargeParam.getNet());
            record.setAmount(BigDecimal.valueOf(Double.parseDouble(rechargeParam.getStrAmounts())));
            record.setTxType(rechargeParam.getTxType());
            record.setTxHash(rechargeParam.getTxHash());
            record.setAddress(rechargeParam.getAddress());
            record.setCreateTime(new Date());
            save(record);
            userService.recharge(userPO.getId(), record.getAmount());
        } finally {
            redisTemplate.delete(key);
        }
        return "address 在地址库";
    }
}

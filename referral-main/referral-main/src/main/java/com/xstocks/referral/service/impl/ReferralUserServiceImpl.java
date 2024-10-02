package com.xstocks.referral.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.referral.common.CommonConstant;
import com.xstocks.referral.common.Constants;
import com.xstocks.referral.exception.BizException;
import com.xstocks.referral.mapper.ReferralUserMapper;
import com.xstocks.referral.pojo.ReferralUser;
import com.xstocks.referral.pojo.ReferralWithdraw;
import com.xstocks.referral.pojo.enums.ErrorCode;
import com.xstocks.referral.pojo.param.CommonParam;
import com.xstocks.referral.pojo.vo.BaseResp;
import com.xstocks.referral.pojo.vo.UserReferralVo;
import com.xstocks.referral.service.ReferralUserService;
import com.xstocks.referral.service.ReferralWithdrawService;
import com.xstocks.referral.utils.IdHelper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 邀请用户表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
@Slf4j
@Service
public class ReferralUserServiceImpl extends ServiceImpl<ReferralUserMapper, ReferralUser> implements ReferralUserService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ReferralWithdrawService referralWithdrawService;

    @Value("${app.chainbridge.rpc.withdraw-url}")
    private String withdrawUrl;

    @Value("${app.chainbridge.rpc.token}")
    private String TOKEN;

    @Value("${app.chainbridge.rpc.callback-url}")
    private String callbackUrl;

    @Override
    public long checkUser(Integer uid) {
        return this.count(Wrappers.<ReferralUser>lambdaQuery().eq(ReferralUser::getUserId, uid));
    }

    @Override
    public void createUser(LinkedHashMap data) {
        log.info("createUser data:{}", data);
        //判断data 是否存在 id  和 txAddress 2个key  如果不存在抛出异常
        if (!data.containsKey("id") || !data.containsKey("txAddress")) {
            throw new BizException(ErrorCode.ILLEGAL_STATE);
        }
        ReferralUser user = new ReferralUser();
        user.setUserId(Integer.valueOf(data.get("id").toString()));
        user.setRebates(BigDecimal.ZERO);
        user.setClaimableAmount(BigDecimal.ZERO);
        user.setReferralNum(0);
        user.setTotalVolume(BigDecimal.ZERO);
        user.setAddress(data.get("txAddress").toString());
        user.setCreateTime(LocalDateTime.now());
        this.save(user);
    }

    @Override
    public UserReferralVo myData(CommonParam param) {
        //根据userId查询 用户信息
        ReferralUser user = this.getOne(Wrappers.<ReferralUser>lambdaQuery().eq(ReferralUser::getUserId, param.getUid()).last("LIMIT 1"));
        if (user == null) {
            return new UserReferralVo(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return new UserReferralVo(user.getReferralNum(), user.getTotalVolume(), user.getRebates(), user.getClaimableAmount());
    }

    @Override
    public ReferralUser getReferralUser(String uid) {
        return this.getBaseMapper().getReferralUser(uid);
    }

    @Override
    public ReferralUser getUserId(Integer userId) {
        return this.getOne(Wrappers.<ReferralUser>lambdaQuery().eq(ReferralUser::getUserId, userId).last("LIMIT 1"));
    }

    @Override
    public void fixUserReferralNum(Integer userId) {
        this.baseMapper.fixUserReferralNum(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdraw(CommonParam param) {
        //此处使用redisson分布式锁
        String lockKey = CommonConstant.WITHDRAW_LIMIT_KEY + param.getUid();
        try {
            if (!redissonClient.getLock(lockKey).tryLock(5, TimeUnit.SECONDS)) {
                throw new BizException(ErrorCode.TOO_MANY_REQUESTS);
            }
            ReferralUser user = getUserId(param.getUid());
            if (user == null) {
                throw new BizException(ErrorCode.USER_NOT_EXIST_ERROR);
            }
            if (user.getClaimableAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException(ErrorCode.NOT_ENOUGH_REBATES_ERROR);
            }
            BigDecimal amount = user.getClaimableAmount();
            user.setClaimableAmount(BigDecimal.ZERO);
            //调用提现接口
            this.updateById(user);


            ReferralWithdraw rw = new ReferralWithdraw();
            rw.setAddress(user.getAddress());
            rw.setAmount(amount);
            rw.setOrderNo(IdHelper.generateOrderId());
            rw.setNet("WOW");
            rw.setCreateTime(LocalDateTime.now());
            referralWithdrawService.save(rw);

            //调用签名服务提现
            JSONObject object = new JSONObject();
            object.put("toAddress", user.getAddress());
            object.put("amount", amount.multiply(BigDecimal.valueOf(Constants.USDT_CURRENCY_DECIMAL)).longValue());
            object.put("bizType", "referral");
            object.put("bizId", rw.getOrderNo());
            object.put("callbackUrl", callbackUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", TOKEN);
            HttpEntity<JSONObject> entity = new HttpEntity<>(object, headers);
            BaseResp<String> result = restTemplate.postForEntity(withdrawUrl, entity, BaseResp.class).getBody();
            log.info("调用上链服务参数:{}  结果:{}", object, result);



//            rabbitTemplate.convertAndSend(Constants.REFERRAL_WITHDRAW_EXCHANGE, Constants.REFERRAL_WITHDRAW_ROUTINGKEY,
//                    new UserWithdrawVo(user.getAddress(), amount,"WOW"));
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new BizException(ErrorCode.TOO_MANY_REQUESTS);
        } finally {
            redissonClient.getLock(lockKey).unlock();
        }
    }

}

package com.xstocks.referral.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.referral.common.CommonConstant;
import com.xstocks.referral.common.Constants;
import com.xstocks.referral.exception.BizException;
import com.xstocks.referral.mapper.ReferralCodeMapper;
import com.xstocks.referral.pojo.ReferralCode;
import com.xstocks.referral.pojo.ReferralUser;
import com.xstocks.referral.pojo.enums.ErrorCode;
import com.xstocks.referral.pojo.param.CommonParam;
import com.xstocks.referral.pojo.param.ReferralCodeParam;
import com.xstocks.referral.pojo.vo.BaseResp;
import com.xstocks.referral.pojo.vo.ReferralCodeVo;
import com.xstocks.referral.pojo.vo.UserContractVo;
import com.xstocks.referral.service.ReferralCodeService;
import com.xstocks.referral.service.ReferralUserService;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 邀请码表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
@Service
public class ReferralCodeServiceImpl extends ServiceImpl<ReferralCodeMapper, ReferralCode> implements ReferralCodeService {


    @Autowired
    private ReferralUserService referralUserService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.uc.rpc.get-user-url}")
    private String getUserUrl;

    @Value("${app.authorization}")
    private String authorization;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ReferralCodeParam param) {
        /*校验param的code，校验规则是数字字母下划线, 区分大小写  60个字符*/
        if (param == null || !param.getCode().matches("^[a-zA-Z0-9_]{1,60}$")) {
            throw new BizException(ErrorCode.ILLICIT_CHARACTER_ERROR);
        }

        //此处使用redisson分布式锁
        String lockKey = CommonConstant.CREATE_USER_LIMIT_KEY + param.getCode();
        if (!redissonClient.getLock(lockKey).tryLock()) {
            throw new BizException(ErrorCode.ILLEGAL_STATE);
        }
        try {
            createCode(param);
        } finally {
            redissonClient.getLock(lockKey).unlock();
        }
    }

    private void createCode(ReferralCodeParam param) {
        if (checkCode(param.getCode()))
            throw new BizException(ErrorCode.CODE_ALREADY_EXISTS_ERROR);

        if (this.count(Wrappers.<ReferralCode>lambdaQuery().eq(ReferralCode::getUserId, param.getUid())) >= 6)
            throw new BizException(ErrorCode.OVER_MAX_COUNT_ERROR);

        //判断用户是否存在
        if (referralUserService.checkUser(param.getUid()) == 0) {
            //创建用户
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", authorization);
            HttpEntity<JSONObject> httpEntity = new HttpEntity<>(null, headers);
            BaseResp<LinkedHashMap> resp = restTemplate.postForEntity(getUserUrl + param.getUid(), httpEntity, BaseResp.class).getBody();
            if (resp == null || resp.getCode() != 0 || resp.getData() == null)
                throw new BizException(ErrorCode.ILLEGAL_STATE);
            referralUserService.createUser(resp.getData());
        }

        ReferralCode code = new ReferralCode();
        code.setUserId(param.getUid());
        code.setReferralCode(param.getCode());
        code.setReferralNum(0);
        code.setTotalVolume(BigDecimal.ZERO);
        code.setCreateTime(LocalDateTime.now());
        this.save(code);

        ReferralUser user = referralUserService.getUserId(code.getUserId());
        if (user == null)
            throw new BizException(ErrorCode.ILLEGAL_STATE);

        rabbitTemplate.convertAndSend(Constants.REFERRAL_CONTRACT_EXCHANGE, Constants.REFERRAL_CONTRACT_ROUTINGKEY,
                new UserContractVo("addInviteCode", Arrays.asList(user.getAddress(), param.getCode())));
    }

    @Override
    public boolean check(ReferralCodeParam param) {
        return checkCode(param.getCode());
    }

    @Override
    public ReferralCode checkBind(String code) {
        ReferralCode referralCode = this.getOne(Wrappers.<ReferralCode>lambdaQuery()
                .eq(ReferralCode::getReferralCode, code).last("LIMIT 1"));
        if (null == referralCode)
            throw new BizException(ErrorCode.CODE_NOT_EXISTS_ERROR);
        return referralCode;
    }

    @Override
    public List<ReferralCodeVo> codeList(CommonParam param) {
        List<ReferralCode> list = this.list(Wrappers.<ReferralCode>lambdaQuery().eq(ReferralCode::getUserId, param.getUid()).orderByDesc(ReferralCode::getId));
        if (CollectionUtils.isEmpty(list))
            return Collections.emptyList();
        return list.stream().map(ReferralCodeVo::new).collect(Collectors.toList());
    }

    //true 则已存在
    private boolean checkCode(String code) {
        return this.count(Wrappers.<ReferralCode>lambdaQuery().eq(ReferralCode::getReferralCode, code)) > 0;
    }

    //查询邀请码
    @Override
    public ReferralCode queryCode(Integer userId) {
        return this.getBaseMapper().queryCode(userId);
    }

    @Override
    public int getSevenDaysNum(Integer userId) {
        return this.getBaseMapper().getSevenDaysNum(userId);
    }

    @Override
    public BigDecimal getTotalVolume(Integer userId) {
        return this.getBaseMapper().getTotalVolume(userId);
    }


    @Override
    public void fixReferralNum(Integer userId) {
        this.getBaseMapper().fixReferralNum(userId);
    }
}

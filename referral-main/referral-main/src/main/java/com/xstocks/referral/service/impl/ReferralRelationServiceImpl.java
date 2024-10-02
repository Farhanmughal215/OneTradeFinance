package com.xstocks.referral.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.referral.common.Constants;
import com.xstocks.referral.exception.BizException;
import com.xstocks.referral.mapper.ReferralRelationMapper;
import com.xstocks.referral.pojo.ReferralCode;
import com.xstocks.referral.pojo.ReferralRelation;
import com.xstocks.referral.pojo.enums.ErrorCode;
import com.xstocks.referral.pojo.param.CommonParam;
import com.xstocks.referral.pojo.param.ReferralCodeParam;
import com.xstocks.referral.pojo.vo.BaseResp;
import com.xstocks.referral.pojo.vo.UserContractVo;
import com.xstocks.referral.service.ReferralCodeService;
import com.xstocks.referral.service.ReferralRelationService;
import com.xstocks.referral.service.ReferralUserService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * 邀请关系表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-04-06
 */
@Slf4j
@Service
public class ReferralRelationServiceImpl extends ServiceImpl<ReferralRelationMapper, ReferralRelation> implements ReferralRelationService {

    @Autowired
    private ReferralCodeService referralCodeService;

    @Autowired
    private ReferralUserService referralUserService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.uc.rpc.get-user-url}")
    private String getUserUrl;

    @Value("${app.authorization}")
    private String authorization;

    @Override
    @Transactional
    public void bind(ReferralCodeParam param) {
        ReferralCode code = referralCodeService.checkBind(param.getCode());
        //不能绑定自己的邀请码
        if (code.getUserId().equals(param.getUid()))
            throw new BizException(ErrorCode.CAN_NOT_BIND_OWNER_ERROR);

        //判断是否已绑定该用户
        if (count(Wrappers.<ReferralRelation>lambdaQuery().eq(ReferralRelation::getReferralId, code.getId())
                .eq(ReferralRelation::getUserId, param.getUid())) > 0)
            return;


        //先解绑
        unBind(param.getUid(), code.getReferralCode());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorization);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(null, headers);
        BaseResp<LinkedHashMap> resp = restTemplate.postForEntity(getUserUrl + param.getUid(), httpEntity, BaseResp.class).getBody();
        if (resp == null || resp.getCode() != 0 || resp.getData() == null)
            throw new BizException(ErrorCode.ILLEGAL_STATE);
        if (!resp.getData().containsKey("logo") || !resp.getData().containsKey("txAddress")) {
            throw new BizException(ErrorCode.ILLEGAL_STATE);
        }
        log.info("bind user info:{}", resp.getData());

        ReferralRelation relation = new ReferralRelation();
        relation.setReferralId(code.getId());
        relation.setReferralUserId(code.getUserId());
        relation.setUserId(param.getUid());
        relation.setTotalRebates(BigDecimal.ZERO);
        relation.setUserAddress(resp.getData().get("txAddress").toString());
        relation.setUserLogo(null == resp.getData().get("logo") ? null : resp.getData().get("logo").toString());
        relation.setCreateTime(LocalDateTime.now());
        save(relation);
        //邀请人数+1
        code.setReferralNum(code.getReferralNum() + 1);
        referralCodeService.updateById(code);

        rabbitTemplate.convertAndSend(Constants.REFERRAL_CONTRACT_EXCHANGE, Constants.REFERRAL_CONTRACT_ROUTINGKEY,
                new UserContractVo("addInvitation", Arrays.asList(code.getReferralCode(), relation.getUserAddress())));

        //更新用户邀请人数
        referralUserService.fixUserReferralNum(code.getUserId());
        referralCodeService.fixReferralNum(code.getUserId());
    }

    private void unBind(Integer userId, String referralCode) {
        List<ReferralRelation> list = list(Wrappers.<ReferralRelation>lambdaQuery().eq(ReferralRelation::getUserId, userId));
        if (CollectionUtils.isEmpty(list))
            return;

        for (ReferralRelation relation : list) {
            //解绑
            removeById(relation.getId());
            rabbitTemplate.convertAndSend(Constants.REFERRAL_CONTRACT_EXCHANGE, Constants.REFERRAL_CONTRACT_ROUTINGKEY,
                    new UserContractVo("removeInvitation", Arrays.asList(referralCode, relation.getUserAddress())));
        }
    }

    @Override
    //查询用户关系列表
    public List<ReferralRelation> userList(CommonParam param) {
        return getBaseMapper().userList(param);
    }

    @Override
    public ReferralRelation getReferralRelation(String uid) {
        return getOne(Wrappers.<ReferralRelation>lambdaQuery().eq(ReferralRelation::getUserId, uid).last("limit 1"));
    }
}
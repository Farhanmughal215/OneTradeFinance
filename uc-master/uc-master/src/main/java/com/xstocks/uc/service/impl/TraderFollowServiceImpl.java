package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.common.Constants;
import com.xstocks.uc.exception.BizException;
import com.xstocks.uc.mapper.TraderFollowMapper;
import com.xstocks.uc.pojo.Trader;
import com.xstocks.uc.pojo.TraderFollow;
import com.xstocks.uc.pojo.enums.ErrorCode;
import com.xstocks.uc.pojo.param.FollowTraderParam;
import com.xstocks.uc.pojo.param.TraderFollowParam;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.*;
import com.xstocks.uc.service.TraderFollowService;
import com.xstocks.uc.service.TraderService;
import com.xstocks.uc.service.UserService;
import com.xstocks.uc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 交易员带单用户表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2024-03-08
 */
@Slf4j
@Service
public class TraderFollowServiceImpl extends ServiceImpl<TraderFollowMapper, TraderFollow> implements TraderFollowService {

    @Autowired
    private TraderService traderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app.analysis.rpc.trader-profit-url}")
    private String traderProfitUrl;

    @Value("${app.analysis.rpc.follow-users-profit-url}")
    private String followUsersProfitUrl;

    @Value("${app.follow.rpc.opening-order-url}")
    private String openingOrderUrl;

    @Value("${app.analysis.rpc.followorno-url}")
    private String followOrNoUrl;

    @Value("${app.analysis.rpc.freeze-balance-url}")
    private String freezeBalanceUrl;

    @Override
    public void follow(FollowTraderParam followTraderParam, Long id) {
        if (null == followTraderParam || null == followTraderParam.getTraderId())
            throw new BizException(ErrorCode.PARAM_ERROR);

        Trader trader = traderService.getByUserId(Long.parseLong(followTraderParam.getTraderId().toString()));
        if (Objects.isNull(trader)) {
            throw new BizException(ErrorCode.TRADER_NOT_EXIST_ERROR);
        }
        if (trader.getStatus() != 1) {
            throw new BizException(ErrorCode.ILLEGAL_STATE);
        }

        //判断是否满员

        TraderFollow tf = this.getOne(Wrappers.<TraderFollow>lambdaQuery()
                .eq(TraderFollow::getTraderId, trader.getId()).eq(TraderFollow::getUserId, id).last("LIMIT 1"));

        TraderFollow traderFollow = new TraderFollow();
        traderFollow.setTraderId(trader.getId());
        traderFollow.setUserId(id);
        traderFollow.setMaxAmount(followTraderParam.getMaxAmount());
        traderFollow.setAmount(followTraderParam.getAmount());
        traderFollow.setType(followTraderParam.getType());

        if (Objects.isNull(tf)) {
            notifyUnFollow(trader.getUserId(), 1);
            traderFollow.setCreateTime(new Date());
            this.save(traderFollow);
            return;
        }
        traderFollow.setId(tf.getId());
        this.updateById(traderFollow);
    }

    @Override
    public List<FollowUserVo> getFollowList(Integer userId) {
        return this.getBaseMapper().getFollowList(userId);
    }

    @Override
    public List<TraderDataVo> getMyTraders(Long id) {
        List<TraderDataVo> list = baseMapper.getMyTraders(id);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        ObjectNode node = JsonUtil.createObjectNode();
        node.put("uid", id);
        node.put("traderUid", list.stream().map(TraderDataVo::getId).map(String::valueOf).collect(Collectors.joining(",")));
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
        BaseResp<List<LinkedHashMap>> resp = restTemplate.postForEntity(traderProfitUrl, httpEntity, BaseResp.class).getBody();

        log.info("用户查询跟单的交易员参数:{}   返回结果:{}", JsonUtil.toJSONString(node), JsonUtil.toJSONString(resp));
        if (resp == null || resp.getCode() != 0) {
            return list;
        }
        List<FollowUsersVo> data = resp.getData().stream().map(map -> new FollowUsersVo(Integer.parseInt(map.get("traderUid").toString()),
                BigDecimal.valueOf((Double) map.get("pl")), BigDecimal.valueOf((Double) map.get("followAmount")))).collect(Collectors.toList());

        for (TraderDataVo dataVo : list) {
            Optional<FollowUsersVo> q = data.stream().filter(vo -> vo.getTraderUid().equals(dataVo.getId())).findFirst();
            if (q.isPresent()) {
                dataVo.setProfitAmount(q.get().getPl());
                dataVo.setTotalAmount(q.get().getFollowAmount());
            }
        }
        return list;
    }

    @Override
    public List<TraderDataVo> follows(TraderFollowParam param, Long userId) {
        Long traderUid = param == null || param.getTraderUid() == null ? null : param.getTraderUid();
        if (traderUid == null) {
            Trader trader = traderService.getByUserId(userId);
            if (Objects.isNull(trader))
                throw new BizException(ErrorCode.TRADER_NOT_EXIST_ERROR);
            traderUid = trader.getUserId();
        }
        List<TraderDataVo> list = baseMapper.follows(traderUid);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        ObjectNode node = JsonUtil.createObjectNode();
        node.put("traderUid", traderUid);
        node.put("uid", userId);
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
        BaseResp<List<LinkedHashMap>> resp = restTemplate.postForEntity(followUsersProfitUrl, httpEntity, BaseResp.class).getBody();

        log.info("followUsersProfitUrl resp:{}", JsonUtil.toJSONString(resp));
        if (resp == null || resp.getCode() != 0) {
            return list;
        }

        List<FollowUsersVo> data = resp.getData().stream().map(map -> new FollowUsersVo(Integer.parseInt(map.get("traderUid").toString()), Integer.parseInt(map.get("followUid").toString()), Integer.parseInt(map.get("days").toString()), BigDecimal.valueOf((Double) map.get("pl")),
                BigDecimal.valueOf((Double) map.get("followAmount")))).collect(Collectors.toList());

        for (TraderDataVo dataVo : list) {
            Optional<FollowUsersVo> q = data.stream().filter(vo -> vo.getFollowUid().equals(dataVo.getId())).findFirst();
            if (q.isPresent()) {
                dataVo.setDays(q.get().getDays());
                dataVo.setProfitAmount(q.get().getPl());
                dataVo.setTotalAmount(q.get().getFollowAmount());
            }
        }
        return list.stream().sorted(Comparator.comparing(TraderDataVo::getProfitAmount).reversed()).collect(Collectors.toList());
    }

    @Override
    public void unFollow(TraderFollowParam unFollowTraderParam, Long userId) {
        //校验是否有持仓的订单
        try {
            ObjectNode node = JsonUtil.createObjectNode();
            node.put("traderUid", unFollowTraderParam.getTraderUid());
            node.put("followUid", userId);
            node.put("positionStatus", "opening");
            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
            BaseResp<List<String>> resp = restTemplate.postForEntity(openingOrderUrl, httpEntity, BaseResp.class).getBody();
            if (resp == null || resp.getCode() != 0) {
                throw new BizException(ErrorCode.DEFAULT_ERROR);
            }
            log.info("用户取消跟单查询持仓参数:{}    结果:{}", JsonUtil.toJSONString(node), JsonUtil.toJSONString(resp));
            if (CollectionUtils.isNotEmpty(resp.getData()) && resp.getData().size() > 0) {
                throw new BizException(ErrorCode.HAVE_OPENING_ORDER_ERROR);
            }
        } catch (BizException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException(ErrorCode.DEFAULT_ERROR);
        }

        Trader trader = traderService.getByUserId(unFollowTraderParam.getTraderUid());
        if (Objects.isNull(trader))
            throw new BizException(ErrorCode.TRADER_NOT_EXIST_ERROR);

        UserPO userPO = userService.getById(userId);
        if (userPO == null)
            throw new BizException(ErrorCode.USER_NOT_FOUND_ERROR);

        /**
         * 1.查询余额
         * 2.调用跨链桥退款
         * 3.修改
         */

        /*
        {
            "uid":"154",//跟单员id
            "traderUid":"59"//交易员id
        }
         */
        ObjectNode node = JsonUtil.createObjectNode();
        node.put("traderUid", unFollowTraderParam.getTraderUid());
        node.put("uid", userId);
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
        BaseResp<Number> resp = restTemplate.postForEntity(freezeBalanceUrl, httpEntity, BaseResp.class).getBody();
        log.info("冻结用户金额参数:{}   结果:{}", JsonUtil.toJSONString(node), JsonUtil.toJSONString(resp));
        if (resp == null || resp.getCode() != 0) {
            throw new BizException(ErrorCode.DEFAULT_ERROR);
        }
        Number amount = resp.getData();
        Double amountD = amount.doubleValue();
        if (amountD > 0) {
            rabbitTemplate.convertAndSend(Constants.REFUND_EXCHANGE, Constants.REFUND_ROUTING_KEY,
                    new UserRefundVo(userId.intValue(), userPO.getTxAddress(), trader.getId()
                            , trader.getUserId().intValue(), BigDecimal.valueOf(amountD)));
        }


        notifyUnFollow(unFollowTraderParam.getTraderUid(), 2);

        this.remove(Wrappers.<TraderFollow>lambdaQuery()
                .eq(TraderFollow::getTraderId, trader.getId())
                .eq(TraderFollow::getUserId, userId));
    }


    private void notifyUnFollow(Long traderUid, int type) {
        ObjectNode node = JsonUtil.createObjectNode();
        node.put("traderUid", traderUid);
        node.put("type", type);
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
        BaseResp<List<String>> resp = restTemplate.postForEntity(followOrNoUrl, httpEntity, BaseResp.class).getBody();
        log.info("跟单/取消跟单参数:{}   结果:{}", JsonUtil.toJSONString(node), JsonUtil.toJSONString(resp));
        if (resp == null || resp.getCode() != 0) {
            throw new BizException(ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Trader trader = traderService.getByUserId(id);
        if (Objects.isNull(trader))
            throw new BizException(ErrorCode.TRADER_NOT_EXIST_ERROR);

        ObjectNode node = JsonUtil.createObjectNode();
        node.put("traderUid", trader.getId());
        node.put("positionStatus ", "opening");
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
        BaseResp<List<String>> resp = restTemplate.postForEntity(openingOrderUrl, httpEntity, BaseResp.class).getBody();
        if (resp == null || resp.getCode() != 0) {
            throw new BizException(ErrorCode.DEFAULT_ERROR);
        }
        if (CollectionUtils.isNotEmpty(resp.getData())) {
            throw new BizException(ErrorCode.HAVE_OPENING_ORDER_ERROR);
        }

        this.remove(Wrappers.<TraderFollow>lambdaQuery()
                .eq(TraderFollow::getTraderId, trader.getId()));
        traderService.removeById(trader.getId());
    }

    @Override
    public TraderFollow getByUid(Integer traderId, Integer userId) {
        return this.getBaseMapper().getByUid(traderId, userId);
    }
}

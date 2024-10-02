package com.xstocks.referral.listener;

import com.alibaba.fastjson.JSON;
import com.xstocks.referral.common.Constants;
import com.xstocks.referral.pojo.*;
import com.xstocks.referral.pojo.dto.UserPositions;
import com.xstocks.referral.pojo.vo.BaseResp;
import com.xstocks.referral.pojo.vo.UserContractVo;
import com.xstocks.referral.service.*;
import com.xstocks.referral.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ReferralOrderFeeListener {

    @Autowired
    private ReferralOrderService referralOrderService;

    @Autowired
    private ReferralUserService referralUserService;

    @Autowired
    private ReferralRelationService referralRelationService;

    @Autowired
    private ReferralCodeService referralCodeService;

    @Autowired
    private RebatesRecordService rebatesRecordService;

    @Value("${app.chainbridge.rpc.referral-write-url}")
    private String referralWriteUrl;

    @Autowired
    private RestTemplate restTemplate;

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory", bindings = @QueueBinding(
            value = @Queue(value = Constants.REFERRAL_CONTRACT_QUEUE, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = Constants.REFERRAL_CONTRACT_EXCHANGE, type = ExchangeTypes.FANOUT),
            key = Constants.REFERRAL_CONTRACT_ROUTINGKEY))
    public void receiveContractMessage(UserContractVo contractVo) {
        try {
            TimeUnit.SECONDS.sleep(4);
            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UserContractVo> httpEntity = new HttpEntity<>(contractVo, reqHeaders);
            BaseResp<LinkedHashMap> resp = restTemplate.postForEntity(referralWriteUrl, httpEntity, BaseResp.class).getBody();
            log.info("跨链桥邀请码操作参数:{}  结果:{}", contractVo.toString(), resp);
        } catch (InterruptedException e) {
            log.error("休眠4秒防止并发问题 ,发生异常:{}", e.getMessage());
        }
    }
/*

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory", bindings = @QueueBinding(
            value = @Queue(value = Constants.REFERRAL_WITHDRAW_QUEUE, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = Constants.REFERRAL_WITHDRAW_EXCHANGE, type = ExchangeTypes.FANOUT),
            key = Constants.REFERRAL_WITHDRAW_ROUTINGKEY))
    public void receiveWithdrawMessage(UserWithdrawVo withdrawVo) {
        try {
            TimeUnit.SECONDS.sleep(4);
            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<UserWithdrawVo> httpEntity = new HttpEntity<>(withdrawVo, reqHeaders);
            BaseResp<LinkedHashMap> resp = restTemplate.postForEntity(withdrawUrl, httpEntity, BaseResp.class).getBody();
            log.info("跨链桥提现参数:{}  结果:{}", withdrawVo.toString(), resp);
            ReferralWithdraw rw = new ReferralWithdraw();
            rw.setAddress(withdrawVo.getAddress());
            rw.setAmount(withdrawVo.getAmount());
            rw.setNet(withdrawVo.getNet());
            rw.setCreateTime(LocalDateTime.now());
            if (null != resp && resp.getData() != null && resp.getData().containsKey("hash"))
                rw.setTxHash(resp.getData().getOrDefault("hash", "").toString());
            referralWithdrawService.save(rw);
        } catch (InterruptedException e) {
            log.error("休眠4秒防止并发问题 ,发生异常:{}", e.getMessage());
        }
    }

*/

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory", bindings = @QueueBinding(
            value = @Queue(value = "referral-queue", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "dex.topic.order", type = ExchangeTypes.TOPIC)))
    public void receiveFanoutMessage(String message) {
        try {
            log.info("OrderFeeListener receive message: {}", message);
            UserPositions positions = JSON.parseObject(message, UserPositions.class);

            if (positions == null || positions.getFee() == null)
                return;

            //更新用户返佣
            ReferralRelation relation = referralRelationService.getReferralRelation(positions.getUid());
            if (relation == null) {
                log.info("用户没有邀请关系,放弃");
                return;
            }

            ReferralCode code = referralCodeService.getById(relation.getReferralId());
            if (code == null) {
                log.info("用户没有绑定邀请码");
                return;
            }

            //计算本次市值
            //把String类型的sourceSize转成BigDecimal类型
            BigDecimal volume = positions.getSourcePrice().multiply(new BigDecimal(positions.getSourceSize()));

            //计算返佣比例
            //计算是7天内邀请人数
            BigDecimal rate = new BigDecimal("0.05");
            int sevenDaysNum = referralCodeService.getSevenDaysNum(code.getUserId());

            BigDecimal totalVolume = referralCodeService.getTotalVolume(code.getUserId());
            totalVolume = totalVolume.add(volume);
            if (sevenDaysNum >= 3 && totalVolume.compareTo(new BigDecimal("100000")) >= 0) {
                rate = new BigDecimal("0.1");
            }
            if (sevenDaysNum >= 5 && totalVolume.compareTo(new BigDecimal("250000")) >= 0) {
                rate = new BigDecimal("0.15");
            }

            //计算本次回扣金额
            BigDecimal amount = positions.getFee().multiply(rate);

            log.info("本次订单手续费:{}  返佣比例:{}  返佣金额:{}   总交易量:{}",
                    positions.getFee(), rate, amount, totalVolume);
            ReferralCode rc = new ReferralCode();
            rc.setId(code.getId());
            rc.setTotalRebates(code.getTotalRebates().add(amount));
            rc.setTotalVolume(code.getTotalVolume().add(volume));
            referralCodeService.updateById(rc);

            relation.setTotalRebates(relation.getTotalRebates().add(amount));
            referralRelationService.updateById(relation);

            ReferralUser user = referralUserService.getUserId(code.getUserId());
            if (user != null) {
                user.setRebates(user.getRebates().add(amount));
                user.setTotalVolume(user.getTotalVolume().add(volume));
                user.setUpdateTime(LocalDateTime.now());
                referralUserService.updateById(user);
            }

            ReferralOrder order = new ReferralOrder();
            order.setAddress(positions.getAddress());
            order.setOrderTime(DateUtils.parseUnixTimeToLocalDateTime(positions.getDoneTs()));
            order.setOrderId(String.valueOf(positions.getOrderId()));
            order.setFee(positions.getFee());
            order.setRate(rate);
            order.setAmount(amount);
            referralOrderService.save(order);

            RebatesRecord record = new RebatesRecord();
            record.setUserId(code.getUserId());
            record.setRebates(amount);
            record.setCreateTime(LocalDateTime.now());
            record.setStatus(0);
            rebatesRecordService.save(record);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}

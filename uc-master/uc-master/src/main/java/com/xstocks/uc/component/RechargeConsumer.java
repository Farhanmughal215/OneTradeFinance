package com.xstocks.uc.component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.pojo.*;
import com.xstocks.uc.pojo.enums.TransactionalEnum;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.pojo.vo.UserRechargeVo;
import com.xstocks.uc.service.*;
import com.xstocks.uc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.util.Date;

/**
 * @author Kevin
 * @date 2024/3/20 00:20
 * @apiNote
 */
@Slf4j
@Component
public class RechargeConsumer {

    @Autowired
    private TradeRecordService tradeRecordService;

    @Autowired
    private TraderService traderService;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowRefundService followRefundService;

    @Autowired
    private TraderFollowService traderFollowService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.analysis.rpc.recharge-url}")
    private String rechargeUrl;

    @Value("${app.analysis.rpc.update-freeze-url}")
    private String updateFreezeUrl;

    @Autowired
    private TransactionalRecordService transactionalRecordService;

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory", bindings = @QueueBinding(
            value = @Queue(value = "recharge-queue", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "dex.topic.usdt.recharge", type = ExchangeTypes.TOPIC)))
    public void receiveFanoutMessage(String message) {
        /**
         * {
         *   "fromAddress": "from", 发起转账地址
         *   "toAddress": "to", 接收地址
         *   "amount": "weiAmount", 金额十进制带小数点，字符串
         *   "txHash": "hash", 交易hash
         *   "blockNumber": "blockNumber", 区块好
         *   "tokenContract": "tokenContract" 合约地址
         * }
         */
        log.info("receiveFanoutMessage:{}", message);
        if (StringUtils.isEmpty(message)) {
            return;
        }
        try {
            UserRechargeVo rechargeVo = JsonUtil.parseObject(message, UserRechargeVo.class);
            if (rechargeVo == null) {
                log.error("Cannot deserialize value UserRechargeVo");
                return;
            }
            //判断是否是退款消息
            FollowRefund fr = followRefundService.isRefundMessage(rechargeVo.getTxHash());
            if (null != fr) {
                log.info("查询到是退款消息,开始处理");
                //判断交易状态和退款记录状态
                if (rechargeVo.getStatus() == 1 && fr.getStatus() == 0) {
                    //修改退款记录状态为成功
                    FollowRefund refund = new FollowRefund();
                    refund.setId(fr.getId());
                    refund.setStatus(1);
                    followRefundService.updateById(refund);
                }
                ObjectNode node = JsonUtil.createObjectNode();
                node.put("uid", fr.getUserId());
                node.put("traderUid", fr.getTraderUid());
                node.put("success", rechargeVo.getStatus() == 1 ? 1 : 0);
                HttpHeaders reqHeaders = new HttpHeaders();
                reqHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
                BaseResp resp = restTemplate.postForEntity(updateFreezeUrl, httpEntity, BaseResp.class).getBody();
                if (resp == null || resp.getCode() != 0) {
                    log.error("用户退款余额结算处理失败");
                    TransactionalRecord tr = new TransactionalRecord();
                    tr.setBusinessId(rechargeVo.getTxHash());
                    tr.setOptType("USER_REFUND");
                    tr.setData(JsonUtil.toJSONString(rechargeVo));
                    tr.setSendTime(new Date());
                    tr.setStatus(TransactionalEnum.FAILED.name());
                    tr.setRemark("用户退款余额结算处理失败");
                    tr.setErrorMessage(null != resp ? resp.getMessage() : null);
                    transactionalRecordService.save(tr);
                }
                log.info("退款处理完成,通知清结算 参数:{}   结果:{}", JsonUtil.toJSONString(node), JsonUtil.toJSONString(resp));
                return;
            }

            Trader trader = traderService.getTraderByAddress(rechargeVo.getToAddress());

            //此处只管充值消息
            //如果不是转给交易员账户的金额则不管
            if (trader == null) {
                log.info("找不到交易员,放弃");
                return;
            }
            UserPO userPO = userService.getByTxAddress(rechargeVo.getFromAddress());
            //如果查不到用户也不管
            if (userPO == null) {
                log.info("找不到用户,放弃");
                return;
            }

            TraderFollow tf = traderFollowService.getByUid(trader.getId(), userPO.getId().intValue());
            if (tf == null) {
                log.info("找不到关注记录,放弃");
                return;
            }
            if (tf.getStatus() == 0) {
                TraderFollow newTf = new TraderFollow();
                newTf.setId(tf.getId());
                newTf.setStatus(1);
                traderFollowService.updateById(newTf);
            }

            TradeRecord record = new TradeRecord();
            record.setCurrency("USDT");
            record.setType(0);  //此处是转给交易员的，都是到资金账户
            record.setFromAddress(rechargeVo.getFromAddress());
            record.setToAddress(rechargeVo.getToAddress());
            record.setUserId(userPO.getId());
            record.setAmount(rechargeVo.getAmount());
            record.setTxHash(rechargeVo.getTxHash());
            record.setCreateTime(new Date());
            tradeRecordService.save(record);

            //给交易员账户增加余额
            traderService.addBalance(trader.getId(), rechargeVo.getAmount());


            //通知清结算
            ObjectNode node = JsonUtil.createObjectNode();
            node.put("userId", userPO.getId());
            node.put("rechargeAmount", rechargeVo.getAmount());
            node.put("traderUid", trader.getUserId());
            node.put("orderId", rechargeVo.getTxHash());
            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
            BaseResp resp = restTemplate.postForEntity(rechargeUrl, httpEntity, BaseResp.class).getBody();
            log.info("通知清结算参数:{}   结果:{}", JsonUtil.toJSONString(node), JsonUtil.toJSONString(resp));
            if (resp == null || resp.getCode() != 0) {
                log.error("通知清结算失败");
                TransactionalRecord tr = new TransactionalRecord();
                tr.setBusinessId(rechargeVo.getTxHash());
                tr.setOptType("RECHARGE");
                tr.setData(JsonUtil.toJSONString(rechargeVo));
                tr.setSendTime(new Date());
                tr.setStatus(TransactionalEnum.FAILED.name());
                tr.setRemark("充值通知清结算失败");
                tr.setErrorMessage(null != resp ? resp.getMessage() : null);
                transactionalRecordService.save(tr);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

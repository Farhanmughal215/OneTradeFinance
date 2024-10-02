package com.xstocks.uc.component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xstocks.uc.common.Constants;
import com.xstocks.uc.pojo.FollowRefund;
import com.xstocks.uc.pojo.vo.BaseResp;
import com.xstocks.uc.pojo.vo.UserRefundVo;
import com.xstocks.uc.service.FollowRefundService;
import com.xstocks.uc.utils.JsonUtil;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Kevin
 * @date 2024/3/20 00:20
 * @apiNote
 */
@Slf4j
@Component
public class RefundConsumer {

    @Autowired
    private FollowRefundService followRefundService;

    @Autowired
    private RestTemplate restTemplate;


    @Value("${app.chainbridge.rpc.refund-url}")
    private String refundUrl;


    @RabbitListener(containerFactory = "rabbitListenerContainerFactory", bindings = @QueueBinding(
            value = @Queue(value = Constants.REFUND_QUEUE, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = Constants.REFUND_EXCHANGE, type = ExchangeTypes.FANOUT),
            key = Constants.REFUND_ROUTING_KEY))
    public void receiveRefundMessage(UserRefundVo refundVo) {
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            log.error("休眠4秒防止并发问题 ,发生异常  继续,{}", e.getMessage());
        }
        /**
         * {
         *   "userId": "userId", 发起转账地址
         *   "traderId": "traderId", 接收地址
         *   "traderUid": "weiAmount", 金额十进制带小数点，字符串
         *   "amount": "hash", 交易hash
         * }
         */
        log.info("用户退款消息:{}", refundVo);
//        if (StringUtils.isEmpty(message)) {
//            return;
//        }
        try {
//            UserRefundVo refundVo = JsonUtil.parseObject(message, UserRefundVo.class);
            if (refundVo == null) {
                log.error("消费用户退款数据异常,UserRefundVo is null");
                return;
            }
            //如果amount 小于等于0 则不做处理
            if (refundVo.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                log.error("消费用户退款数据异常,amount <= 0");
                return;
            }

            ObjectNode node = JsonUtil.createObjectNode();
            node.put("address", refundVo.getAddress());
            node.put("net", "WOW");
            node.put("amount", refundVo.getAmount());
            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(node, reqHeaders);
            BaseResp<LinkedHashMap> resp = restTemplate.postForEntity(refundUrl, httpEntity, BaseResp.class).getBody();
            log.info("跟单/取消跟单参数:{}   结果:{}", JsonUtil.toJSONString(node), JsonUtil.toJSONString(resp));
            if (resp == null) {
                log.error("退款mq消费失败:resp is null");
                return;
            }
            if (resp.getCode() != 0) {
                log.error("退款mq消费失败,code not 0,resp:{}", JsonUtil.toJSONString(resp));
                return;
            }

            //写入交易记录
            FollowRefund refund = new FollowRefund();
            refund.setTraderId(refundVo.getTraderId());
            refund.setUserId(refundVo.getUserId());
            refund.setAmount(refundVo.getAmount());
            refund.setStatus(0);
            refund.setUserAddress(refundVo.getAddress());
            refund.setTxHash(resp.getData().getOrDefault("hash", "").toString());
            refund.setCreateTime(new Date());
            refund.setTraderUid(refundVo.getTraderUid());
            followRefundService.save(refund);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

package com.xstocks.statistics.listener;


import com.alibaba.fastjson.JSON;
import com.xstocks.statistics.common.CommonConstant;
import com.xstocks.statistics.pojo.dto.UserPositions;
import com.xstocks.statistics.service.OrderMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class ExchangeAndQueueService {

    @Autowired
    private OrderMetadataService metadataService;

    @Autowired
    private RedissonClient redissonClient;

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory", bindings = @QueueBinding(
            value = @Queue(value = "opening-queue", durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = "dex.topic.order", type = ExchangeTypes.TOPIC)))
    public void receiveFanoutMessage(String message) {


        log.info("建仓or平仓消息: {}", message);
        UserPositions positions = JSON.parseObject(message, UserPositions.class);

        if (positions == null)
            return;

        //判断是否重复消费
        RBucket<Object> bucket = redissonClient.getBucket(CommonConstant.CONSUMED_KEY + positions.getOrderId() + "_" + positions.getPositionStatus());
        if (!bucket.setIfAbsent(1, Duration.ofHours(1))) {
            return;
        }

        //保存到数据库
        metadataService.saveData(positions);
    }
}

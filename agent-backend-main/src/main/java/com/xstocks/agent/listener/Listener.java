package com.xstocks.agent.listener;

import com.xstocks.agent.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Slf4j
//@Component
public class Listener {


    @RabbitListener(containerFactory = "rabbitListenerContainerFactory", bindings = @QueueBinding(
            value = @Queue(value = Constants.QUEUE, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = Constants.EXCHANGE, type = ExchangeTypes.FANOUT),
            key = Constants.ROUTINGKEY))
    public void receiveContractMessage(String message) {

    }
}

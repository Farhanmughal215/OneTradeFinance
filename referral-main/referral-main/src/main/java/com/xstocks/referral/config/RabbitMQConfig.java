package com.xstocks.referral.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2MessageConverter());
        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        //rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback(
                (correlationData, ack, cause) -> log.warn("ConfirmCallback:相关数据 {},确认情况 {},原因 {}",
                        correlationData, ack, cause));

        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.warn("ReturnCallback:消息 {},回应码 {}, 回应信息 {}, 交换机 {}, 路由键 {}", message, replyCode,
                    replyText, exchange, routingKey);
        });

        return rabbitTemplate;
    }
}

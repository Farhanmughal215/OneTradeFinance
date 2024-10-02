package com.xstocks.uc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {

    @Autowired
    private AppConfig appConfig;

    @Bean
    public FanoutExchange userExchange() {
        return new FanoutExchange(appConfig.getRabbitmqConfig().getUser().getExchange(), true, false);
    }

    @Bean
    public Queue userQueueForOrder() {
        return new Queue(appConfig.getRabbitmqConfig().getUser().getQueueOrder(), true, false, false);
    }

    @Bean
    public Queue userQueueForAi() {
        return new Queue(appConfig.getRabbitmqConfig().getUser().getQueueAi(), true, false, false);
    }

    @Bean
    public Binding userBindingOrder(FanoutExchange userExchange,
                                    Queue userQueueForOrder) {
        return BindingBuilder
                .bind(userQueueForOrder)
                .to(userExchange);
    }

    @Bean
    public Binding userBindingAi(FanoutExchange userExchange,
                                 Queue userQueueForAi) {
        return BindingBuilder
                .bind(userQueueForAi)
                .to(userExchange);
    }

    @Bean
    public FanoutExchange depositExchange() {
        return new FanoutExchange(appConfig.getRabbitmqConfig().getDeposit().getExchange(), true, false);
    }

    @Bean
    public Queue depositQueueForOrder() {
        return new Queue(appConfig.getRabbitmqConfig().getDeposit().getQueueOrder(), true, false, false);
    }

    @Bean
    public Queue depositQueueForAi() {
        return new Queue(appConfig.getRabbitmqConfig().getDeposit().getQueueAi(), true, false, false);
    }

    @Bean
    public Binding depositBindingOrder(FanoutExchange depositExchange,
                                    Queue depositQueueForOrder) {
        return BindingBuilder
                .bind(depositQueueForOrder)
                .to(depositExchange);
    }

    @Bean
    public Binding depositBindingAi(FanoutExchange depositExchange,
                                 Queue depositQueueForAi) {
        return BindingBuilder
                .bind(depositQueueForAi)
                .to(depositExchange);
    }

    @Bean
    public FanoutExchange withdrawExchange() {
        return new FanoutExchange(appConfig.getRabbitmqConfig().getWithdraw().getExchange(), true, false);
    }

    @Bean
    public Queue withdrawQueueForOrder() {
        return new Queue(appConfig.getRabbitmqConfig().getWithdraw().getQueueOrder(), true, false, false);
    }

    @Bean
    public Queue withdrawQueueForAi() {
        return new Queue(appConfig.getRabbitmqConfig().getWithdraw().getQueueAi(), true, false, false);
    }

    @Bean
    public Binding withdrawBindingOrder(FanoutExchange withdrawExchange,
                                       Queue withdrawQueueForOrder) {
        return BindingBuilder
                .bind(withdrawQueueForOrder)
                .to(withdrawExchange);
    }

    @Bean
    public Binding withdrawBindingAi(FanoutExchange withdrawExchange,
                                     Queue withdrawQueueForAi) {
        return BindingBuilder
                .bind(withdrawQueueForAi)
                .to(withdrawExchange);
    }

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

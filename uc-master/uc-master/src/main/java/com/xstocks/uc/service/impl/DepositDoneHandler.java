package com.xstocks.uc.service.impl;

import com.xstocks.uc.config.AppConfig;
import com.xstocks.uc.pojo.enums.TodoStatusEnum;
import com.xstocks.uc.pojo.enums.TodoTypeEnum;
import com.xstocks.uc.pojo.po.TodoPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.service.TodoHandler;
import com.xstocks.uc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class DepositDoneHandler implements TodoHandler {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public boolean careAbout(TodoPO todo) {
        return TodoStatusEnum.ACCEPT.getCode() == todo.getTodoStatus() &&
                TodoTypeEnum.DEPOSIT.getCode() == todo.getTodoType();
    }

    @Override
    public void process(TodoPO todo) {
        UserPO user = userService.getById(todo.getUserId());
        if (Objects.nonNull(user)) {
            user.setAvailableAssets(NumberUtils.createBigDecimal(todo.getDescription()).add(user.getAvailableAssets()));
            user.setUpdateTime(new Date());
            user.setUpdateBy(todo.getUpdateBy());
            userService.updateById(user);
            rabbitTemplate.convertAndSend(appConfig.getRabbitmqConfig().getDeposit().getExchange(), null, todo);
        }
    }
}

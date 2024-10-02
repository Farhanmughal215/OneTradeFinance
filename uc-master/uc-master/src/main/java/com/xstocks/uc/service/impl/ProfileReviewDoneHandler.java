package com.xstocks.uc.service.impl;

import com.xstocks.uc.pojo.enums.TodoStatusEnum;
import com.xstocks.uc.pojo.enums.TodoTypeEnum;
import com.xstocks.uc.pojo.enums.UserStatusEnum;
import com.xstocks.uc.pojo.po.TodoPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.pojo.vo.UserProfileVO;
import com.xstocks.uc.service.TodoHandler;
import com.xstocks.uc.service.UserService;
import com.xstocks.uc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class ProfileReviewDoneHandler implements TodoHandler {

    @Autowired
    private UserService userService;

    @Override
    public boolean careAbout(TodoPO todo) {
        return TodoStatusEnum.ACCEPT.getCode() == todo.getTodoStatus() && TodoTypeEnum.PROFILE_REVIEW.getCode() == todo.getTodoType();
    }

    @Override
    public void process(TodoPO todo) {
        UserPO user = userService.getById(todo.getUserId());
        if (Objects.nonNull(user)) {
            UserProfileVO userProfileVO = JsonUtil.parseObject(todo.getDescription(), UserProfileVO.class);
            if (Objects.nonNull(userProfileVO)) {
                user.setAddress(userProfileVO.getAddress());
                user.setUserName(userProfileVO.getUserName());
                user.setBankNo(userProfileVO.getBankNo());
            }
            if (StringUtils.isNotBlank(todo.getAttachments())) {
                user.setIdCard(todo.getAttachments());
            }
            user.setUserStatus(UserStatusEnum.TRADABLE.getCode());
            user.setUpdateTime(new Date());
            user.setUpdateBy(todo.getUpdateBy());
            userService.updateById(user);
        }
    }
}

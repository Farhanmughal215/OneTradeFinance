package com.xstocks.uc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xstocks.uc.mapper.TodoMapper;
import com.xstocks.uc.pojo.event.TodoDoneEvent;
import com.xstocks.uc.pojo.param.TodoQueryParam;
import com.xstocks.uc.pojo.po.OrgPO;
import com.xstocks.uc.pojo.po.TodoPO;
import com.xstocks.uc.pojo.po.UserPO;
import com.xstocks.uc.service.OrgService;
import com.xstocks.uc.service.TodoHandler;
import com.xstocks.uc.service.TodoService;
import com.xstocks.uc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author firtuss
 * @description 针对表【todo】的数据库操作Service实现
 * @createDate 2023-10-28 22:12:22
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TodoServiceImpl extends ServiceImpl<TodoMapper, TodoPO>
        implements TodoService {

    @Autowired
    public List<TodoHandler> todoHandlers;

    @Autowired
    public UserService userService;

    @Autowired
    public OrgService orgService;

    @Autowired
    public TodoMapper todoMapper;

    @Async("eventHandleExecutor")
    @EventListener
    @Override
    public void handleTodoDoneEvent(TodoDoneEvent todoDoneEvent) {
        todoHandlers.parallelStream()
                .forEach(todoHandler -> {
                    try {
                        todoHandler.handle(todoDoneEvent.getTodo());
                    } catch (Exception ex) {
                        log.error("handleTodoDoneEvent_ex", ex);
                    }
                });
    }

    @Override
    public IPage<TodoPO> queryPageToDo(TodoQueryParam todoQueryParam, UserPO currentLoginUser) {
        Page<TodoPO> page = new Page<>(todoQueryParam.getPageNo(), todoQueryParam.getPageSize());
        page.addOrder(OrderItem.desc("create_time"));
        OrgPO currentAdminOrg = orgService.getOne(
                Wrappers.<OrgPO>lambdaQuery().eq(OrgPO::getCode, currentLoginUser.getOrgCode()).last("LIMIT 1"));
        return todoMapper.getMyToDo(page,
                currentAdminOrg.getOrgCodePath(),
                todoQueryParam.getUserId(),
                todoQueryParam.getUserBizId(),
                todoQueryParam.getPhone(),
                todoQueryParam.getTodoType(),
                todoQueryParam.getTodoStatus());
    }
}





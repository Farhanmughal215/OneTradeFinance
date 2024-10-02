package com.xstocks.uc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xstocks.uc.pojo.event.TodoDoneEvent;
import com.xstocks.uc.pojo.param.TodoQueryParam;
import com.xstocks.uc.pojo.po.TodoPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xstocks.uc.pojo.po.UserPO;

/**
 * @author firtuss
 * @description 针对表【todo】的数据库操作Service
 * @createDate 2023-10-28 22:12:22
 */
public interface TodoService extends IService<TodoPO> {
    void handleTodoDoneEvent(TodoDoneEvent todoDoneEvent);

    IPage<TodoPO> queryPageToDo(TodoQueryParam todoQueryParam, UserPO currentLoginUser);
}

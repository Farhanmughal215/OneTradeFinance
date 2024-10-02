package com.xstocks.uc.pojo.event;

import com.xstocks.uc.pojo.po.TodoPO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassName TxEvent
 * @Description TODO
 * @Author firtuss
 * @Date 2023/9/11 12:54
 **/
@Getter
public class TodoDoneEvent extends ApplicationEvent {

    private final TodoPO todo;

    public TodoDoneEvent(Object source, TodoPO todo) {
        super(source);
        this.todo = todo;
    }

}

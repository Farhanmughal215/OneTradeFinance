package com.xstocks.uc.service;

import com.xstocks.uc.pojo.po.TodoPO;

public interface TodoHandler {

    boolean careAbout(TodoPO todo);

    default void handle(TodoPO todo) {
        if (careAbout(todo)) {
            process(todo);
        }
    }

    void process(TodoPO todo);
}

package com.xstocks.uc.pojo.param;

import lombok.Data;

@Data
public class ProcessTodoParam {
    private Long todoId;

    private Integer acceptOrDeny;

    private String feedBack;
}

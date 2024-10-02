package com.xstocks.agent.task;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "app.task-config", value = "enable", havingValue = "true")
public class Task {


}

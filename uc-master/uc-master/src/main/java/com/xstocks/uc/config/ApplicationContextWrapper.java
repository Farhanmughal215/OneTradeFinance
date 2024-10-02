package com.xstocks.uc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextWrapper {

    @Autowired
    private ApplicationContext applicationContext;

    public void publish(ApplicationEvent applicationEvent) {
        applicationContext.publishEvent(applicationEvent);
    }

    public String getActiveProfile() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }
}

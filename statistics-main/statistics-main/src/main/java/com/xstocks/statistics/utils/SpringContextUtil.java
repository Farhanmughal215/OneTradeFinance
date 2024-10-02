package com.xstocks.statistics.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class SpringContextUtil implements ApplicationContextAware {

    public static ApplicationContext context = null;

    /**
     * 获取实体类
     *
     * @param beanName beanName
     * @param <T>      实体类
     * @return 实体类
     */
    public static <T> T getBean(String beanName) {
        try {
            return (T) context.getBean(beanName);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据类型查找
     *
     * @param type 类型
     * @param <T>  实体类
     * @return 实例MAP
     */
    public static <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) {
        return context.getBeansOfType(type);
    }

    /**
     * 获取国际化消息
     *
     * @param key 键
     * @return 值
     */
    public static String getMessage(String key) {
        return context.getMessage(key, null, Locale.getDefault());
    }

    /**
     * 获取所有激活的配置文件
     *
     * @return 配置文件
     */
    public static String[] getActiveProfiles() {
        return context.getEnvironment().getActiveProfiles();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }
}

package com.self.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public final class SpringUtils implements ApplicationContextAware, BeanFactoryPostProcessor {

    private static ApplicationContext context;

    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    private SpringUtils() {
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringUtils.context = context;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        SpringUtils.beanFactory = configurableListableBeanFactory;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBean(String name, Class<T> aClass) {
        return context.getBean(name, aClass);
    }

    public static <T> T getBean(Class<T> aClass) {
        return context.getBean(aClass);
    }

    /**
     * 获取对象
     * @return Object 一个以所给名字注册的bean的实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        return (T) beanFactory.getBean(name);
    }

}

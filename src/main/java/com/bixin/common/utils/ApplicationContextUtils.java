package com.bixin.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author xiangfeihan
 *
 * 获取上下文注入对象
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(String beanName) {
        return (T) getApplicationContext().getBean(beanName);
    }

    public static <T> T getBean(Class clazz) {
        return (T) getApplicationContext().getBean(clazz);
    }

    public static boolean containsBean(String beanName) {
        return getApplicationContext().containsBean(beanName);
    }

    public static void registerSingleton(String beanName, Object instance) {
        if (applicationContext instanceof AnnotationConfigApplicationContext) {
            DefaultListableBeanFactory factory = (DefaultListableBeanFactory) ((AnnotationConfigApplicationContext) applicationContext).getBeanFactory();
            factory.registerSingleton(beanName, instance);
            return;
        }
        throw new UnsupportedOperationException();
    }
}

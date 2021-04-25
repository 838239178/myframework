package org.shijh.myframework.framework;


import org.shijh.myframework.framework.annotation.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum BeanFactory {
    I;

    private final Map<String, Object> beanMap = new ConcurrentHashMap<>(10);

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        Component anno = clazz.getAnnotation(Component.class);
        String key = clazz.getSimpleName();
        if (anno != null) {
            key = anno.value();
        }
        try {
            if (!beanMap.containsKey(key)) beanMap.put(key, clazz.getConstructor().newInstance());
            Object bean = beanMap.get(key);
            return (T)bean;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException ignore) {}
        return null;
    }
}

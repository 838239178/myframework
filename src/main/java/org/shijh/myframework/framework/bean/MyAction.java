package org.shijh.myframework.framework.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MyAction {
    private final Method method;
    private final Object context;

    public MyAction(Method method, Object context) {
        this.method = method;
        this.context = context;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annoClass) {
        return method.getAnnotation(annoClass);
    }

    public Parameter[] getParameters() {
        return method.getParameters();
    }

    public Object invoke(Object... args) throws InvocationTargetException{
        try {
            method.setAccessible(true);
            return method.invoke(context, args);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new InvocationTargetException(e);
        }
    }
}
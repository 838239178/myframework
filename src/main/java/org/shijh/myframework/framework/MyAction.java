package org.shijh.myframework.framework;

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

    public Parameter[] getParameters() {
        return method.getParameters();
    }

    public Object invoke(Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(context, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
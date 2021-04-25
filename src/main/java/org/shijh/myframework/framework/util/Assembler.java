package org.shijh.myframework.framework.util;


import org.shijh.myframework.framework.BeanFactory;
import org.shijh.myframework.framework.annotation.Autowired;
import org.shijh.myframework.framework.annotation.Component;

import java.lang.reflect.Field;
import java.util.logging.Logger;

public class Assembler {

    private static final Class<?>[] primitiveClasses = new Class[]{int.class, long.class, float.class, double.class,
            char.class, boolean.class, byte.class, short.class};

    public static Class<?> primitiveClass(Class<? extends Number> target) {
        for (Class<?> aClass : primitiveClasses) {
            if (target.getSimpleName().toLowerCase().matches(aClass.getName() + ".*")) {
                return aClass;
            }
        }
        return null;
    }

    private static boolean isComponent(Object o) {
        Component anno = o.getClass().getDeclaredAnnotation(Component.class);
        return anno != null;
    }

    public static void autowired(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(Autowired.class) != null) {
                try {
                    Class<?> pClass = field.getType();
                    Object targetBean = BeanFactory.I.getBean(pClass);
                    field.set(o, targetBean);
                    if (targetBean != null && isComponent(targetBean)) {
                        autowired(targetBean);
                    }
                } catch (IllegalAccessException e) {
                    Logger.getGlobal().warning(o.getClass().getSimpleName() + "的参数注入失败，类型为" + field.getType().getSimpleName());
                    e.printStackTrace();
                }
            }
        }
    }
}

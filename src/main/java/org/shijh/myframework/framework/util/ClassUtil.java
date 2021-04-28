package org.shijh.myframework.framework.util;

import org.apache.commons.beanutils.ConvertUtils;

import java.util.Arrays;

public abstract class ClassUtil {

    private static final Class<?>[] primitiveClasses = new Class[]{int.class, long.class, float.class, double.class,
            char.class, boolean.class, byte.class, short.class};

    private static final Class<?>[] wrapperClasses = new Class[]{Integer.class, Long.class, Float.class, Double.class,
            Character.class, Boolean.class, Byte.class, Short.class};

    public static boolean isPrimitiveClass(Class<?> clazz) {
        for (Class<?> primitiveClass : primitiveClasses) {
            if (primitiveClass.equals(clazz)) return true;
        }
        return false;
    }

    public static ClassLoader getClassLoader(Object object) {
        return object.getClass().getClassLoader();
    }

    public static Class<?>[] getInterfaces(Object object) {
        return object.getClass().getInterfaces();
    }

    public static boolean isWrapperClass(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz);
    }

    public static Class<?> primitiveClass(Class<? extends Number> target) {
        for (Class<?> aClass : primitiveClasses) {
            if (target.getSimpleName().toLowerCase().matches(aClass.getName() + ".*")) {
                return aClass;
            }
        }
        return null;
    }

    public static Class<?> wrapperClass(Class<? extends Number> target) {
        for (Class<?> aClass : wrapperClasses) {
            if (aClass.getSimpleName().toLowerCase().matches(target.getName() + ".*")) {
                return aClass;
            }
        }
        return null;
    }

    public static Class<?> getInvokeClass() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className;
        if (stackTrace.length <= 1) {
            className = stackTrace[0].getClassName();
        } else {
            className = stackTrace[1].getClassName();
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignore) { }

        if (cl == null) {
            cl = ClassUtil.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ignore) { }
            }
        }
        return cl;
    }
}

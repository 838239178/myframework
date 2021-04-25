package org.shijh.myframework.framework.util;

public abstract class ClassUtil {

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

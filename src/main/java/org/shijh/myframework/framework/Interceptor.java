package org.shijh.myframework.framework;

import org.shijh.myframework.framework.annotation.DoIntercept;
import org.shijh.myframework.framework.bean.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Interceptor {
    default boolean support(DoIntercept annotation) {
        return false;
    }

    default boolean support(String url) {
        return false;
    }

    ModelAndView preHandle(HttpServletRequest request, HttpServletResponse response, Object[] invokeArgs, String[] annoParams);
    default void afterHandle(HttpServletRequest request, HttpServletResponse response,Object result, String[] annoParams){}
}

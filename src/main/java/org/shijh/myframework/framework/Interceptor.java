package org.shijh.myframework.framework;

import org.shijh.myframework.framework.annotation.DoIntercept;
import org.shijh.myframework.framework.bean.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

public interface Interceptor {
    default boolean support(DoIntercept annotation) {
        return false;
    }

    default boolean support(String url) {
        return false;
    }

    ModelAndView preHandle(HttpServletRequest request, HttpServletResponse response, String[] params);
}

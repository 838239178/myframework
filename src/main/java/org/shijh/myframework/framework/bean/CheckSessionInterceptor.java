package org.shijh.myframework.framework.bean;

import org.shijh.myframework.framework.Interceptor;
import org.shijh.myframework.framework.annotation.Component;
import org.shijh.myframework.framework.annotation.DoIntercept;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component("CheckSession")
public class CheckSessionInterceptor implements Interceptor {

    @Override
    public boolean support(DoIntercept annotation) {
        return annotation != null && annotation.value().equals("CheckSession");
    }

    @Override
    public boolean support(String url) {
        return false;
    }

    @Override
    public ModelAndView preHandle(HttpServletRequest request, HttpServletResponse response, Object[] invokeArgs, String[] annoParams) {
        HttpSession session = request.getSession();
        for (String param : annoParams) {
            Object o = session.getAttribute(param);
            if (o == null) {
                return new ModelAndView(){{
                    setSuccess(false);
                    setView("/error.jsp");
                    setModel("Session缺少必要参数：" + param);
                }};
            }
        }
        return null;
    }
}

package org.shijh.myframework.framework.util;

import org.shijh.myframework.framework.Interceptor;
import org.shijh.myframework.framework.annotation.DoIntercept;
import org.shijh.myframework.framework.bean.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;

@Deprecated
public class AopProxyUtil {
    private static List<Interceptor> interceptors = new LinkedList<>();
    public static void setInterceptors(List<Interceptor> interceptors) {
        AopProxyUtil.interceptors = interceptors;
    }

    @SuppressWarnings("unchecked")
    public static<T> T getAopProxyObject(final Object target, HttpServletRequest request, HttpServletResponse response) {
        return (T) Proxy.newProxyInstance(ClassUtil.getClassLoader(target), ClassUtil.getInterfaces(target), (p,m,arg)->{
            DoIntercept anno = target.getClass().getAnnotation(DoIntercept.class);
            List<Interceptor> supportInterceptors = new LinkedList<>();
            if (anno != null) {
                for (Interceptor interceptor : interceptors) {
                    if (interceptor.support(anno)) {
                        ModelAndView mv = interceptor.preHandle(request, response, arg, anno.params());
                        if(mv != null) return mv;
                        supportInterceptors.add(interceptor);
                    }
                }
            }
            Object result =  m.invoke(target, arg);
            for (Interceptor interceptor : supportInterceptors) {
                interceptor.afterHandle(request,response,result,anno.params());
            }
            return result;
        });
    }
}

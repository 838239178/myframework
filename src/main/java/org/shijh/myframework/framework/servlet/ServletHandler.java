package org.shijh.myframework.framework.servlet;


import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.beanutils.BeanUtils;
import org.shijh.myframework.framework.Interceptor;
import org.shijh.myframework.framework.annotation.Component;
import org.shijh.myframework.framework.annotation.DoIntercept;
import org.shijh.myframework.framework.util.Assembler;
import org.shijh.myframework.framework.bean.ModelAndView;
import org.shijh.myframework.framework.bean.MyAction;
import org.shijh.myframework.framework.annotation.Mapping;
import org.shijh.myframework.framework.annotation.Param;
import org.shijh.myframework.framework.controller.Controller;
import org.shijh.myframework.framework.util.ClassUtil;
import org.shijh.myframework.framework.util.Str;
import sun.rmi.runtime.Log;
import sun.util.resources.th.CalendarData_th;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

@Component("servletHandler")
@lombok.extern.java.Log
public class ServletHandler {
    private final Map<String, Controller> ctrlMap;
    private final List<Interceptor> interceptorList;

    public void addCtrl(Controller... controllers) {
        for (Controller ctrl : controllers) {
            Assembler.autowired(ctrl);
            Mapping anno = ctrl.getClass().getAnnotation(Mapping.class);
            String key = anno == null ? "/.*" : anno.value();
            ctrlMap.put(".*" + key + "(/.*)\\??", ctrl);
        }
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptorList.add(interceptor);
    }

    public ServletHandler() {
        ctrlMap = new ConcurrentSkipListMap<>();
        interceptorList = new LinkedList<>();
    }

    private MyAction getMethod(String url) {
        Iterator<Map.Entry<String, Controller>> iterator = ctrlMap.entrySet().iterator();
        Map.Entry<String, Controller> next;
        while (iterator.hasNext()) {
            next = iterator.next();
            String s = Str.matchFst(next.getKey(), url);
            if (s != null) {
                return getMethod(next.getValue(), s);
            }
        }
        return null;
    }

    private Map<String, Object> getCurrentMap(Map<String, ? extends String[]> map) {
        Map<String, Object> cur = new HashMap<>();
        map.forEach((key, val) -> {
            if (val.length == 1) {
                String[] vals = val;
                if (!Str.empty(vals[0])) {
                    cur.put(key, vals[0]);
                } else {
                    cur.put(key, "");
                }
            } else if (val.length > 1) {
                cur.put(key, val);
            }
        });
        return cur;
    }

    private MyAction getMethod(Controller ctrl, String path) {
        Method[] methods = ctrl.getClass().getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            Mapping annotation = method.getAnnotation(Mapping.class);
            if (annotation.value().equals(path)) {
                return new MyAction(method, ctrl);
            }
        }
        return null;
    }

    private ModelAndView preIntercept(DoIntercept anno, HttpServletResponse response, HttpServletRequest request) {
        for (Interceptor interceptor : interceptorList) {
            if (anno != null && interceptor.support(anno) || interceptor.support(request.getRequestURL().toString())) {
                log.info("do intercept:" + interceptor.getClass().getName());
                ModelAndView mv = interceptor.preHandle(request, response, anno == null ? new String[0] : anno.params());
                if (mv != null) return mv;
            }
        }
        return null;
    }

    private ModelAndView doTargetInvoke(MyAction method,List<Object> realParam, HttpServletRequest request, HttpServletResponse response) {
        DoIntercept doi = method.getAnnotation(DoIntercept.class);
        ModelAndView mv = preIntercept(doi, response, request);
        if (mv == null) {
            try {
                mv = (ModelAndView) method.invoke(realParam.toArray());
            } catch (InvocationTargetException invokeException) {
                mv = new ModelAndView() {{
                    setSuccess(false);
                    setView("/error.jsp");
                    setModel("执行错误," + invokeException.getCause().getMessage());
                }};
            }
        }
        return mv;
    }

    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) {
        String url = request.getRequestURL().toString();
        HttpSession session = request.getSession();
        Map<String, Object> curMap = getCurrentMap(request.getParameterMap());
        MyAction method = getMethod(url);
        if (method == null) {
            log.warning("找不到控制器方法，错误url:" + url);
            return null;
        }
        Parameter[] methodParams = method.getParameters();
        List<Object> realParam = new ArrayList<>(10);
        try {
            Object[] mapValues = curMap.values().toArray();
            for (int i = 0; i < methodParams.length; i++) {
                Parameter mParam = methodParams[i];
                Object value = "";
                Param annotation = mParam.getAnnotation(Param.class);
                // 如果有使用注解 则根据注解名称直接查找 否则按参数顺序一一对应
                if (annotation != null) {
                    value = curMap.getOrDefault(annotation.value(), "");
                } else if (i < mapValues.length) {
                    value = mapValues[i];
                }
                // 尝试获取待注入参数的构造器 否则把整个paramMap当作一个对象处理
                try {
                    Object paramValue;
                    // 对HttpSession对象特殊处理
                    if (mParam.getType().equals(HttpSession.class)) {
                        paramValue = session;
                    } else {
                        //尝试直接构造参数 （非JavaBean）
                        Class<?> type = mParam.getType();
                        if (ClassUtil.isPrimitiveClass(type)) {
                            type = ClassUtil.wrapperClass((Class<? extends Number>) type);
                            assert type != null;
                        }
                        Constructor<?> constructor = type.getConstructor(value.getClass());
                        paramValue = constructor.newInstance(value);
                    }
                    realParam.add(paramValue);
                } catch (NoSuchMethodException ne) {
                    //尝试构造(JavaBean)
                    Object p = mParam.getType().newInstance();
                    BeanUtils.populate(p, curMap);
                    realParam.add(p);
                } catch (InvocationTargetException ne) {
                    //构造基本类型时可能因为空参导致错误
                    log.warning("控制器方法接受的参数类型有误");
                    realParam.add(null);
                }
            }
            return doTargetInvoke(method, realParam, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

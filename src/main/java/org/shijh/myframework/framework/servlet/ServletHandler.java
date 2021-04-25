package org.shijh.myframework.framework.servlet;


import org.apache.commons.beanutils.BeanUtils;
import org.shijh.myframework.framework.Assembler;
import org.shijh.myframework.framework.ModelAndView;
import org.shijh.myframework.framework.MyAction;
import org.shijh.myframework.framework.annotation.Mapping;
import org.shijh.myframework.framework.annotation.Param;
import org.shijh.myframework.framework.controller.Controller;
import org.shijh.myframework.framework.util.Str;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class ServletHandler {
    private final Map<String, Controller> ctrlMap;

    public void addCtrl(Controller... controllers) {
        for (Controller ctrl : controllers) {
            Assembler.autowired(ctrl);
            Mapping anno = ctrl.getClass().getAnnotation(Mapping.class);
            String key = anno == null ? "/.*" : anno.value();
            ctrlMap.put(".*" + key + "(/.*)\\??", ctrl);
        }
    }

    public ServletHandler() {
        ctrlMap = new ConcurrentSkipListMap<>();
//        addCtrl(
//                new MajorController(),
//                new StudentController()
//        );
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
        map.forEach((key,val)->{
            if (val.length == 1) {
                String[] vals = val;
                if (!Str.empty(vals[0])) {
                    cur.put(key, vals[0]);
                } else {
                    cur.put(key,"");
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

    public ModelAndView execute(String url, Map<String, String[]> paramMap) {
        Map<String,Object> curMap = getCurrentMap(paramMap);
        MyAction method = getMethod(url);
        if (method == null) return null;
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
                    Constructor<?> constructor = mParam.getType().getConstructor(value.getClass());
                    Object rp = constructor.newInstance(value);
                    realParam.add(rp);
                } catch (NoSuchMethodException ne) {
                    Object p = mParam.getType().newInstance();
                    BeanUtils.populate(p, curMap);
                    realParam.add(p);
                }
            }
            return (ModelAndView) method.invoke(realParam.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

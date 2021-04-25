package org.shijh.myframework.framework.dao;



import org.shijh.myframework.framework.bean.ResultMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;

public abstract class Dao {
    private ResultMap rm = null;
    protected abstract Object resultMap(Map<String, Object> resultSet) throws IllegalAccessException, InvocationTargetException, SQLException;
    public ResultMap getResultMap() {
        if (rm != null) return rm;
         try {
            Method rMethod = this.getClass().getDeclaredMethod("resultMap", Map.class);
            return rm = new ResultMap(rMethod, this);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.out.println("子类没有实现 resultMap 方法");
        }
        return null;
    }
}

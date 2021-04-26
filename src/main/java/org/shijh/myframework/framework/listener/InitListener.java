package org.shijh.myframework.framework.listener;

import lombok.extern.java.Log;
import org.shijh.myframework.framework.Interceptor;
import org.shijh.myframework.framework.bean.BeanFactory;
import org.shijh.myframework.framework.bean.CheckSessionInterceptor;
import org.shijh.myframework.framework.bean.FrameworkConfig;
import org.shijh.myframework.framework.bean.JdbcConfig;
import org.shijh.myframework.framework.controller.Controller;
import org.shijh.myframework.framework.dao.ConnectionManager;
import org.shijh.myframework.framework.servlet.ServletHandler;
import org.shijh.myframework.framework.util.ResourceUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.Checksum;

@WebListener
@Log
public class InitListener implements ServletContextListener {

    private final ServletHandler servletHandler = BeanFactory.I.getBean(ServletHandler.class);

    private void initController(List<String> controllers) throws ClassNotFoundException {
        if (controllers != null) {
            for (String className : controllers) {
                Object bean = BeanFactory.I.getBean(Class.forName(className));
                servletHandler.addCtrl((Controller) bean);
            }
        }
    }

    private void initJdbc(JdbcConfig jdbcConfig) throws ClassNotFoundException {
        if (jdbcConfig == null) {
            throw new ClassNotFoundException("缺少jdbc参数");
        }
        ConnectionManager.setJdbcConfig(jdbcConfig);
    }

    private void InitInterceptors(List<String> interceptors) throws ClassNotFoundException {
        if (interceptors != null) {
            for (String interceptor : interceptors) {
                Object i = BeanFactory.I.getBean(Class.forName(interceptor));
                servletHandler.addInterceptor(((Interceptor) i));
            }
        }
        servletHandler.addInterceptor(BeanFactory.I.getBean(CheckSessionInterceptor.class));
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            InputStream resource = ResourceUtil.getResourceAsStream("classpath:myframework.yml");
            FrameworkConfig config = ResourceUtil.loadYamlAs(resource, FrameworkConfig.class);
            initJdbc(config.getJdbcConfig());
            initController(config.getController());
            InitInterceptors(config.getInterceptor());
        } catch (FileNotFoundException e) {
            log.warning("找不到配置文件‘myframework.yml’");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            log.warning("找不到必要参数Controller/jdbcConfig，检查配置文件是否有误");
            e.printStackTrace();
        }
    }
}

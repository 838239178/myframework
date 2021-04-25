package org.shijh.myframework.framework.listener;

import org.shijh.myframework.framework.BeanFactory;
import org.shijh.myframework.framework.FrameworkConfig;
import org.shijh.myframework.framework.controller.Controller;
import org.shijh.myframework.framework.servlet.ServletHandler;
import org.shijh.myframework.framework.util.ResourceUtil;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.FileNotFoundException;
import java.io.InputStream;

@WebListener
public class InitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletHandler servletHandler = BeanFactory.I.getBean(ServletHandler.class);
        try {
            InputStream resource = ResourceUtil.getResourceAsStream("classpath:myframework.yml");
            FrameworkConfig config = ResourceUtil.loadYamlAs(resource, FrameworkConfig.class);
            for (String className : config.getController()) {
                Object bean = BeanFactory.I.getBean(Class.forName(className));
                assert servletHandler != null;
                servletHandler.addCtrl((Controller) bean);
            }
        } catch (FileNotFoundException e) {
            System.out.println("找不到配置文件‘myframework.yml’");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("找不到Controller，检查配置文件是否有误");
            e.printStackTrace();
        }
    }
}

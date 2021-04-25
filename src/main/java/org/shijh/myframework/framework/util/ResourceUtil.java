package org.shijh.myframework.framework.util;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class ResourceUtil {

    public static InputStream getResourceAsStream(String location) throws FileNotFoundException {
        if (Str.empty(location)) {
            throw new FileNotFoundException("资源路径不能为空");
        }
        if (!location.startsWith("classpath:")) {
            throw new FileNotFoundException("路径必须以'classpath:'开头");
        }
        String path = location.substring("classpath:".length());
        ClassLoader classLoader = ClassUtil.getDefaultClassLoader();
        return classLoader.getResourceAsStream(path);
    }

    public static <T> T loadYamlAs(InputStream resource, Class<T> clazz) {
        Yaml yaml = new Yaml();
        return yaml.loadAs(resource, clazz);
    }
}

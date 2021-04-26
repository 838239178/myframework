package org.shijh.myframework.framework.bean;

import lombok.Data;

import java.util.List;

@Data
public class FrameworkConfig {
    private List<String> controller;
    private List<String> interceptor;
    private JdbcConfig jdbcConfig;
}

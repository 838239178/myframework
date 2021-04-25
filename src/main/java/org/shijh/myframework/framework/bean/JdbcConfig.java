package org.shijh.myframework.framework.bean;

import lombok.Data;

@Data
public class JdbcConfig {
    private String driver;
    private String url;
    private String username;
    private String password;
}

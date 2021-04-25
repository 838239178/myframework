package org.shijh.myframework.framework.bean;

import lombok.Data;

@Data
public class ModelAndView {
    private Boolean success;
    private String view;
    private Object model;
}

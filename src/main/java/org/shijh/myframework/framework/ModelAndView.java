package org.shijh.myframework.framework;

import lombok.Data;

@Data
public class ModelAndView {
    private Boolean success;
    private String view;
    private Object model;
}

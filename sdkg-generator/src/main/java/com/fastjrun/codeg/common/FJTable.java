/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Table类
 */
@Data
public class FJTable {

    /**
     * 表字段集合
     */
    private Map<String, FJColumn> columns;
    /**
     * 表名
     */
    private String name;
    /**
     * 备注
     */
    private String comment;
    /**
     * javaBean 类名
     */
    private String className;
    /**
     * javaBean 自动
     */
    private List<String> primaryKeyColumnNames;
}

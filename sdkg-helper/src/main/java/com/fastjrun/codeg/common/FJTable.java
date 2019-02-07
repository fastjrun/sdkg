package com.fastjrun.codeg.common;

import java.util.List;
import java.util.Map;

/**
 * Table类
 */
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

    public List<String> getPrimaryKeyColumnNames() {
        return primaryKeyColumnNames;
    }

    public void setPrimaryKeyColumnNames(List<String> primaryKeyColumnNames) {
        this.primaryKeyColumnNames = primaryKeyColumnNames;
    }

    public Map<String, FJColumn> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, FJColumn> columns) {
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "fjTable [columns=" + columns + ", name=" + name + ", comment=" + comment + ", className=" + className
                + "]";
    }
}

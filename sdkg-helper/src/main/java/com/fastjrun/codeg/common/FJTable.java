package com.fastjrun.codeg.common;

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
     * 表名，中文
     */
    private String name;
    /**
     * javaBean 类名
     */
    private String className;
    /**
     * javaBean 自动
     */
    private Map<String, FJColumn> primaryKeyColumns;

    public Map<String, FJColumn> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public void setPrimaryKeyColumns(Map<String, FJColumn> primaryKeyColumns) {
        this.primaryKeyColumns = primaryKeyColumns;
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

    @Override
    public String toString() {
        return "FJTable [columns=" + columns + ", name="
                + name + ", className=" + className + "]";
    }
}

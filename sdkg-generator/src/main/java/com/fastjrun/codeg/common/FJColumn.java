/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

/**
 * 字段类
 */
public class FJColumn {

    /**
     * 字段名
     */
    private String name;

    /**
     * 属性名，由code处理得到，
     */
    private String fieldName;

    /**
     *
     */
    private String comment;

    /**
     * 字段数据类型
     */
    private String datatype;

    /**
     * 数据类型，<a:DataType>
     */
    private String datatypeSource;

    /**
     * 自动增长属性，<a:Identity>
     */
    private boolean identity;

    public boolean isIdentity() {
        return identity;
    }

    public void setIdentity(boolean identity) {
        this.identity = identity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getDatatypeSource() {
        return datatypeSource;
    }

    public void setDatatypeSource(String datatypeSource) {
        this.datatypeSource = datatypeSource;
    }

    @Override
    public String toString() {
        return "FJColumn [name=" + name + ", fieldName=" + fieldName + ", comment=" + comment
                + ", datatype=" + datatype + ", datatypeSource=" + datatypeSource + ", identity=" + identity + "]";
    }
}

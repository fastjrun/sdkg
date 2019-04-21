/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.mybatis.declare;

public class Declare {
    protected String sql;

    public Declare() {

    }

    public Declare(String sql) {
        this.sql = sql;
    }

    public void createSql() {

    }

    public String getSql() {
        createSql();
        return sql;
    }
}

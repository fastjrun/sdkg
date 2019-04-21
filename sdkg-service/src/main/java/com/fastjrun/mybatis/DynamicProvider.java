/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.mybatis;

import java.util.Map;

import com.fastjrun.mybatis.declare.Declare;

public class DynamicProvider {
    public String sql(Declare dec) {
        return dec.getSql();
    }

    public String sqlLimit(Map<String, Object> para) {
        Declare dec = (Declare) para.get("dec");
        return dec.getSql();
    }
}

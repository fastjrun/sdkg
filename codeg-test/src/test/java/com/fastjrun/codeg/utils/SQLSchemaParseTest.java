/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.utils;

import java.util.Map;

import org.testng.annotations.Test;

import com.fastjrun.codeg.common.DataBaseObject;
import com.fastjrun.codeg.common.FJTable;

public class SQLSchemaParseTest {
    @Test
    public void testParse() {
        DataBaseObject dataBaseObject =
                SQLSchemaParse.process(SQLSchemaParse.TargetType.TargetType_Mysql, "fast-demo.sql");
        System.out.println(dataBaseObject.getTargetType());
        Map<String, FJTable> map = dataBaseObject.getTableMap();
        for (FJTable table : map.values()) {
            System.out.println(table);
        }
    }
}

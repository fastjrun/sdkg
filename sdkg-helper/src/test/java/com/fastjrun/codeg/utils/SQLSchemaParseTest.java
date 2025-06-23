/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.utils;

import com.fastjrun.codeg.common.DataBaseObject;
import com.fastjrun.codeg.common.FJTable;
import org.testng.annotations.Test;

import java.util.Map;

public class SQLSchemaParseTest {

    @Test
    public void testProcess() {
        String sqlFile = "fast-demo.sql";
        DataBaseObject dataBaseObject =
          SQLSchemaParse.process(SQLSchemaParse.TargetType.TargetType_Mysql, sqlFile);
        System.out.println(dataBaseObject.getTargetType());
        Map<String, FJTable> map = dataBaseObject.getTableMap();
        for (FJTable table : map.values()) {
            System.out.println(table);
        }
    }
}

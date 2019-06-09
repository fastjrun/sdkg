/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.codeg.common.DataBaseObject;
import com.fastjrun.codeg.common.FJTable;
import com.fastjrun.test.AbstractAdVancedTestNGSpringContextTest;
import org.testng.annotations.Test;

import java.util.Map;

public class SQLSchemaParseTest extends AbstractAdVancedTestNGSpringContextTest {
    @Test(dataProvider = "loadParam")
    public void testParse(String reqParamsJsonStrAndAssert) {
        JsonNode[] jsonNodes = this.parseStr2JsonArray(reqParamsJsonStrAndAssert);
        String sqlFile = jsonNodes[0].get("sqlFile").asText();
        DataBaseObject dataBaseObject =
          SQLSchemaParse.process(SQLSchemaParse.TargetType.TargetType_Mysql, sqlFile);
        System.out.println(dataBaseObject.getTargetType());
        Map<String, FJTable> map = dataBaseObject.getTableMap();
        for (FJTable table : map.values()) {
            System.out.println(table);
        }
    }
}

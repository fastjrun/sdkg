package com.fastjrun.codeg.utils;

import com.fastjrun.codeg.common.DataBaseObject;
import com.fastjrun.codeg.common.FJTable;
import org.testng.annotations.Test;

import java.util.Map;

public class SQLSchemaParseTest {
    @Test
    public void testParse() {
        DataBaseObject dataBaseObject = SQLSchemaParse.process("fast_demo.sql");
        System.out.println(dataBaseObject.getTargetType());
        Map<String, FJTable> map = dataBaseObject.getTableMap();
        for (FJTable table : map.values()) {
            System.out.println(table);
        }
    }
}

/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.test.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.utils.JacksonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class TestUtils {

    public static final String ASSERTION_SPLITSTRING      = ",assert=";
    public static final int    PARAM_ASSERTION_ARRAY_SIZE = 2;

    public static Properties initParam(String proptiesFileInClassPath) throws IOException {
        Properties properties = new Properties();
        InputStream inParam = TestUtils.class.getResourceAsStream(proptiesFileInClassPath);
        properties.load(inParam);
        inParam.close();
        return properties;

    }

    public static Object[][] loadParam(Properties propParams, String className, Method method) {
        Set<String> keys = propParams.stringPropertyNames();
        List<String[]> parameters = new ArrayList<>();
        for (String key : keys) {
            if (key.startsWith(className.concat(".").concat(method.getName()).concat("."))) {
                String value = propParams.getProperty(key);
                parameters.add(new String[] { value });
            }
        }
        Object[][] object = new Object[parameters.size()][];
        for (int i = 0; (i < object.length); i++) {
            String[] str = parameters.get(i);
            object[i] = new String[str.length];
            System.arraycopy(str, 0, object[i], 0, str.length);
        }
        return object;
    }

    public static JsonNode[] parseStr2JsonArray(String reqParamsJsonStrAndAssert) {
        JsonNode[] jsonNodes = new JsonNode[2];
        String[] reqParamsJsonStrAndAssertArray =
          reqParamsJsonStrAndAssert.split(ASSERTION_SPLITSTRING);
        String reqParamsJsonStr = reqParamsJsonStrAndAssertArray[0];
        jsonNodes[0] = JacksonUtils.toJsonNode(reqParamsJsonStr);
        if (reqParamsJsonStrAndAssertArray.length == PARAM_ASSERTION_ARRAY_SIZE) {
            jsonNodes[1] = JacksonUtils.toJsonNode(reqParamsJsonStrAndAssertArray[1]);
        }
        return jsonNodes;
    }
}


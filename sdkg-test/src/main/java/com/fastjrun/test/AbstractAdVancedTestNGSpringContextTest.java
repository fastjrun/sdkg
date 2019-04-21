/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.test.util.TestUtils;
import com.fastjrun.utils.JacksonUtils;

@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
public abstract class AbstractAdVancedTestNGSpringContextTest extends
        AbstractTestNGSpringContextTests {

    protected static final String ASSERTION_SPLITSTRING = ",assert=";
    protected static final int PARAM_ASSERTION_ARRAY_SIZE = 2;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Properties propParams = new Properties();

    @BeforeClass
    @Parameters({
            "envName"
    })
    protected void initParam(@Optional("unitTest") String envName) {
        try {
            InputStream inParam =
                    this.getClass().getResourceAsStream((("/testdata/" + envName) + ".properties"));
            propParams.load(inParam);
            inParam.close();
        } catch (IOException e) {
            log.error("load config for error:{}", e);
        }
    }

    @DataProvider(
            name = "loadParam"
    )
    public Object[][] loadParam(Method method) {
        return TestUtils.loadParam(this.propParams, this.getClass().getSimpleName(), method);
    }

    protected JsonNode generateParamJson(String reqParamsJsonStrAndAssert) {
        String[] reqParamsJsonStrAndAssertArray = reqParamsJsonStrAndAssert.split(",assert=");
        String reqParamsJsonStr = reqParamsJsonStrAndAssertArray[0];
        log.debug(reqParamsJsonStr);
        return JacksonUtils.toJsonNode(reqParamsJsonStr);
    }



    protected JsonNode[] parseStr2JsonArray(String reqParamsJsonStrAndAssert) {
        JsonNode[] jsonNodes = new JsonNode[2];

        String[] reqParamsJsonStrAndAssertArray = reqParamsJsonStrAndAssert.split(ASSERTION_SPLITSTRING);
        jsonNodes[0] = JacksonUtils.toJsonNode(reqParamsJsonStrAndAssertArray[0]);
        if (reqParamsJsonStrAndAssertArray.length == PARAM_ASSERTION_ARRAY_SIZE) {
            jsonNodes[1] = JacksonUtils.toJsonNode(reqParamsJsonStrAndAssertArray[1]);
        }
        return jsonNodes;
    }
}

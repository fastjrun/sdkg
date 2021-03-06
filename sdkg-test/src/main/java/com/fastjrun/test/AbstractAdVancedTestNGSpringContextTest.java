/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.test.util.TestUtils;
import com.fastjrun.utils.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

@ContextConfiguration(locations = { "classpath*:applicationContext.xml" })
public abstract class AbstractAdVancedTestNGSpringContextTest
  extends AbstractTestNGSpringContextTests {


    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Properties propParams = new Properties();

    @BeforeClass
    @Parameters({ "envName" })
    protected void initParam(@Optional("unitTest") String envName) {
        log.debug("initParam");
        try {
            this.propParams = TestUtils.initParam("/testdata/" + envName + ".properties");
        } catch (IOException e) {
            log.error("load config for error:{}", e);
        }
    }

    @DataProvider(name = "loadParam")
    public Object[][] loadParam(Method method) {
        log.debug("loadParam");
        return TestUtils.loadParam(this.propParams, this.getClass().getSimpleName(), method);
    }

    protected JsonNode[] parseStr2JsonArray(String reqParamsJsonStrAndAssert) {
        log.debug("reqParamsJsonStrAndAssert={}", reqParamsJsonStrAndAssert);
        return TestUtils.parseStr2JsonArray(reqParamsJsonStrAndAssert);
    }
}

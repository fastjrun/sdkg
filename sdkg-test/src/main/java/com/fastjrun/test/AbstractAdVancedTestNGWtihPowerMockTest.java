/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.test.util.TestUtils;
import org.powermock.modules.testng.PowerMockTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public abstract class AbstractAdVancedTestNGWtihPowerMockTest extends PowerMockTestCase {

    protected ApplicationContext applicationContext;

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Properties propParams = new Properties();

    @BeforeClass
    @Parameters({ "envName" })
    protected void initParamAndSpringConfig(@Optional("unitTest") String envName) {
        applicationContext =
          new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        try {
            this.propParams = TestUtils.initParam("/testdata/" + envName + ".properties");
        } catch (IOException e) {
            log.error("load config for error:{}", e);
        }
    }

    protected abstract void setUp();

    @DataProvider(name = "loadParam")
    public Object[][] loadParam(Method method) {
        return TestUtils.loadParam(this.propParams, this.getClass().getSimpleName(), method);
    }

    protected JsonNode[] parseStr2JsonArray(String reqParamsJsonStrAndAssert) {
        log.debug("reqParamsJsonStrAndAssert={}", reqParamsJsonStrAndAssert);
        return TestUtils.parseStr2JsonArray(reqParamsJsonStrAndAssert);
    }
}

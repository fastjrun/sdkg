/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.util.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.collections.Maps;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class AbstractTestNGTest {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Map<String, Object> propParams = Maps.newHashMap();

    @BeforeClass
    @Parameters({"envName"})
    protected void initParam(@Optional("unitTest") String envName) {
        log.debug("initParam");
        try {
            this.propParams = TestUtils.initParam("/testdata/" + envName + ".yaml");
        } catch (IOException e) {
            log.error("load config for error:{}", e);
        }
    }

    @DataProvider(name = "loadParam")
    public Object[][] loadParam(Method method) {
        log.debug("loadParam");
        return TestUtils.loadParam(this.propParams, this.getClass().getSimpleName(), method);
    }

    protected <T> void processExceptionInResponse(JsonNode assertJson, Exception e) {
        if (e instanceof CodeGException && assertJson != null) {
            JsonNode codeJson = assertJson.get("code");
            if (codeJson != null) {
                String code = codeJson.asText();
                if (code != null) {
                    Assert.assertEquals(((CodeGException) e).getCode(), code, "消息码不同");
                }
            } else {
                log.error("", e);
            }
        } else {
            log.error("", e);
        }
    }

}

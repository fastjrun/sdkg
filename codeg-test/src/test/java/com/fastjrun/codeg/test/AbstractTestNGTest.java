package com.fastjrun.codeg.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.test.util.TestUtils;
import com.fastjrun.utils.JacksonUtils;

public abstract class AbstractTestNGTest {

    protected final Logger log = LogManager.getLogger(this.getClass());

    protected static final String ASSERTION_SPLITSTRING = ",assert=";

    protected static final int PARAM_ASSERTION_ARRAY_SIZE = 2;

    protected Properties propParams = new Properties();

    protected void initParam(String envName) {

        try {
            this.propParams = TestUtils.initParam("/testdata/" + envName + ".properties");
        } catch (IOException e) {
            log.error("", e);
        }

    }

    @DataProvider(
            name = "loadParam"
    )
    public Object[][] loadParam(Method method) {
        return TestUtils.loadParam(this.propParams, this.getClass().getSimpleName(), method);
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

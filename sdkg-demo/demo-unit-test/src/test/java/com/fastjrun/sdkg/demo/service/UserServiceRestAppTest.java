/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.sdkg.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.common.ServiceException;
import com.fastjrun.sdkg.demo.packet.app.RegistserRestRequestBody;
import com.fastjrun.test.AbstractAdVancedTestNGSpringContextTest;

public class UserServiceRestAppTest extends AbstractAdVancedTestNGSpringContextTest {

    @Autowired
    UserServiceRestApp userServiceRestApp;

    @BeforeTest
    @org.testng.annotations.Parameters({
            "envName"
    })
    protected void init(String envName) {
        this.initParam(envName);
    }

    @Test(dataProvider = "loadParam")
    @org.testng.annotations.Parameters({
            "reqParamsJsonStrAndAssert"
    })
    public void testRegisterv2(String reqParamsJsonStrAndAssert) {
        String[] reqParamsJsonStrAndAssertArray = reqParamsJsonStrAndAssert.split(",assert=");
        String reqParamsJsonStr = reqParamsJsonStrAndAssertArray[0];
        log.debug(reqParamsJsonStr);
        JsonNode reqParamsJson = com.fastjrun.utils.JacksonUtils.toJsonNode(reqParamsJsonStr);
        RegistserRestRequestBody requestBody = null;
        JsonNode reqJsonRequestBody = reqParamsJson.get("requestBody");
        if (reqJsonRequestBody != null) {
            requestBody = com.fastjrun.utils.JacksonUtils
                    .readValue(reqJsonRequestBody.toString(), RegistserRestRequestBody.class);
        }
        JsonNode assertJson = null;
        if (reqParamsJsonStrAndAssertArray.length == 2) {
            assertJson = com.fastjrun.utils.JacksonUtils.toJsonNode(reqParamsJsonStrAndAssertArray[1]);
        }
        if (assertJson != null) {
            JsonNode codeNode = assertJson.get("code");
            if (codeNode != null) {
                try {
                    userServiceRestApp.registerv2(requestBody);
                } catch (ServiceException e) {
                    org.testng.Assert.assertEquals(e.getCode(), codeNode.asText(),
                            ("返回消息码不是指定消息码:" + codeNode
                                    .asText()));
                }
            } else {
                userServiceRestApp.registerv2(requestBody);
            }
        } else {
            userServiceRestApp.registerv2(requestBody);
        }

    }
}

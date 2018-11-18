/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.sdkg.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.common.ServiceException;
import com.fastjrun.sdkg.demo.entity.User;
import com.fastjrun.test.AbstractAdVancedTestNGSpringContextTest;
import com.fastjrun.utils.JacksonUtils;

public class CoreUserServiceTest extends AbstractAdVancedTestNGSpringContextTest {

    @Autowired
    CoreUserService coreUserService;

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
    public void testCheckLoign(String reqParamsJsonStrAndAssert) {
        String[] reqParamsJsonStrAndAssertArray = reqParamsJsonStrAndAssert.split(",assert=");
        String reqParamsJsonStr = reqParamsJsonStrAndAssertArray[0];
        log.debug(reqParamsJsonStr);
        JsonNode reqParamsJson = JacksonUtils.toJsonNode(reqParamsJsonStr);
        String uuid = null;
        JsonNode uuidjSon = reqParamsJson.get("uuid");
        if (uuidjSon != null) {
            uuid = uuidjSon.asText();
        }
        String deviceId = null;
        JsonNode deviceIdjSon = reqParamsJson.get("deviceId");
        if (deviceIdjSon != null) {
            deviceId = deviceIdjSon.asText();
        }
        JsonNode assertJson = null;
        if (reqParamsJsonStrAndAssertArray.length == 2) {
            assertJson = com.fastjrun.utils.JacksonUtils.toJsonNode(reqParamsJsonStrAndAssertArray[1]);
        }
        if (assertJson != null) {
            JsonNode codeNode = assertJson.get("code");
            if (codeNode != null) {
                try {
                    coreUserService.checkLoign(uuid, deviceId);
                } catch (ServiceException e) {
                    org.testng.Assert.assertEquals(e.getCode(), codeNode.asText(),
                            ("返回消息码不是指定消息码:" + codeNode
                                    .asText()));
                }
            } else {
                coreUserService.checkLoign(uuid, deviceId);
            }
        } else {
            coreUserService.checkLoign(uuid, deviceId);
        }

    }

    @Test(dataProvider = "loadParam")
    @org.testng.annotations.Parameters({
            "reqParamsJsonStrAndAssert"
    })
    //User autoLogin(String deviceId, String uuidOld, String uuidNew);
    public void testAutoLogin(String reqParamsJsonStrAndAssert) {
        String[] reqParamsJsonStrAndAssertArray = reqParamsJsonStrAndAssert.split(",assert=");
        String reqParamsJsonStr = reqParamsJsonStrAndAssertArray[0];
        log.debug(reqParamsJsonStr);
        JsonNode reqParamsJson = JacksonUtils.toJsonNode(reqParamsJsonStr);
        String deviceId = null;
        JsonNode deviceIdjSon = reqParamsJson.get("deviceId");
        if (deviceIdjSon != null) {
            deviceId = deviceIdjSon.asText();
        }
        String uuidOld = null;
        JsonNode uuidOldjSon = reqParamsJson.get("uuidOld");
        if (uuidOldjSon != null) {
            uuidOld = uuidOldjSon.asText();
        }
        String uuidNew = null;
        JsonNode uuidNewjSon = reqParamsJson.get("uuidNew");
        if (uuidNewjSon != null) {
            uuidNew = uuidNewjSon.asText();
        }
        JsonNode assertJson = null;
        if (reqParamsJsonStrAndAssertArray.length == 2) {
            assertJson = com.fastjrun.utils.JacksonUtils.toJsonNode(reqParamsJsonStrAndAssertArray[1]);
        }
        User response = null;
        if (assertJson != null) {
            JsonNode codeNode = assertJson.get("code");
            if (codeNode != null) {
                try {
                    coreUserService.autoLogin(deviceId, uuidOld, uuidNew);
                } catch (ServiceException e) {
                    org.testng.Assert.assertEquals(e.getCode(), codeNode.asText(),
                            ("返回消息码不是指定消息码:" + codeNode
                                    .asText()));
                }
            } else {
                response = coreUserService.autoLogin(deviceId, uuidOld, uuidNew);
            }
        } else {
            response = coreUserService.autoLogin(deviceId, uuidOld, uuidNew);
        }

        log.info(response);

    }
}

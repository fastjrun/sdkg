/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.sdkg.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.sdkg.demo.entity.User;
import com.fastjrun.test.AbstractAdVancedTestNGSpringContextTest;
import com.fastjrun.utils.JacksonUtils;

public class CoreUserServiceTest extends AbstractAdVancedTestNGSpringContextTest {

    @Autowired
    CoreUserService coreUserService;

    @Test(dataProvider = "loadParam", priority = 1)
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
        coreUserService.checkLoign(uuid, deviceId);

    }

    @Test(dataProvider = "loadParam", priority = 1)
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
        User response = coreUserService.autoLogin(deviceId, uuidOld, uuidNew);
        log.info(response);

    }
}

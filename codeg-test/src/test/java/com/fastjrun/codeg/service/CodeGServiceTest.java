/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.service.impl.DefaultCodeGService;
import com.fastjrun.codeg.test.AbstractTestNGTest;
import org.testng.annotations.Test;

public class CodeGServiceTest extends AbstractTestNGTest implements CodeGConstants {

    @Test(dataProvider = "loadParam")
    public void testGenerateAPI(String reqParamsJsonStrAndAssert) {
        JsonNode[] jsonNodes = this.parseStr2JsonArray(reqParamsJsonStrAndAssert);
        String bundleFiles = jsonNodes[0].get("bundleFiles").asText();
        String moduleName = jsonNodes[0].get("moduleName").asText();
        String packagePrefix = jsonNodes[0].get("packagePrefix").asText();
        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setPackageNamePrefix(packagePrefix);
        try {
            codeGService.generateAPI(bundleFiles, moduleName);
        } catch (Exception e) {
            this.processExceptionInResponse(jsonNodes[1], e);

        }
    }

    @Test(dataProvider = "loadParam")
    public void testGenerateBundle(String reqParamsJsonStrAndAssert) {
        JsonNode[] jsonNodes = this.parseStr2JsonArray(reqParamsJsonStrAndAssert);
        String bundleFiles = jsonNodes[0].get("bundleFiles").asText();
        String moduleName = jsonNodes[0].get("moduleName").asText();
        String packagePrefix = jsonNodes[0].get("packagePrefix").asText();
        boolean supportServiceTest = jsonNodes[0].get("supportServiceTest").asBoolean();
        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setPackageNamePrefix(packagePrefix);
        try {
            codeGService.generateProvider(bundleFiles, moduleName, supportServiceTest);
        } catch (Exception e) {
            this.processExceptionInResponse(jsonNodes[1], e);
        }

    }

    @Test(dataProvider = "loadParam")
    public void testGenerateBundleMock(String reqParamsJsonStrAndAssert) {
        JsonNode[] jsonNodes = this.parseStr2JsonArray(reqParamsJsonStrAndAssert);
        String bundleFiles = jsonNodes[0].get("bundleFiles").asText();
        String moduleName = jsonNodes[0].get("moduleName").asText();
        String packagePrefix = jsonNodes[0].get("packagePrefix").asText();
        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setPackageNamePrefix(packagePrefix);
        try {
            codeGService.generateProviderMock(bundleFiles, moduleName, MockModel.MockModel_Swagger);
        } catch (Exception e) {
            this.processExceptionInResponse(jsonNodes[1], e);
        }
    }

    @Test(dataProvider = "loadParam")
    public void testGenerateClient(String reqParamsJsonStrAndAssert) {
        JsonNode[] jsonNodes = this.parseStr2JsonArray(reqParamsJsonStrAndAssert);
        String bundleFiles = jsonNodes[0].get("bundleFiles").asText();
        String moduleName = jsonNodes[0].get("moduleName").asText();
        String packagePrefix = jsonNodes[0].get("packagePrefix").asText();
        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setPackageNamePrefix(packagePrefix);
        try {
            codeGService.generateClient(bundleFiles, moduleName);
        } catch (Exception e) {
            this.processExceptionInResponse(jsonNodes[1], e);
        }

    }

    @Test(dataProvider = "loadParam")
    public void testGenerateBase(String reqParamsJsonStrAndAssert) {
        JsonNode[] jsonNodes = this.parseStr2JsonArray(reqParamsJsonStrAndAssert);
        String sqlFile = jsonNodes[0].get("sqlFile").asText();
        boolean supportController = jsonNodes[0].get("supportController").asBoolean();
        boolean supportTest = jsonNodes[0].get("supportTest").asBoolean();
        String moduleName = jsonNodes[0].get("moduleName").asText();
        String packagePrefix = jsonNodes[0].get("packagePrefix").asText();
        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setPackageNamePrefix(packagePrefix);
        try {
            codeGService.generateBase(sqlFile, moduleName, supportTest, supportController);
        } catch (Exception e) {
            this.processExceptionInResponse(jsonNodes[1], e);
        }
    }

    @Test(dataProvider = "loadParam")
    public void testGenerateBaseByMybatisVersion(String reqParamsJsonStrAndAssert) {
        JsonNode[] jsonNodes = this.parseStr2JsonArray(reqParamsJsonStrAndAssert);
        String sqlFile = jsonNodes[0].get("sqlFile").asText();
        boolean supportController = jsonNodes[0].get("supportController").asBoolean();
        boolean supportTest = jsonNodes[0].get("supportTest").asBoolean();
        String moduleName = jsonNodes[0].get("moduleName").asText();
        String packagePrefix = jsonNodes[0].get("packagePrefix").asText();
        String mybatisVersion = jsonNodes[0].get("mybatisVersion").asText();
        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setPackageNamePrefix(packagePrefix);
        try {
            codeGService.generateBase(sqlFile, moduleName, supportController, supportTest,mybatisVersion);
        } catch (Exception e) {
            this.processExceptionInResponse(jsonNodes[1], e);
        }
    }

}

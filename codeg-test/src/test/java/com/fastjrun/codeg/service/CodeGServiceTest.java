package com.fastjrun.codeg.service;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.service.impl.DefaultCodeGService;
import com.fastjrun.codeg.test.AbstractTestNGTest;

public class CodeGServiceTest extends AbstractTestNGTest implements CodeGConstants {

    @BeforeClass
    @org.testng.annotations.Parameters({
            "envName"
    })
    public void setup(@Optional("local") String envName) {
        this.initParam(envName);
    }

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
        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setPackageNamePrefix(packagePrefix);
        try {
            codeGService.generateBundle(bundleFiles, moduleName, MockModel.MockModel_Common);
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
            codeGService.generateBundle(bundleFiles, moduleName, MockModel.MockModel_Swagger);
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
        String moduleName = jsonNodes[0].get("moduleName").asText();
        String packagePrefix = jsonNodes[0].get("packagePrefix").asText();
        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setPackageNamePrefix(packagePrefix);
        try {
            codeGService.generateBase(sqlFile, moduleName);
        } catch (Exception e) {
            this.processExceptionInResponse(jsonNodes[1], e);
        }
    }

}

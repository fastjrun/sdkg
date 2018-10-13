package com.fastjrun.codeg.service;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.service.impl.DefaultCodeGService;

public class CodeGServiceTest implements CodeGConstants {

    CodeGService codeGGenericService;

    @BeforeTest
    public void setUp() {
        String packagePrefix = "com.fastjrun.sdkg.demo.";
        String genericBundleFiles = "generic-client.xml,app-client.xml,api-client.xml";
        DefaultCodeGService defaultGenericCodeGService = new DefaultCodeGService();
        defaultGenericCodeGService.setBundleFiles(genericBundleFiles.split(","));
        defaultGenericCodeGService.setPackageNamePrefix(packagePrefix);
        codeGGenericService = defaultGenericCodeGService;
    }

    @Test
    public void generateAPI() {
        String moduleName = "sdkg-demo/demo-api";
        codeGGenericService.generateAPI(moduleName);
    }

    @Test
    public void generateBundle() {
        String moduleName = "sdkg-demo/demo-bundle";
        codeGGenericService.generateProvider(moduleName);
    }

    @Test
    public void generateBundleMock() {
        String moduleName = "sdkg-demo/demo-bundle-mock";
        MockModel mockModel = MockModel.MockModel_Swagger;
        codeGGenericService.generateBundle(moduleName, mockModel);
    }
}

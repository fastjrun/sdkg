package com.fastjrun.codeg.service;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.service.impl.DefaultCodeGService;

public class CodeGServiceTest implements CodeGConstants {

    CodeGService codeGAppService;

    CodeGService codeGApiService;

    CodeGService codeGGenericService;

    @BeforeTest
    public void setUp() {
        String appBundleFiles = "app-client.xml";
        String packagePrefix = "com.fastjrun.sdkg.demo.";
        DefaultCodeGService defaultAppCodeGService = new DefaultCodeGService();
        defaultAppCodeGService.setBundleFiles(appBundleFiles.split(","));
        defaultAppCodeGService.setPackageNamePrefix(packagePrefix);
        codeGAppService = defaultAppCodeGService;
        String apiBundleFiles = "api-client.xml";
        DefaultCodeGService defaultApiCodeGService = new DefaultCodeGService();
        defaultApiCodeGService.setBundleFiles(apiBundleFiles.split(","));
        defaultApiCodeGService.setPackageNamePrefix(packagePrefix);
        codeGApiService = defaultApiCodeGService;
        String genericBundleFiles = "generic-client.xml";
        DefaultCodeGService defaultGenericCodeGService = new DefaultCodeGService();
        defaultGenericCodeGService.setBundleFiles(genericBundleFiles.split(","));
        defaultGenericCodeGService.setPackageNamePrefix(packagePrefix);
        codeGGenericService = defaultApiCodeGService;
    }

    @Test
    public void generateAPI() {
        String moduleName = "sdkg-demo/demo-api";
        codeGGenericService.generateAPI(moduleName, RPC_TYPE_DEFAULT, CONTROLLER_TYPE_DEFAULT);
    }

    @Test
    public void generateBundle() {
        String moduleName = "sdkg-demo/demo-bundle";
        codeGGenericService.generateProvider(moduleName, RPC_TYPE_DEFAULT, CONTROLLER_TYPE_DEFAULT);
    }

    @Test
    public void generateBundleMock() {
        String moduleName = "sdkg-demo/demo-bundle-mock";
        MockModel mockModel = MockModel.MockModel_Swagger;
        codeGGenericService.generateBundle(moduleName, RPC_TYPE_DEFAULT, CONTROLLER_TYPE_DEFAULT, mockModel);
    }

    @Test
    public void generateAPIAPI() {
        String moduleName = "sdkg-demo/demo-app-api";
        codeGApiService.generateAPI(moduleName, RPC_TYPE_DEFAULT, ControllerType.ControllerType_API);
    }

    @Test
    public void generateAPIBundle() {
        String moduleName = "sdkg-demo/demo-app-bundle";
        codeGApiService.generateProvider(moduleName, RPC_TYPE_DEFAULT, ControllerType.ControllerType_API);
    }

    @Test
    public void generateAPIBundleMock() {
        String moduleName = "sdkg-demo/demo-app-bundle-mock";
        MockModel mockModel = MockModel.MockModel_Swagger;
        codeGApiService.generateBundle(moduleName, RPC_TYPE_DEFAULT, ControllerType.ControllerType_API, mockModel);
    }

    @Test
    public void generateAPPAPI() {
        String moduleName = "sdkg-demo/demo-app-api";
        codeGAppService.generateAPI(moduleName, RPC_TYPE_DEFAULT, ControllerType.ControllerType_APP);
    }

    @Test
    public void generateAPPBundle() {
        String moduleName = "sdkg-demo/demo-app-bundle";
        codeGAppService.generateProvider(moduleName, RPC_TYPE_DEFAULT, ControllerType.ControllerType_APP);
    }

    @Test
    public void generateAPPBundleMock() {
        String moduleName = "sdkg-demo/demo-app-bundle-mock";
        MockModel mockModel = MockModel.MockModel_Swagger;
        codeGAppService.generateBundle(moduleName, RPC_TYPE_DEFAULT, ControllerType.ControllerType_APP, mockModel);
    }
}

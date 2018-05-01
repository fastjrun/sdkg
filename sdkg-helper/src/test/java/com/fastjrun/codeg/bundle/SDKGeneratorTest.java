package com.fastjrun.codeg.bundle;

import org.junit.Test;

public class SDKGeneratorTest {
    @Test
    public void testProcessAppSDK() {
        String moduleName = "Example";
        SDKGenerator sdkGenerator = new SDKGenerator();
        sdkGenerator.setModuleName(moduleName);
        sdkGenerator.setBundleFiles("../app-client.xml".split(","));
        sdkGenerator.setPackageNamePrefix("com.fastjrun.common.");
        sdkGenerator.setAppName("app");
        sdkGenerator.generate();
    }

    @Test
    public void testProcessApiSDK() {
        String moduleName = "Example";
        SDKGenerator sdkGenerator = new SDKGenerator();
        sdkGenerator.setModuleName(moduleName);
        sdkGenerator.setBundleFiles("../api-client.xml".split(","));
        sdkGenerator.setPackageNamePrefix("com.fastjrun.common.");
        sdkGenerator.setAppName("api");
        sdkGenerator.generate();
    }

    @Test
    public void testProcessGenericSDK() {
        String moduleName = "Example";
        SDKGenerator sdkGenerator = new SDKGenerator();
        sdkGenerator.setModuleName(moduleName);
        sdkGenerator.setBundleFiles("../generic-client.xml".split(","));
        sdkGenerator.setPackageNamePrefix("com.fastjrun.common.");
        sdkGenerator.setAppName("app");
        sdkGenerator.generate();
    }

}

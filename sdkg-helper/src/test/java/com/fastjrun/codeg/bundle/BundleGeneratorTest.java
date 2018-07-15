package com.fastjrun.codeg.bundle;

import org.junit.Test;

public class BundleGeneratorTest {
    @Test
    public void testProcessAppSDK() {
        String moduleName = "Example";
        BundleGenerator bundleGenerator = new BundleGenerator();
        bundleGenerator.setModuleName(moduleName);
        bundleGenerator.setBundleFiles("../app-client.xml".split(","));
        bundleGenerator.setPackageNamePrefix("com.fastjrun.common.");
        bundleGenerator.generate();
    }

    @Test
    public void testGenerateBundle() {
        String moduleName = "Example";
        BundleGenerator bundleGenerator = new BundleGenerator();
        bundleGenerator.setModuleName(moduleName);
        bundleGenerator.setBundleFiles("../generic-client.xml,../api-client.xml,../app-client.xml".split(","));
        bundleGenerator.setPackageNamePrefix("com.fastjrun.common.");
        bundleGenerator.generate();
    }

    @Test
    public void testGenerateSDK() {
        String moduleName = "Example";
        SDKGenerator sdkGenerator = new SDKGenerator();
        sdkGenerator.setModuleName(moduleName);
        sdkGenerator.setAppName("apiwolrd");
        sdkGenerator.setBundleFiles("../generic-client.xml".split(","));
        sdkGenerator.setAuthor("崔莹峰");
        sdkGenerator.setPackageNamePrefix("com.fastjrun.common.");
        sdkGenerator.generate();
    }

    @Test
    public void testGenerateBundleMock() {
        String moduleName = "Example";
        BundleGenerator bundleGenerator = new BundleGenerator();
        bundleGenerator.setMock(true);
        bundleGenerator.setModuleName(moduleName);
        bundleGenerator.setBundleFiles("../generic-client.xml".split(","));
        bundleGenerator.setAuthor("崔莹峰");
        bundleGenerator.setPackageNamePrefix("com.fastjrun.common.");
        bundleGenerator.generate();
    }

}

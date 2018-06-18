package com.fastjrun.codeg.bundle;

import org.junit.Test;

public class BundleMockGeneratorTest {
    @Test
    public void testProcessAppSDK() {
        String moduleName = "Example";
        BundleMockGenerator bundleMockGenerator = new BundleMockGenerator();
        bundleMockGenerator.setModuleName(moduleName);
        bundleMockGenerator.setBundleFiles("../app-client.xml".split(","));
        bundleMockGenerator.setPackageNamePrefix("com.fastjrun.common.");
        bundleMockGenerator.generate();
    }

    @Test
    public void testProcessApiSDK() {
        String moduleName = "Example";
        BundleMockGenerator bundleMockGenerator = new BundleMockGenerator();
        bundleMockGenerator.setModuleName(moduleName);
        bundleMockGenerator.setBundleFiles("../api-client.xml".split(","));
        bundleMockGenerator.setPackageNamePrefix("com.fastjrun.common.");
        bundleMockGenerator.generate();
    }

    @Test
    public void testProcessGenericSDK() {
        String moduleName = "Example";
        BundleMockGenerator bundleMockGenerator = new BundleMockGenerator();
        bundleMockGenerator.setModuleName(moduleName);
        bundleMockGenerator.setBundleFiles("../generic-client.xml".split(","));
        bundleMockGenerator.setAuthor("崔莹峰");
        bundleMockGenerator.setPackageNamePrefix("com.fastjrun.share.giz_openapi_apps.sdk.");
        bundleMockGenerator.generate();
    }

}

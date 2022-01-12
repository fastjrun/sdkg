/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.codeg;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.service.impl.DefaultCodeGService;
import com.fastjrun.test.base.AbstractTestNGTest;
import org.testng.annotations.Test;

import java.util.Map;

public class EladminCodeGTest extends AbstractTestNGTest implements CodeGConstants {

  @Test(dataProvider = "loadParam")
  public void testGenerateAPI(Map<String, Object> testPOMap) {
    String bundleFiles = testPOMap.get("bundleFiles").toString();
    String moduleName = testPOMap.get("moduleName").toString();
    String packagePrefix = testPOMap.get("packagePrefix").toString();
    DefaultCodeGService codeGService = new DefaultCodeGService();
    codeGService.setPackageNamePrefix(packagePrefix);
    try {
      codeGService.generateAPI(bundleFiles, moduleName);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test(dataProvider = "loadParam")
  public void testGenerateBundle(Map<String, Object> testPOMap) {
    String bundleFiles = testPOMap.get("bundleFiles").toString();
    String moduleName = testPOMap.get("moduleName").toString();
    String packagePrefix = testPOMap.get("packagePrefix").toString();
    DefaultCodeGService codeGService = new DefaultCodeGService();
    codeGService.setPackageNamePrefix(packagePrefix);
    try {
      codeGService.generateProvider(bundleFiles, moduleName);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test(dataProvider = "loadParam")
  public void testGenerateBundleMock(Map<String, Object> testPOMap) {
    String bundleFiles = testPOMap.get("bundleFiles").toString();
    String moduleName = testPOMap.get("moduleName").toString();
    String packagePrefix = testPOMap.get("packagePrefix").toString();
    DefaultCodeGService codeGService = new DefaultCodeGService();
    codeGService.setPackageNamePrefix(packagePrefix);
    try {
      codeGService.generateProviderMock(bundleFiles, moduleName, MockModel.MockModel_Swagger);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test(dataProvider = "loadParam")
  public void testGenerateClient(Map<String, Object> testPOMap) {
    String bundleFiles = testPOMap.get("bundleFiles").toString();
    String moduleName = testPOMap.get("moduleName").toString();
    String packagePrefix = testPOMap.get("packagePrefix").toString();
    DefaultCodeGService codeGService = new DefaultCodeGService();
    codeGService.setPackageNamePrefix(packagePrefix);
    try {
      codeGService.generateClient(bundleFiles, moduleName);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

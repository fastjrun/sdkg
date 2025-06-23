package com.fastjrun.codeg.service;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.service.impl.DefaultCodeGService;
import org.testng.annotations.Test;

public class DefaultCodeGServiceTest {

    @Test
    public void testGenerateAPI() {
    }

    @Test
    public void testGenerateMybatisPlusCode() {
    }

    @Test
    public void testGenerateProvider() {
    }

    @Test
    public void testGenerateProviderMock() {
        String xmlFile = "fast-demo-api.xml";
        String outputDir = "codeg-test/cdemo-provider-mock";

        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setPackageNamePrefix("com.fastjrun.example.");
        codeGService.generateProviderMock(xmlFile,outputDir, CodeGConstants.SwaggerVersion.Swagger3);
    }

    @Test
    public void testGenerateMybatisPlus() {
    }
}

package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.generator.method.BaseHTTPMethodGenerator;

import java.util.List;
import java.util.Properties;

public abstract class BaseHTTPGenerator extends BaseControllerGenerator {

    @Override
    public void processProviderModule() {
        this.genreateControllerPath();
        this.processService();
        if (this.getMockModel() != MockModel.MockModel_Common) {
            this.processServiceMock();
        }
        this.processController();

        List<CommonMethod> commonMethods = this.commonController.getService().getMethods();
        for (CommonMethod commonMethod : commonMethods) {
            BaseHTTPMethodGenerator baseHTTPMethodGenerator = null;
            try {
                baseHTTPMethodGenerator = (BaseHTTPMethodGenerator) this.baseControllerMethodGenerator.clone();
                baseHTTPMethodGenerator.setCommonMethod(commonMethod);
            } catch (CloneNotSupportedException e) {
                throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持这个生成器", e);
            }
            baseHTTPMethodGenerator.processServiceMethod(this.serviceClass);
            if (this.getMockModel() != MockModel.MockModel_Common) {
                baseHTTPMethodGenerator.processServiceMockMethod(this.serviceMockClass);
            }
            baseHTTPMethodGenerator.processControllerMethod(commonController, this.controlllerClass);
        }
    }

    @Override
    public void processApiModule() {
        this.genreateControllerPath();
        this.processService();
        this.processClient();
        this.processClientTest();
        this.clientTestParam = new Properties();

        List<CommonMethod> commonMethods = this.commonController.getService().getMethods();
        for (CommonMethod commonMethod : commonMethods) {
            BaseHTTPMethodGenerator baseHTTPMethodGenerator = null;
            try {
                baseHTTPMethodGenerator = (BaseHTTPMethodGenerator) this.baseControllerMethodGenerator.clone();
                baseHTTPMethodGenerator.setCommonMethod(commonMethod);
            } catch (CloneNotSupportedException e) {
                throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持这个生成器", e);
            }
            baseHTTPMethodGenerator.processServiceMethod(this.serviceClass);
            baseHTTPMethodGenerator.processClientMethod(this.controllerPath, this.clientClass);
            baseHTTPMethodGenerator.processClientTestMethod(this.clientTestClass);
            StringBuilder sb = new StringBuilder(this.clientName).append(".test");
            sb.append(baseHTTPMethodGenerator.getMethodName()).append(".n");
            baseHTTPMethodGenerator.processClientTestPraram();
            this.clientTestParam.put(sb.toString(), baseHTTPMethodGenerator.getMethodParamInJsonObject()
                    .toString().replaceAll("\n", "").replaceAll("\r", "")
                    .trim());
        }

    }

}
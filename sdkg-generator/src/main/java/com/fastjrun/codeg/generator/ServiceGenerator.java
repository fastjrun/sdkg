/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.generator.common.BaseCMGenerator;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.fastjrun.helper.StringHelper;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMod;

import java.util.HashMap;
import java.util.Map;

public class ServiceGenerator extends BaseCMGenerator {

    static    String        SERVICETEST_SUFFIX = "Test";
    protected CommonService commonService;
    protected JDefinedClass serviceClass;
    protected JDefinedClass serviceTestClass;
    protected JDefinedClass serviceMockClass;
    private   boolean       supportTest        = false;

    private Map<CommonMethod, ServiceMethodGenerator> serviceMethodGeneratorMap;

    public Map<CommonMethod, ServiceMethodGenerator> getServiceMethodGeneratorMap() {
        return serviceMethodGeneratorMap;
    }

    public JDefinedClass getServiceMockClass() {
        return serviceMockClass;
    }

    public void setServiceMockClass(JDefinedClass serviceMockClass) {
        this.serviceMockClass = serviceMockClass;
    }

    public CommonService getCommonService() {
        return commonService;
    }

    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    public JDefinedClass getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(JDefinedClass serviceClass) {
        this.serviceClass = serviceClass;
    }

    public boolean isSupportTest() {
        return supportTest;
    }

    public void setSupportTest(boolean supportTest) {
        this.supportTest = supportTest;
    }

    public JDefinedClass getServiceTestClass() {
        return serviceTestClass;
    }

    public void setServiceTestClass(JDefinedClass serviceTestClass) {
        this.serviceTestClass = serviceTestClass;
    }

    protected void processService() {
        try {
            this.serviceClass =
              cm._class(this.packageNamePrefix + SERVICE_PACKAGE_NAME + commonService.get_class(),
                EClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceClass);
    }

    protected JFieldVar processServiceTest() {
        try {
            this.serviceTestClass = cmTest._class(
              this.packageNamePrefix + SERVICE_PACKAGE_NAME + commonService.get_class() + SERVICETEST_SUFFIX);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + SERVICETEST_SUFFIX + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        this.serviceTestClass._extends(
          cmTest.ref("com.fastjrun.test" + ".AbstractAdVancedTestNGSpringContextTest"));
        this.addClassDeclaration(this.serviceTestClass);
        String lowerCaseFirstOneClassName =
          StringHelper.toLowerCaseFirstOne(this.serviceClass.name());
        JFieldVar fieldVar =
          serviceTestClass.field(JMod.PRIVATE, this.serviceClass, lowerCaseFirstOneClassName);
        fieldVar.annotate(cmTest.ref("org.springframework.beans.factory.annotation.Autowired"));
        return fieldVar;
    }

    protected void processServiceMock() {
        try {
            this.serviceMockClass =
              cm._class(MOCK_PACKAGE_NAME + commonService.get_class() + "Mock");
            serviceMockClass._implements(this.serviceClass);
            serviceMockClass.annotate(cm.ref("org.springframework.stereotype.Service")).param(
              "value", commonService.getName());
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceMockClass);
    }

    @Override
    public void generate() {
        JFieldVar fieldVar = null;
        if (!this.isApi()) {
            this.processService();
            if (this.supportTest) {
                fieldVar = this.processServiceTest();
            }
            if (!this.isClient()) {
                if (this.mockModel != CodeGConstants.MockModel.MockModel_Common) {
                    this.processServiceMock();
                }
            }
        }

        this.serviceMethodGeneratorMap = new HashMap<>();
        for (CommonMethod commonMethod : this.commonService.getMethods()) {
            ServiceMethodGenerator serviceMethodGenerator = new ServiceMethodGenerator();
            serviceMethodGenerator.setPackageNamePrefix(packageNamePrefix);
            serviceMethodGenerator.setMockModel(mockModel);
            serviceMethodGenerator.setAuthor(author);
            serviceMethodGenerator.setCompany(company);
            serviceMethodGenerator.setApi(this.isApi());
            serviceMethodGenerator.setClient(this.isClient());
            serviceMethodGenerator.setServiceGenerator(this);
            serviceMethodGenerator.setCommonMethod(commonMethod);
            serviceMethodGenerator.setCm(cm);
            serviceMethodGenerator.setCmTest(cmTest);
            serviceMethodGenerator.setFieldVar(fieldVar);
            serviceMethodGenerator.generate();
            this.serviceMethodGeneratorMap.put(commonMethod, serviceMethodGenerator);
        }
    }
}
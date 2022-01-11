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
import com.fastjrun.codeg.helper.StringHelper;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMod;

import java.util.HashMap;
import java.util.Map;

public class ServiceGenerator extends BaseCMGenerator {

    protected CommonService commonService;
    protected JDefinedClass serviceClass;
    protected JDefinedClass serviceMockClass;

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

    protected void processService() {
        try {
            this.serviceClass =
              cm._class(this.packageNamePrefix + BaseCMGenerator.SERVICE_PACKAGE_NAME + commonService.get_class(),
                EClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceClass);
    }

    protected void processServiceMock() {
        try {
            this.serviceMockClass =
              cm._class(BaseCMGenerator.MOCK_PACKAGE_NAME + commonService.get_class() + "Mock");
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
            serviceMethodGenerator.setMockHelperClassName(mockHelperClassName);
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
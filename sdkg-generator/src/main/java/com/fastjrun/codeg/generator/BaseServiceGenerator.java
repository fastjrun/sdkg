/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.generator.common.BaseCMGenerator;
import com.fastjrun.codeg.generator.method.BaseServiceMethodGenerator;
import com.helger.jcodemodel.*;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseServiceGenerator extends BaseCMGenerator {

    protected CommonService commonService;
    protected JDefinedClass serviceClass;
    protected JDefinedClass serviceMockClass;
    protected String  mockHelperName;
    protected String  pageResultName;
    protected String  serviceGeneratorName;

    public String getMockHelperName() {
        return mockHelperName;
    }

    public void setMockHelperName(String mockHelperName) {
        this.mockHelperName = mockHelperName;
    }

    public String getPageResultName() {
        return pageResultName;
    }

    public void setPageResultName(String pageResultName) {
        this.pageResultName = pageResultName;
    }

    protected abstract void init();

    protected Map<CommonMethod, BaseServiceMethodGenerator> serviceMethodGeneratorMap;

    public Map<CommonMethod, BaseServiceMethodGenerator> getServiceMethodGeneratorMap() {
        return serviceMethodGeneratorMap;
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

    public JDefinedClass getServiceMockClass() {
        return serviceMockClass;
    }

    public void setServiceMockClass(JDefinedClass serviceMockClass) {
        this.serviceMockClass = serviceMockClass;
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
                    "value", this.commonService.getName());
        } catch (JClassAlreadyExistsException e) {
            String msg = this.commonService.get_class() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceMockClass);
    }

    @Override
    public void generate() {
        this.init();
        if (!this.isApi()) {
            this.processService();
            if (!this.isClient()) {
                if (mockModel != MockModel.MockModel_Common) {
                    this.processServiceMock();
                }
            }
        }

        this.serviceMethodGeneratorMap = new HashMap<>();
        for (CommonMethod commonMethod : this.commonService.getMethods()) {
            BaseServiceMethodGenerator serviceMethodGenerator = null;
            try {
                serviceMethodGenerator = (BaseServiceMethodGenerator)Class.forName(this.serviceGeneratorName).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            serviceMethodGenerator.setPackageNamePrefix(packageNamePrefix);
            serviceMethodGenerator.setMockModel(mockModel);
            serviceMethodGenerator.setAuthor(author);
            serviceMethodGenerator.setCompany(company);
            serviceMethodGenerator.setApi(this.isApi());
            serviceMethodGenerator.setClient(this.isClient());
            serviceMethodGenerator.setServiceGenerator(this);
            serviceMethodGenerator.setCommonMethod(commonMethod);
            serviceMethodGenerator.setCm(cm);
            serviceMethodGenerator.generate();
            this.serviceMethodGeneratorMap.put(commonMethod, serviceMethodGenerator);
        }
    }


}
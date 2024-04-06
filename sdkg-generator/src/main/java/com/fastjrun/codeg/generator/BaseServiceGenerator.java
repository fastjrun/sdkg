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
import com.fastjrun.codeg.generator.method.DefaultServiceMethodGenerator;
import com.helger.jcodemodel.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class BaseServiceGenerator extends BaseCMGenerator {

    protected CommonService commonService;
    protected JDefinedClass serviceClass;
    protected JDefinedClass serviceMockClass;
    protected String  mockHelperName;
    protected String  pageResultName;

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
        } catch (JCodeModelException e) {
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
        } catch (JCodeModelException e) {
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
            if (mockModel != MockModel.MockModel_Common) {
                this.processServiceMock();
            }
        }
        this.serviceMethodGeneratorMap = new HashMap<>();
        for (CommonMethod commonMethod : this.commonService.getMethods()) {
            BaseServiceMethodGenerator serviceMethodGenerator = new DefaultServiceMethodGenerator();
            serviceMethodGenerator.setPackageNamePrefix(packageNamePrefix);
            serviceMethodGenerator.setMockModel(mockModel);
            serviceMethodGenerator.setAuthor(author);
            serviceMethodGenerator.setCompany(company);
            serviceMethodGenerator.setApi(this.isApi());
            serviceMethodGenerator.setServiceGenerator(this);
            serviceMethodGenerator.setCommonMethod(commonMethod);
            serviceMethodGenerator.setCm(cm);
            serviceMethodGenerator.generate();
            this.serviceMethodGeneratorMap.put(commonMethod, serviceMethodGenerator);
        }

    }


}
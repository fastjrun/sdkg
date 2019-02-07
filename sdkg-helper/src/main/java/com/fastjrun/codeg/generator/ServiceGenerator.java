package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonService;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;

public class ServiceGenerator extends BaseCMGenerator {

    static String serviceTestSuffix = "Test";
    protected CommonService commonService;
    protected JDefinedClass serviceClass;
    protected JDefinedClass serviceTestClass;
    protected JDefinedClass serviceMockClass;
    private boolean test = false;

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

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public JDefinedClass getServiceTestClass() {
        return serviceTestClass;
    }

    public void setServiceTestClass(JDefinedClass serviceTestClass) {
        this.serviceTestClass = serviceTestClass;
    }

    protected void processService() {
        try {
            this.serviceClass = cm._class(this.packageNamePrefix + this.servicePackageName + commonService.get_class(),
                    ClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceClass);
    }

    protected void processServiceTest() {
        try {
            this.serviceTestClass = cmTest._class(this.packageNamePrefix + this.servicePackageName + commonService
                            .get_class() + serviceTestSuffix,
                    ClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + serviceTestSuffix + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceTestClass);
    }

    protected void processServiceMock() {
        try {
            this.serviceMockClass = cm._class(mockPackageName + commonService.get_class() + "Mock");
            serviceMockClass._implements(this.serviceClass);
            serviceMockClass.annotate(cm.ref("org.springframework.stereotype.Service")).param("value",
                    commonService.getName());
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceMockClass);
    }

    @Override
    public void generate() {
        if (!this.isApi()) {
            if (this.test) {
                this.processServiceTest();
            } else {
                this.processService();
                if (this.mockModel != MockModel.MockModel_Common) {
                    this.processServiceMock();
                }
            }
        }
    }
}
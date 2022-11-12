/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.common;

import com.fastjrun.codeg.common.*;
import com.fastjrun.codeg.generator.BaseServiceGenerator;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseServiceMethodGenerator;
import com.helger.jcodemodel.*;

public abstract class BaseControllerGenerator extends BaseCMGenerator {

    static final String WEB_PACKAGE_NAME = "web.controller.";

    protected CommonController commonController;

    protected String clientName;

    protected JDefinedClass clientClass;

    protected JDefinedClass controlllerClass;

    protected String controllerPath;

    protected BaseServiceGenerator serviceGenerator;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getControllerPath() {
        return controllerPath;
    }

    public void setControllerPath(String controllerPath) {
        this.controllerPath = controllerPath;
    }

    public BaseServiceGenerator getServiceGenerator() {
        return serviceGenerator;
    }

    public void setServiceGenerator(BaseServiceGenerator serviceGenerator) {
        this.serviceGenerator = serviceGenerator;
    }

    public JDefinedClass getClientClass() {
        return clientClass;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    public JDefinedClass getControlllerClass() {
        return controlllerClass;
    }

    public void setControlllerClass(JDefinedClass controlllerClass) {
        this.controlllerClass = controlllerClass;
    }

    public abstract BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(
      BaseServiceMethodGenerator baseServiceMethodGenerator);

    protected void processController() {
        ControllerType controllerType = commonController.getControllerType();

        String controllerPackageName = this.packageNamePrefix + WEB_PACKAGE_NAME;

        if (this.getMockModel() != CodeGConstants.MockModel.MockModel_Common) {
            controllerPackageName = MOCK_PACKAGE_NAME + WEB_PACKAGE_NAME;
        }

        String controllerName = commonController.getName();

        try {
            this.controlllerClass = cm._class(controllerPackageName + controllerName);
            if (controllerType.providerParentName != null && !controllerType.providerParentName.equals(
              "")) {
                this.controlllerClass._extends(cm.ref(controllerType.providerParentName));
            }

        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.controlllerClass.annotate(
          cm.ref(controllerType.baseControllerName));
        this.controlllerClass.annotate(
          cm.ref("org.springframework.web.bind.annotation.RequestMapping")).param("value",
          this.controllerPath);
        this.controlllerClass.annotate(cm.ref("io.swagger.annotations.Api")).param("value",
                commonController.getRemark()).paramArray("tags", commonController.getTags());
        this.addClassDeclaration(this.controlllerClass);

        String serviceName = commonController.getServiceName();
        JFieldVar fieldVar =
          this.controlllerClass.field(JMod.PRIVATE, this.serviceGenerator.getServiceClass(),
            serviceName);
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Qualifier")).param(
          "value", commonController.getServiceRef());
    }

    protected void processClient() {
        CodeGConstants.ControllerType controllerType = commonController.getControllerType();
        this.clientName = commonController.getClientName();
        int lastDotIndex = clientName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            this.clientName = clientName.substring(lastDotIndex + 1, clientName.length());
        }

        try {
            this.clientClass = cm._class(this.getPackageNamePrefix() + "client." + this.clientName);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        AbstractJClass baseClientClass = cm.ref(controllerType.baseClient);

        AbstractJClass jParentClass =
          cm.ref("com.fastjrun.client.BaseApplicationClient").narrow(baseClientClass);

        this.addClassDeclaration(this.clientClass);

        this.clientClass._extends(jParentClass);
        this.clientClass._implements(this.serviceGenerator.getServiceClass());

        JMethod clientInitMethod = clientClass.method(JMod.PUBLIC, cm.VOID, "initSDKConfig");
        clientInitMethod.annotate(cm.ref("Override"));
        JBlock jInitBlock = clientInitMethod.body();
        jInitBlock.assign(JExpr.ref("baseClient"), JExpr._new(baseClientClass));
        jInitBlock.invoke(JExpr.ref("baseClient"), "initSDKConfig");
    }

    protected void genreateControllerPath() {
        this.controllerPath = commonController.getPath();
        String version = commonController.getVersion();
        if (version != null && !version.equals("")) {
            this.controllerPath = this.controllerPath + "/" + version;
        }
    }

    protected void generatorControllerMethod() {
        for (CommonMethod commonMethod : this.commonController.getService().getMethods()) {
            BaseServiceMethodGenerator serviceMethodGenerator =
              this.serviceGenerator.getServiceMethodGeneratorMap().get(commonMethod);
            BaseControllerMethodGenerator baseControllerMethodGenerator =
              this.prepareBaseControllerMethodGenerator(serviceMethodGenerator);
            baseControllerMethodGenerator.setApi(this.isApi());
            baseControllerMethodGenerator.setClient(this.isClient());
            baseControllerMethodGenerator.setCm(cm);
            baseControllerMethodGenerator.generate();
        }
    }
}
